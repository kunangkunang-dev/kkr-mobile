package com.kunangkunang.app.presenter

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.kunangkunang.app.R
import com.kunangkunang.app.activity.MainActivity
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.constant.Constants.CHANGE_LOCATION_SETTING_REQUEST_CODE
import com.kunangkunang.app.model.banner.Banner
import com.kunangkunang.app.model.checkout.Checkout
import com.kunangkunang.app.model.config.Config
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.history.History
import com.kunangkunang.app.model.item.Item
import com.kunangkunang.app.model.item.ItemDetails
import com.kunangkunang.app.model.login.LoginDataChecklistItem
import com.kunangkunang.app.model.login.Staff
import com.kunangkunang.app.model.logout.Logout
import com.kunangkunang.app.model.menu.Menu
import com.kunangkunang.app.model.news.News
import com.kunangkunang.app.model.review.Review
import com.kunangkunang.app.model.status.Status
import com.kunangkunang.app.model.weather.Weather
import com.kunangkunang.app.view.MainView
import kotlinx.coroutines.*

class MainPresenter(private val context: Context,
                    private val scope: CoroutineScope,
                    private val view: MainView,
                    private val repository: AppRepository) {

    private var locationCallback: LocationCallback? = null

    private val sampleMenus = intArrayOf(
        R.drawable.menu_dining,
        R.drawable.menu_laundry,
        R.drawable.menu_spa,
        R.drawable.menu_amenities
    )

    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private fun loadWeather(lat: String, lon: String): Job {
        return scope.launch {
            repository.getCurrentWeather(lat, lon, object : AppRepositoryCallback<Weather?> {
                override fun onDataLoaded(data: Weather?) {
                    view.loadWeather(data)
                }

                override fun onDataError() {
                    view.weatherFailed()
                }
            })
        }
    }

    fun loadBanner(): Job {
        return scope.launch {
            repository.getBanner(object : AppRepositoryCallback<Banner?> {
                override fun onDataLoaded(data: Banner?) {
                    view.loadBanner(data)
                }

                override fun onDataError() {
                    view.bannerFailed()
                }
            })
        }
    }

    fun loadNews(): Job {
        return scope.launch {
            repository.getNews(object : AppRepositoryCallback<News?> {
                override fun onDataLoaded(data: News?) {
                    view.loadNews(data)
                }

                override fun onDataError() {
                    view.newsFailed()
                }
            })
        }
    }

    fun loadMenu(): Job {
        return scope.launch {
            val data: MutableList<Menu?> = mutableListOf()

            val fNb = Menu("Food & Beverage", null, sampleMenus[0])
            val laundry = Menu("Laundry", null, sampleMenus[1])
            val spa = Menu("Spa", null, sampleMenus[2])
            val amenities = Menu("Product", null, sampleMenus[3])

            data.add(fNb)
            data.add(laundry)
            data.add(spa)
            data.add(amenities)

            view.loadMenu(data)
        }
    }

    @SuppressLint("MissingPermission")
    fun loadCoordinates(): Job {
        return GlobalScope.launch {
            client.lastLocation.addOnSuccessListener {
                if (it != null) {
                    loadWeather(it.latitude.toString(), it.longitude.toString())
                } else {
                    setLocationCallback()
                    verifyLocationSetting()
                }
            }
        }
    }

    fun checkGpsPermission() {
        if (context.checkSelfPermission(permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as MainActivity, arrayOf(permission.ACCESS_FINE_LOCATION), Constants.ACCESS_FINE_LOCATION_REQUEST_CODE)
        } else {
            if (isLocationEnabled()) {
                loadCoordinates()
            } else{
                turnOnGPSManually()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun turnOnGPSManually() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    fun logOut(roomId: Int, password: String): Job {
        return scope.launch {
            repository.logOut(roomId, password, object : AppRepositoryCallback<Logout?> {
                override fun onDataLoaded(data: Logout?) {
                    view.notifyLogoutStatus(data)
                }

                override fun onDataError() {
                    view.logOutFailed()
                }
            })
        }
    }

    fun loadCustomerInfo(id: Int): Job {
        return scope.launch {
            repository.getCustomerInfo(id, object : AppRepositoryCallback<Customer?> {
                override fun onDataLoaded(data: Customer?) {
                    view.loadCustomerInfo(data)
                }

                override fun onDataError() {
                    view.customerFailed()
                }

            })
        }
    }


    fun checkOut(checkout: Checkout): Job {
        return scope.launch {
            repository.checkOut(checkout, object : AppRepositoryCallback<Status?> {
                override fun onDataLoaded(data: Status?) {
                    view.notifyCheckoutStatus(data)
                }

                override fun onDataError() {
                    view.checkOutFailed()
                }
            })
        }
    }

    fun generateItemChecklist(roomId: Int,
                              checkinId: Int,
                              employeeName: String,
                              data: List<LoginDataChecklistItem?>) : Item {
        val details: MutableList<ItemDetails> = mutableListOf()

        for (item in data) {
            item?.let {

                val available: Int = if (it.state) {
                    1
                } else {
                    0
                }

                val itemDetail = ItemDetails(it.id, it.checklistId, it.itemName, it.quantity, it.description, available)
                details.add(itemDetail)
            }
        }

        return Item(roomId, checkinId, employeeName, details)
    }

    fun submitItem(item: Item): Job {
        return scope.launch {
            repository.submitItem(item, object : AppRepositoryCallback<Status?> {
                override fun onDataLoaded(data: Status?) {
                    view.notifySubmitItemStatus(data)
                }

                override fun onDataError() {
                    view.submitItemFailed()
                }
            })
        }
    }

    fun loadConfig(): Job {
        return scope.launch {
            repository.getConfig(object : AppRepositoryCallback<Config?> {
                override fun onDataLoaded(data: Config?) {
                    view.loadConfig(data)
                }

                override fun onDataError() {
                    view.configFailed()
                }
            })
        }
    }

    fun verifyPassword(password: String): Job {
        return scope.launch {
            repository.verifyPassword(password, object : AppRepositoryCallback<Status?> {
                override fun onDataLoaded(data: Status?) {
                    view.notifyPasswordStatus(data)
                }

                override fun onDataError() {
                    view.passwordFailed()
                }
            })
        }
    }

    fun staffLogin(email: String, password: String): Job{
        return scope.launch {
            repository.staffLogin(email, password, object : AppRepositoryCallback<Staff?> {
                override fun onDataLoaded(data: Staff?) {
                    view.notifyStaffStatus(data)
                }

                override fun onDataError() {
                    view.staffFailed()
                }
            })
        }
    }

    fun loadHistory(roomId: Int, customerId: Int): Job {
        return scope.launch {
            repository.getHistory(roomId, customerId, object : AppRepositoryCallback<History?> {
                override fun onDataLoaded(data: History?) {
                    view.loadHistory(data)
                }

                override fun onDataError() {
                    view.historyFailed()
                }
            })
        }
    }

    private fun setLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for (location in it.locations) {
                        loadWeather(location.latitude.toString(), location.longitude.toString())
                    }
                }
            }
        }
    }

    private fun verifyLocationSetting() {
        val builder = LocationSettingsRequest.Builder()
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener(context as MainActivity) { startLocationUpdates(generateLocationRequest()) }
        task.addOnFailureListener(context) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(context, CHANGE_LOCATION_SETTING_REQUEST_CODE)
                } catch (sendEx: SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(locationRequest: LocationRequest?) {
        client.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun generateLocationRequest(): LocationRequest? {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            client.removeLocationUpdates(locationCallback)
        }
    }

    fun requestHelp(roomName: String): Job {
        return scope.launch {
            repository.requestHelp(roomName, object : AppRepositoryCallback<Status?> {
                override fun onDataLoaded(data: Status?) {
                    view.notifyHelpStatus(data)
                }

                override fun onDataError() {
                    view.requestHelpFailed()
                }
            })
        }
    }

    fun submitReview(review: Review): Job {
        return scope.launch {
            repository.submitReview(review, object : AppRepositoryCallback<Status?> {
                override fun onDataLoaded(data: Status?) {
                    view.notifyReviewStatus(data)
                }

                override fun onDataError() {
                    view.submitReviewFailed()
                }
            })
        }
    }
 }