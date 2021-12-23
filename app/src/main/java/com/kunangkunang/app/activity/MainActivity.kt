package com.kunangkunang.app.activity

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.UserManager
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.R
import com.kunangkunang.app.adapter.ChecklistAdapter
import com.kunangkunang.app.adapter.HistoryAdapter
import com.kunangkunang.app.adapter.MenuAdapter
import com.kunangkunang.app.adapter.NewsAdapter
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.*
import com.kunangkunang.app.model.banner.Banner
import com.kunangkunang.app.model.banner.BannerData
import com.kunangkunang.app.model.config.Config
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.history.History
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.login.Staff
import com.kunangkunang.app.model.logout.Logout
import com.kunangkunang.app.model.menu.Menu
import com.kunangkunang.app.model.news.News
import com.kunangkunang.app.model.news.NewsData
import com.kunangkunang.app.model.review.Review
import com.kunangkunang.app.model.status.Status
import com.kunangkunang.app.model.weather.Weather
import com.kunangkunang.app.presenter.MainPresenter
import com.kunangkunang.app.view.MainView
import com.synnapps.carouselview.ImageListener
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_checkin_info.view.*
import kotlinx.android.synthetic.main.dialog_checklist.view.*
import kotlinx.android.synthetic.main.dialog_checkout.view.*
import kotlinx.android.synthetic.main.dialog_help_message.view.*
import kotlinx.android.synthetic.main.dialog_history.view.*
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.android.synthetic.main.dialog_logout.view.et_dialog_pasword
import kotlinx.android.synthetic.main.dialog_request_help.view.*
import kotlinx.android.synthetic.main.dialog_review.view.*
import kotlinx.coroutines.*
import zendesk.chat.Chat
import zendesk.chat.ChatConfiguration
import zendesk.chat.ChatEngine
import zendesk.chat.VisitorInfo
import zendesk.messaging.MessagingActivity
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), ImageListener, MainView {
    private lateinit var bannerImages: MutableList<BannerData?>
    private lateinit var news: MutableList<NewsData?>
    private lateinit var menus: MutableList<Menu?>
    private lateinit var presenter: MainPresenter
    private lateinit var repository: AppRepository
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private lateinit var timeFormat: SimpleDateFormat
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var dialog: AlertDialog
    private lateinit var view: View

    private lateinit var loadBanner: Job
    private lateinit var loadMenu: Job
    private lateinit var loadNews: Job
    private lateinit var loadCustomer: Job
    private lateinit var loadConfig: Job
    private lateinit var loggingOut: Job
    private lateinit var loadHistory: Job
    private lateinit var checkingOut: Job
    private lateinit var submittingItem: Job
    private lateinit var verifying: Job
    private lateinit var submittingReview: Job

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    private var customer: Customer? = null
    private var login: Login? = null
    private var config: Config? = null
    private var history: History? = null
    private var btnState: Boolean = false

    private var password by Delegates.notNull<String>()
    private var email by Delegates.notNull<String>()
    private var roomId by Delegates.notNull<Int>()
    private var roomName by Delegates.notNull<String>()
    private var checkInNumber by Delegates.notNull<String>()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_main)
        setSupportActionBar(tb_main)

        // Starting task
        initiateTask()
    }

    override fun onStart() {
        super.onStart()
        startLockMode()
    }

    override fun onResume() {
        super.onResume()
        launchTask()
    }

    override fun onBackPressed() {
        openLogoutDialog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.loadCoordinates()
            } else {
                Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == Constants.CHANGE_LOCATION_SETTING_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.startLocationUpdates(presenter.generateLocationRequest())
            } else {
                Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setImageForPosition(position: Int, imageView: ImageView?) {
        // Check if imageView is null or not then execute the code within block
        imageView?.let {
            Glide.with(it).load(bannerImages[position]?.image).into(it)
        }
    }

    override fun loadBanner(data: Banner?) {
        // Load banner data to carouselview
        data?.let {
            it.image?.run {
                bannerImages.clear()
                bannerImages.addAll(this)
                crv_main.pageCount = bannerImages.size
            }
        }
    }

    override fun loadMenu(data: MutableList<Menu?>) {
        // Load menu item to recyclerView
        menus.clear()
        menus.addAll(data)
        menuAdapter.notifyDataSetChanged()
        Log.e("MENUS", Gson().toJson(data))
    }

    override fun loadNews(data: News?) {
        // Load news to adapter
        data?.let {
            it.data?.run {
                news.clear()
                news.addAll(this)
                newsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun loadWeather(data: Weather?) {
        // Load weather data to widget
        data?.let {
            it.name?.run {
                tv_location.text = this
            }

            it.main?.temp?.minus(273.15)?.roundToInt()?.toString().run {
                val fullTemp = this + " \u00B0" + "C"
                tv_current_temp.text = fullTemp
            }

            it.weather?.get(0)?.description.run {
                tv_current_weather.text = this
            }
        }
    }

    override fun loadCustomerInfo(data: Customer?) {
        data?.let { it ->
            customer = it

            // Store data in shared preferences
            val sharedPreferences =
                getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("customer", Gson().toJson(customer))
            editor.apply()

            customer?.data?.customer?.name?.let {
                btn_welcome.text = it
            } ?: run {
                btn_welcome.text = "-"
            }

            customer?.data?.customerId?.let {
                loadHistory = presenter.loadHistory(roomId, it)
            }

            customer?.data?.orderNumber?.let {
                checkInNumber = it
            }
        }
    }

    override fun loadConfig(data: Config?) {
        data?.let { it ->
            config = it

            config?.data?.fnbIcon?.let {
                Glide.with(img_fnb)
                    .load(it)
                    .into(img_fnb)
            }

            config?.data?.laundryIcon?.let {
                Glide.with(img_laundry)
                    .load(it)
                    .into(img_laundry)
            }

            config?.data?.spaIcon?.let {
                Glide.with(img_spa)
                    .load(it)
                    .into(img_spa)
            }

            config?.data?.amenitiesIcon?.let {
                Glide.with(img_amenities)
                    .load(it)
                    .into(img_amenities)
            }

            val sharedPreferences =
                getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("config", Gson().toJson(config))
            editor.apply()
        }
    }

    override fun loadHistory(data: History?) {
        data?.let { it ->
            history = it

            history?.data?.let {
                img_notification.setImageDrawable(
                    Utilities.generateIcon(
                        this,
                        it.size,
                        R.drawable.ic_history
                    )
                )
            }
        }
    }

    override fun setTotalPrice(price: Int) {
        view.tv_order_total_price.text = "Total price ${Utilities.getCurrency(price)}"
    }

    override fun notifyPasswordStatus(data: Status?) {
        data?.let {
            if (it.status == 200) {
                dialog.cancel()
                openCheckListDialog(password)
            } else {
                view.et_dialog_pasword.text = null
                view.et_dialog_pasword.hint = "Wrong password"
            }
        }
    }

    override fun notifyStaffStatus(data: Staff?) {
        data?.let {
            if (it.status == 200) {
                dialog.cancel()
                data.data?.let { it1 -> openCheckListDialog(it1) }
            } else {
                view.et_dialog_pasword.text = null
                view.et_dialog_pasword.hint = "Wrong email or password"
                view.etDialogEmail.text = null
                view.etDialogEmail.hint = "Wrong email or password"
            }
        }
    }

    override fun notifySubmitItemStatus(data: Status?) {
        data?.let {
            if (it.status == 200) {
                dialog.cancel()

//                remove checkout
//                val checkOut = Checkout(customer?.data?.orderNumber)
//                checkingOut = presenter.checkOut(checkOut)
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun notifyCheckoutStatus(data: Status?) {
        data?.let {
            if (it.status == 200) {
                Utilities.removeCustomerData(this)
                customer = null
                history = null

                initiateTask()
                launchTask()
            }

            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun notifyLogoutStatus(data: Logout?) {
        data?.status?.let {
            if (it == 200) {
                Utilities.removeRoomData(this)
                dialog.cancel()

                unlockApp()
            } else {
                view.et_dialog_pasword.text = null
                view.et_dialog_pasword.hint = "Wrong password"
            }
        }
    }

    override fun notifyHelpStatus(data: Status?) {
        data?.message?.let {
            btnState = false
            Utilities.stopAnimation(img_refresh_data)
            openHelpMessageDialog(it)
        }
    }

    override fun notifyReviewStatus(data: Status?) {
        data?.status?.let {
            if (it == 200) {
                launchTask()
                Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
            } else {
                launchTask()
                Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun weatherFailed() {
        Log.e("WEATHER", "Error")
    }

    override fun bannerFailed() {
        Log.e("BANNER", "Error")
    }

    override fun newsFailed() {
        Log.e("NEWS", "Error")
    }

    override fun logOutFailed() {
        Log.e("LOGOUT", "Error")
    }

    override fun submitItemFailed() {
        Log.e("SUBMIT ITEM", "Error")
    }

    override fun configFailed() {
        Log.e("CONFIG", "Error")
    }

    override fun passwordFailed() {
        Log.e("PASSWORD", "Error")
    }

    override fun staffFailed() {
        Log.e("STAFF", "Error")
    }

    override fun historyFailed() {
        Log.e("HISTORY", "Error")
    }

    override fun requestHelpFailed() {
        Log.e("REQUEST HELP", "Error")
    }

    override fun submitReviewFailed() {
        Log.e("SUBMIT REVIEW", "Error")
    }

    override fun customerFailed() {
        btn_welcome.text = "Welcome"
        img_notification.setImageDrawable(Utilities.generateIcon(this, 0, R.drawable.ic_history))
        Log.e("CUSTOMER", "Error")
    }

    override fun checkOutFailed() {
        Log.e("CHECKOUT", "Error")
    }

    override fun onDestroy() {
        super.onDestroy()
        // If job is still active, cancel it
        if (scope.isActive) {
            scope.cancel()
        }

        // Remove handler
        handler.removeCallbacks(runnable)

        presenter.stopLocationUpdates()
    }

    private fun initiateTask() {
        // Set lock policies
        setLockPolicies()

        // Clear cache
        this.cacheDir.deleteRecursively()

        // Initiate Zopim chat
        Chat.INSTANCE.init(this, BuildConfig.ZENDESK_API_KEY)

        // Get shared preferences
        getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            .getString("room", null)?.let { it ->
                login = Gson().fromJson(it, Login::class.java)

                login?.data?.id?.let {
                    roomId = it
                }

                login?.data?.roomNumber?.let {
                    roomName = it
                    tv_room_id.text = "Room $roomName"
                }
            }

        // Initiate repository
        repository = AppRepository()

        // Initiate handler
        handler = Handler()

        // Initiate timeformat
        timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

        // Get current time
        getTime()

        // Initiate collection
        menus = mutableListOf()
        bannerImages = mutableListOf()
        news = mutableListOf()

        // initiate adapter and presenter
        presenter = MainPresenter(this, scope, this, repository)
        menuAdapter = MenuAdapter(this, menus)
        newsAdapter = NewsAdapter(this, news)

        // Initiate banner
        crv_main.setImageListener(this)

        // Initiate recyclerView for news
        rv_news_main.layoutManager = LinearLayoutManager(this)
        rv_news_main.setHasFixedSize(true)
        rv_news_main.adapter = newsAdapter

        // Set click listener
        img_chat.setOnClickListener {
            openCheckoutDialog()
        }

        tv_room_id.setOnClickListener {
            openCheckoutDialog()
        }

        img_refresh_data.setOnClickListener {
            Utilities.rotateIcon(this, it)
            launchTask()
        }

        img_notification.setOnClickListener {
            if (history != null && history?.data?.size != 0) {
                openHistoryDialog()
            } else {
                Toast.makeText(this, "Order is empty", Toast.LENGTH_SHORT).show()
            }
        }

        img_panic.setOnClickListener {
            openRequestHelpDialog()
        }

        btn_welcome.setOnClickListener {
            customer?.data?.let {
                openCheckInInfoDialog()
            }
        }

        cv_fnb.setOnClickListener {
            if (customer?.data?.customer != null) {
                //            moveToTitleActivity(Constants.FNB, config?.data?.fnbImage)
                startActivity(Intent(this, FnbActivity::class.java))
            } else {
                Toast.makeText(this, "Guest not checked in", Toast.LENGTH_LONG).show()
            }
        }

        cv_laundry.setOnClickListener {
            if (customer?.data?.customer != null) {
                //            moveToTitleActivity(Constants.LAUNDRY, config?.data?.laundryImage)
                startActivity(Intent(this, LaundryActivity::class.java))
            } else {
                Toast.makeText(this, "Guest not checked in", Toast.LENGTH_LONG).show()
            }
        }

        cv_spa.setOnClickListener {
            if (customer?.data?.customer != null) {
                //            moveToTitleActivity(Constants.SPA, config?.data?.spaImage)
                startActivity(Intent(this, SpaActivity::class.java))
            } else {
                Toast.makeText(this, "Guest not checked in", Toast.LENGTH_LONG).show()
            }
        }

        cv_amenities.setOnClickListener {
            if (customer?.data?.customer != null) {
                //            moveToTitleActivity(Constants.AMENITIES, config?.data?.amenitiesImage)
                startActivity(Intent(this, AmenitiesActivity::class.java))
            } else {
                Toast.makeText(this, "Guest not checked in", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getTime() {
        // Set initial state of time indicator
        var isShowing = false

        // Start runnable to get current date and time then display to UI
        runnable = Runnable {
            handler.postDelayed(runnable, 1000)
            val fullDateAndTime = Calendar.getInstance().time
            val time = timeFormat.format(fullDateAndTime).split(":")
            val date = dateFormat.format(fullDateAndTime)

            tv_current_hour.text = time[0]
            tv_current_minutes.text = time[1]
            tv_current_date.text = date

            if (isShowing) {
                tv_time_separator.visibility = View.INVISIBLE
                isShowing = false
            } else {
                tv_time_separator.visibility = View.VISIBLE
                isShowing = true
            }
        }

        handler.postDelayed(runnable, 0)
    }

    private fun openLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_logout, null)
        builder.setView(view)

        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        view.btn_dialog_logout_cancel.setOnClickListener { dialog.cancel() }
        view.btn_dialog_logout.setOnClickListener {
            when {
                view.et_dialog_pasword.text.isEmpty() -> {
                    view.et_dialog_pasword.hint = "Password can not be empty"
                }
                else -> {
                    loggingOut = presenter.logOut(roomId, view.et_dialog_pasword.text.toString())
                }
            }
        }

        dialog.show()
        setDimensionLarge(dialog)
    }

    private fun openCheckoutDialog() {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_checkout, null)
        builder.setView(view)

        dialog = builder.create()
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)

        view.btn_dialog_checkout_cancel.setOnClickListener { dialog.cancel() }
        view.btn_dialog_checkout.setOnClickListener {
                if(view.etDialogEmail.text.isEmpty()) {
                    view.etDialogEmail.hint = "Email can not be empty"
                }
                if(view.et_dialog_pasword.text.isEmpty())  {
                    view.et_dialog_pasword.hint = "Password can not be empty"
                }
                if(view.et_dialog_pasword.text.isNotEmpty() && view.etDialogEmail.text.isNotEmpty())  {
                    email = view.etDialogEmail.text.toString()
                    password = view.et_dialog_pasword.text.toString()
                    verifying = presenter.staffLogin(email, password)
                }
        }

        dialog.show()
        setDimensionSmall(dialog)
    }

    private fun openCheckListDialog(name: String) {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_checklist, null)
        builder.setView(view)

        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        view.et_checklist_name.setText(name)

        login?.data?.checklist?.item?.let {
            val adapter = ChecklistAdapter(this, it)
            view.rv_checklist.layoutManager = LinearLayoutManager(this)
            view.rv_checklist.setHasFixedSize(true)
            view.rv_checklist.adapter = adapter
        }

        view.btn_checklist_cancel.setOnClickListener { dialog.cancel() }
        view.btn_checklist_submit.setOnClickListener {
            if (view.et_checklist_name.text.isEmpty()) {
                view.et_checklist_name.text = null
                view.et_checklist_name.hint = "Employee name can not be empty"
            } else {
                val checkInId = customer?.data?.id
                val detail = login?.data?.checklist?.item

                if (checkInId != null && detail != null) {
                    val item = presenter.generateItemChecklist(
                        roomId,
                        checkInId,
                        view.et_checklist_name.text.toString(),
                        detail
                    )
                    submittingItem = presenter.submitItem(item)
                } else {
                    Toast.makeText(
                        this,
                        "No one is checked in or item failed to send",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        dialog.show()
        setDimensionSmall(dialog)
    }

    private fun openCheckInInfoDialog() {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_checkin_info, null)
        builder.setView(view)

        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        view.tv_name.text = customer?.data?.customer?.name
        view.tv_days_of_stay.text = customer?.data?.daysOfStay.toString()
        view.tv_check_in.text = customer?.data?.checkInDate
        view.tv_check_out.text = customer?.data?.checkOutDate

        view.btn_check_out_open.setOnClickListener {
            dialog.cancel()
            openReviewDialog()
        }

        view.btn_check_in_close.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
        setDimensionSmall(dialog)
    }

    private fun openHistoryDialog() {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_history, null)
        builder.setView(view)

        dialog = builder.create()
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)

        history?.data?.let {
            val adapter = HistoryAdapter(this, this, it)
            view.rv_history.layoutManager = LinearLayoutManager(this)
            view.rv_history.setHasFixedSize(true)
            view.rv_history.adapter = adapter
        }

        view.btn_dialog_history_close.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
        setDimensionSmall(dialog)
    }

    private fun launchChat() {
        val chatConfig = ChatConfiguration.builder()
            .withPreChatFormEnabled(false)
            .withTranscriptEnabled(false)
            .build()

        val profileProvider = Chat.INSTANCE.providers()?.profileProvider()

        val name = customer?.data?.customer?.name
        val email = customer?.data?.customer?.email
        val phone = customer?.data?.customer?.phone

        val visitorInfo = VisitorInfo
            .builder()
            .withName(name)
            .withEmail(email)
            .withPhoneNumber(phone)
            .build()

        profileProvider?.apply {
            this.setVisitorInfo(visitorInfo, object : ZendeskCallback<Void>() {
                override fun onSuccess(p0: Void?) {

                }

                override fun onError(p0: ErrorResponse?) {

                }
            })
        }

        MessagingActivity
            .builder()
            .withEngines(ChatEngine.engine())
            .show(this, chatConfig)
    }

    private fun launchTask() {
        // Check gps permission
        presenter.checkGpsPermission()

        // Start loading configuration data
        loadConfig = presenter.loadConfig()

        // Start loading menus
        loadMenu = presenter.loadMenu()

        // Start downloading banners
        loadBanner = presenter.loadBanner()

        // Start downloading news
        loadNews = presenter.loadNews()

        // Start downloading customer info
        loadCustomer = presenter.loadCustomerInfo(roomId)
    }

    private fun openRequestHelpDialog() {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_request_help, null)
        builder.setView(view)
        dialog = builder.create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        view.btn_dialog_request_help_cancel.setOnClickListener {
            dialog.cancel()
        }

        view.btn_dialog_request_help_ok.setOnClickListener {
            dialog.cancel()
            presenter.requestHelp(roomName)
        }

        dialog.show()
        setDimensionSmall(dialog)
    }

    private fun openHelpMessageDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_help_message, null)
        builder.setView(view)
        dialog = builder.create()

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        view.tv_dialog_help_message.text = message
        view.cl_dialog_help_message.setOnClickListener {
            dialog.cancel()
        }

        Utilities.fadeOutIcon(this, view.img_dialog_help_message)

        dialog.show()
        setDimensionSmall(dialog)
    }

    private fun openReviewDialog() {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.dialog_review, null)
        builder.setView(view)
        dialog = builder.create()

        val submitReview: Review =
            Gson().fromJson(resources.getString(R.string.submitReview), Review::class.java)
//        val submitReview: Review = Review()

        submitReview.transactionNo = checkInNumber

//        val reviewsCount: MutableList<Boolean> = mutableListOf()
//        for (i in 1..27) {
//            reviewsCount.add(false)
//        }

        view.rg_dialog_fo_1.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(0)?.data?.get(0)?.subData?.poor = 1
                1 -> submitReview.review?.get(0)?.data?.get(0)?.subData?.fair = 1
                2 -> submitReview.review?.get(0)?.data?.get(0)?.subData?.good = 1
                3 -> submitReview.review?.get(0)?.data?.get(0)?.subData?.excellent = 1
            }
//            reviewsCount[0] = true
        }

        view.rg_dialog_fo_2.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(0)?.data?.get(1)?.subData?.poor = 1
                1 -> submitReview.review?.get(0)?.data?.get(1)?.subData?.fair = 1
                2 -> submitReview.review?.get(0)?.data?.get(1)?.subData?.good = 1
                3 -> submitReview.review?.get(0)?.data?.get(1)?.subData?.excellent = 1
            }
//            reviewsCount[1] = true
        }

        view.rg_dialog_fo_3.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(0)?.data?.get(2)?.subData?.poor = 1
                1 -> submitReview.review?.get(0)?.data?.get(2)?.subData?.fair = 1
                2 -> submitReview.review?.get(0)?.data?.get(2)?.subData?.good = 1
                3 -> submitReview.review?.get(0)?.data?.get(2)?.subData?.excellent = 1
            }
//            reviewsCount[2] = true
        }

        view.rg_dialog_fo_4.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(0)?.data?.get(3)?.subData?.poor = 1
                1 -> submitReview.review?.get(0)?.data?.get(3)?.subData?.fair = 1
                2 -> submitReview.review?.get(0)?.data?.get(3)?.subData?.good = 1
                3 -> submitReview.review?.get(0)?.data?.get(3)?.subData?.excellent = 1
            }
//            reviewsCount[3] = true
        }

        view.rg_dialog_fo_5.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(0)?.data?.get(4)?.subData?.poor = 1
                1 -> submitReview.review?.get(0)?.data?.get(4)?.subData?.fair = 1
                2 -> submitReview.review?.get(0)?.data?.get(4)?.subData?.good = 1
                3 -> submitReview.review?.get(0)?.data?.get(4)?.subData?.excellent = 1
            }
//            reviewsCount[4] = true
        }

        view.rg_dialog_fnb_1.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(1)?.data?.get(0)?.subData?.poor = 1
                1 -> submitReview.review?.get(1)?.data?.get(0)?.subData?.fair = 1
                2 -> submitReview.review?.get(1)?.data?.get(0)?.subData?.good = 1
                3 -> submitReview.review?.get(1)?.data?.get(0)?.subData?.excellent = 1
            }
//            reviewsCount[5] = true
        }

        view.rg_dialog_fnb_2.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(1)?.data?.get(1)?.subData?.poor = 1
                1 -> submitReview.review?.get(1)?.data?.get(1)?.subData?.fair = 1
                2 -> submitReview.review?.get(1)?.data?.get(1)?.subData?.good = 1
                3 -> submitReview.review?.get(1)?.data?.get(1)?.subData?.excellent = 1
            }
//            reviewsCount[6] = true
        }

        view.rg_dialog_fnb_3.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(1)?.data?.get(2)?.subData?.poor = 1
                1 -> submitReview.review?.get(1)?.data?.get(2)?.subData?.fair = 1
                2 -> submitReview.review?.get(1)?.data?.get(2)?.subData?.good = 1
                3 -> submitReview.review?.get(1)?.data?.get(2)?.subData?.excellent = 1
            }
//            reviewsCount[7] = true
        }

        view.rg_dialog_fnb_4.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(1)?.data?.get(3)?.subData?.poor = 1
                1 -> submitReview.review?.get(1)?.data?.get(3)?.subData?.fair = 1
                2 -> submitReview.review?.get(1)?.data?.get(3)?.subData?.good = 1
                3 -> submitReview.review?.get(1)?.data?.get(3)?.subData?.excellent = 1
            }
//            reviewsCount[8] = true
        }

        view.rg_dialog_fnb_5.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(1)?.data?.get(4)?.subData?.poor = 1
                1 -> submitReview.review?.get(1)?.data?.get(4)?.subData?.fair = 1
                2 -> submitReview.review?.get(1)?.data?.get(4)?.subData?.good = 1
                3 -> submitReview.review?.get(1)?.data?.get(4)?.subData?.excellent = 1
            }
//            reviewsCount[9] = true
        }

        view.rg_dialog_fnb_6.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(1)?.data?.get(5)?.subData?.poor = 1
                1 -> submitReview.review?.get(1)?.data?.get(5)?.subData?.fair = 1
                2 -> submitReview.review?.get(1)?.data?.get(5)?.subData?.good = 1
                3 -> submitReview.review?.get(1)?.data?.get(5)?.subData?.excellent = 1
            }
//            reviewsCount[10] = true
        }

        view.rg_dialog_hk_1.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(0)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(0)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(0)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(0)?.subData?.excellent = 1
            }
//            reviewsCount[11] = true
        }

        view.rg_dialog_hk_2.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(1)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(1)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(1)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(1)?.subData?.excellent = 1
            }
//            reviewsCount[12] = true
        }

        view.rg_dialog_hk_3.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(2)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(2)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(2)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(2)?.subData?.excellent = 1
            }
//            reviewsCount[13] = true
        }

        view.rg_dialog_hk_4.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(3)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(3)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(3)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(3)?.subData?.excellent = 1
            }
//            reviewsCount[14] = true
        }

        view.rg_dialog_hk_5.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(4)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(4)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(4)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(4)?.subData?.excellent = 1
            }
//            reviewsCount[15] = true
        }

        view.rg_dialog_hk_6.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(5)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(5)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(5)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(5)?.subData?.excellent = 1
            }
//            reviewsCount[16] = true
        }

        view.rg_dialog_hk_7.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(6)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(6)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(6)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(6)?.subData?.excellent = 1
            }
//            reviewsCount[17] = true
        }

        view.rg_dialog_hk_8.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(2)?.data?.get(7)?.subData?.poor = 1
                1 -> submitReview.review?.get(2)?.data?.get(7)?.subData?.fair = 1
                2 -> submitReview.review?.get(2)?.data?.get(7)?.subData?.good = 1
                3 -> submitReview.review?.get(2)?.data?.get(7)?.subData?.excellent = 1
            }
//            reviewsCount[18] = true
        }

        view.rg_dialog_other_1.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(3)?.data?.get(0)?.subData?.poor = 1
                1 -> submitReview.review?.get(3)?.data?.get(0)?.subData?.fair = 1
                2 -> submitReview.review?.get(3)?.data?.get(0)?.subData?.good = 1
                3 -> submitReview.review?.get(3)?.data?.get(0)?.subData?.excellent = 1
            }
//            reviewsCount[19] = true
        }

        view.rg_dialog_ga_1.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(4)?.data?.get(0)?.subData?.no = 1
                1 -> submitReview.review?.get(4)?.data?.get(0)?.subData?.yes = 1
            }
//            reviewsCount[20] = true
        }

        view.rg_dialog_ga_2.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(4)?.data?.get(1)?.subData?.no = 1
                1 -> submitReview.review?.get(4)?.data?.get(1)?.subData?.yes = 1
            }
//            reviewsCount[21] = true
        }

        view.rg_dialog_ga_3.setOnCheckedChangeListener { group, checkedId ->
            val button = group.findViewById<RadioButton>(checkedId)
            when (group.indexOfChild(button)) {
                0 -> submitReview.review?.get(4)?.data?.get(2)?.subData?.no = 1
                1 -> submitReview.review?.get(4)?.data?.get(2)?.subData?.yes = 1
            }
//            reviewsCount[22] = true
        }

        view.cb_dialog_review_friend.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                submitReview.info?.friends = 1
            } else {
                submitReview.info?.friends = 0
            }
//            reviewsCount[23] = true
        }

        view.cb_dialog_review_travel_agent.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                submitReview.info?.travelAgent = 1
//                reviewsCount[24] = true
            } else {
                submitReview.info?.travelAgent = 0
//                reviewsCount[24] = false
            }
        }

        view.cb_dialog_review_online_booking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                submitReview.info?.onlineBooking = 1
//                reviewsCount[25] = true
            } else {
                submitReview.info?.onlineBooking = 0
//                reviewsCount[25] = false
            }
        }

        view.cb_dialog_review_others.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                submitReview.info?.others = 1
//                reviewsCount[26] = true
            } else {
                submitReview.info?.others = 0
//                reviewsCount[26] = false
            }
        }


        view.et_dialog_note.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                submitReview.comments = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

        })

        view.btn_dialog_review_cancel.setOnClickListener {
            dialog.cancel()
        }

        view.btn_dialog_review_submit.setOnClickListener {
//            if (submitReview.comments.isNullOrBlank()) {
//                Toast.makeText(this, "Please type in comments", Toast.LENGTH_LONG).show()
//
//            } else {
            dialog.cancel()
            Log.e("submitReview", submitReview.toString())
            submittingReview = presenter.submitReview(submitReview)
//            }

        }

        dialog.show()
        setDimensionSuperLarge(dialog)
    }

    private fun moveToTitleActivity(category: String, image: String?) {
        val intent = Intent(this, TitleActivity::class.java)
        intent.putExtra("category", category)
        intent.putExtra("image", image)
        startActivity(intent)
    }

    private fun setLockPolicies() {
        // Initiate device policy manager
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        // Initiate admin component
        adminComponentName = DeviceAdminReceiver().getComponentName(this)

        if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
            setDefaultAppPolicies(true)
        } else {
            Toast.makeText(this, R.string.not_device_owner, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLockMode() {
        // start lock task mode if its not already active
        if (devicePolicyManager.isLockTaskPermitted(this.packageName)) {
            val activityManager: ActivityManager? =
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager?.let {
                if (it.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                    startLockTask()
                }
            }
        }
    }

    private fun unlockApp() {
        val activityManager: ActivityManager? =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager?.let {
            if (it.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_LOCKED) {
                stopLockTask()
            }

            setDefaultAppPolicies(false)

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(Constants.MAIN_ACTIVITY_KEY, Constants.FROM_MAIN_ACTIVITY)
            startActivity(intent)
            finish()
        }
    }

    private fun setDefaultAppPolicies(active: Boolean) {
        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active)
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active)

        // disable keyguard and status bar
        if (devicePolicyManager.isAdminActive(adminComponentName)) {
            devicePolicyManager.setKeyguardDisabled(adminComponentName, active)
            devicePolicyManager.setStatusBarDisabled(adminComponentName, active)
        }


        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active)

        // set system update policy
        if (active) {
            devicePolicyManager
                .setSystemUpdatePolicy(
                    adminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
                )
        } else {
            if (devicePolicyManager.isAdminActive(adminComponentName)) {
                devicePolicyManager
                    .setSystemUpdatePolicy(
                        adminComponentName, null
                    )
            }

        }

        // set this Activity as a lock task package
        if (devicePolicyManager.isAdminActive(adminComponentName)) {
            devicePolicyManager
                .setLockTaskPackages(
                    adminComponentName,
                    if (active) arrayOf(packageName) else arrayOf()
                )
        }


        val intentFilter = IntentFilter(Intent.ACTION_MAIN)
        intentFilter.addCategory(Intent.CATEGORY_HOME)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)

        if (active) {
            // set MainActivity as home intent receiver so that it is started
            // on reboot
            devicePolicyManager
                .addPersistentPreferredActivity(
                    adminComponentName,
                    intentFilter,
                    ComponentName(packageName, MainActivity::class.java.name)
                )
        } else {
            if (devicePolicyManager.isAdminActive(adminComponentName)) {
                devicePolicyManager
                    .clearPackagePersistentPreferredActivities(
                        adminComponentName,
                        packageName
                    )
            }

        }
    }

    private fun setUserRestriction(restriction: String, disallow: Boolean) {
        if (disallow) {
            devicePolicyManager
                .addUserRestriction(
                    adminComponentName,
                    restriction
                )
        } else {
            if (devicePolicyManager.isAdminActive(adminComponentName)) {
                devicePolicyManager
                    .clearUserRestriction(
                        adminComponentName, restriction
                    )
            }

        }
    }

    private fun enableStayOnWhilePluggedIn(enabled: Boolean) {
        if (enabled) {
            devicePolicyManager
                .setGlobalSetting(
                    adminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    (BatteryManager.BATTERY_PLUGGED_AC
                            or BatteryManager.BATTERY_PLUGGED_USB
                            or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
                )
        } else {
            if (devicePolicyManager.isAdminActive(adminComponentName)) {
                devicePolicyManager
                    .setGlobalSetting(
                        adminComponentName,
                        Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                        "0"
                    )
            }

        }
    }
}

