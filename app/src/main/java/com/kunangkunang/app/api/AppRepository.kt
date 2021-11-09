package com.kunangkunang.app.api

import android.util.Log
import com.kunangkunang.app.model.amenities.Amenities
import com.kunangkunang.app.model.banner.Banner
import com.kunangkunang.app.model.checkout.Checkout
import com.kunangkunang.app.model.config.Config
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.fnb.FnbCategory
import com.kunangkunang.app.model.history.History
import com.kunangkunang.app.model.item.Item
import com.kunangkunang.app.model.laundry.Laundry
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.login.RoomRequest
import com.kunangkunang.app.model.logout.Logout
import com.kunangkunang.app.model.news.DetailNews
import com.kunangkunang.app.model.news.News
import com.kunangkunang.app.model.review.Review
import com.kunangkunang.app.model.status.Status
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.room.RoomData
import com.kunangkunang.app.model.spa.Spa
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.model.weather.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository {
    fun getCurrentWeather(lat: String, lon: String, callback: AppRepositoryCallback<Weather?>) {
        ApiClient
            .openWeatherAPIServices
            .getCurrentWeather(lat, lon)
            .enqueue(object : Callback<Weather?> {
                override fun onFailure(call: Call<Weather?>?, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Weather?>?, response: Response<Weather?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getBanner(callback: AppRepositoryCallback<Banner?>) {
        ApiClient
            .kunangKunangAPIServices
            .getBanner()
            .enqueue(object : Callback<Banner?> {
                override fun onFailure(call: Call<Banner?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Banner?>, response: Response<Banner?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }

            })
    }

    fun getNews(callback: AppRepositoryCallback<News?>) {
        ApiClient
            .kunangKunangAPIServices
            .getNews()
            .enqueue(object : Callback<News?> {
                override fun onFailure(call: Call<News?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<News?>, response: Response<News?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }

            })
    }

    fun getFoodCategory(callback: AppRepositoryCallback<FnbCategory?>) {
        ApiClient
            .kunangKunangAPIServices
            .getFnbCategory()
            .enqueue(object : Callback<FnbCategory?> {
                override fun onFailure(call: Call<FnbCategory?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<FnbCategory?>, response: Response<FnbCategory?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getLaundry(callback: AppRepositoryCallback<Laundry?>) {
        ApiClient
            .kunangKunangAPIServices
            .getLaundry()
            .enqueue(object : Callback<Laundry?> {
                override fun onFailure(call: Call<Laundry?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Laundry?>, response: Response<Laundry?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getAmenities(callback: AppRepositoryCallback<Amenities?>) {
        ApiClient
            .kunangKunangAPIServices
            .getAmenities()
            .enqueue(object : Callback<Amenities?> {
                override fun onFailure(call: Call<Amenities?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Amenities?>, response: Response<Amenities?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })

    }

    fun postTransaction(transaction: Transaction, callback: AppRepositoryCallback<TransactionResponse?>) {
        ApiClient
            .kunangKunangAPIServices
            .postTransaction(transaction)
            .enqueue(object : Callback<TransactionResponse?> {
                override fun onFailure(call: Call<TransactionResponse?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(
                    call: Call<TransactionResponse?>, response: Response<TransactionResponse?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getRoom(callback: AppRepositoryCallback<Room?>) {
        ApiClient
            .kunangKunangAPIServices
            .getRoom()
            .enqueue(object : Callback<Room?> {
                override fun onFailure(call: Call<Room?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Room?>, response: Response<Room?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getSpa(callback: AppRepositoryCallback<Spa?>) {
        ApiClient
            .kunangKunangAPIServices
            .getSpa()
            .enqueue(object : Callback<Spa?> {
                override fun onFailure(call: Call<Spa?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Spa?>, response: Response<Spa?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun logOut(roomId: Int, password: String, callback: AppRepositoryCallback<Logout?>) {
        ApiClient
            .kunangKunangAPIServices
            .logOut(RoomRequest(roomId, password))
            .enqueue(object : Callback<Logout?> {
                override fun onFailure(call: Call<Logout?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Logout?>, response: Response<Logout?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getDetailNews(id: Int, callback: AppRepositoryCallback<DetailNews?>) {
        ApiClient
            .kunangKunangAPIServices
            .getDetailNews(id)
            .enqueue(object : Callback<DetailNews?> {
                override fun onFailure(call: Call<DetailNews?>, t: Throwable) {
                    Log.e("news error", "masuk onfailure")
                    callback.onDataError()
                }

                override fun onResponse(call: Call<DetailNews?>, response: Response<DetailNews?>?) {
                    response?.let {
                        Log.e("news response", it.toString())
                        if (it.isSuccessful) {
                            Log.e("news success", "masuk onfailure")

                            callback.onDataLoaded(it.body())
                        } else {
                            Log.e("news error", "masuk onresponse")

                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getCustomerInfo(id: Int, callback: AppRepositoryCallback<Customer?>) {
        ApiClient
            .kunangKunangAPIServices
            .getCustomerInfo(id)
            .enqueue(object : Callback<Customer?> {
                override fun onFailure(call: Call<Customer?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Customer?>, response: Response<Customer?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getLoginData(id: Int, password: String, callback: AppRepositoryCallback<Login?>) {
        ApiClient
            .kunangKunangAPIServices
            .getLoginData(RoomRequest(id, password))
            .enqueue(object : Callback<Login?> {
                override fun onFailure(call: Call<Login?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Login?>, response: Response<Login?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun checkOut(checkout: Checkout, callback: AppRepositoryCallback<Status?>) {
        ApiClient
            .kunangKunangAPIServices
            .checkOut(checkout)
            .enqueue(object : Callback<Status?> {
                override fun onFailure(call: Call<Status?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(
                    call: Call<Status?>,
                    response: Response<Status?>?
                ) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun submitItem(item: Item, callback: AppRepositoryCallback<Status?>) {
        ApiClient
            .kunangKunangAPIServices
            .submitItem(item)
            .enqueue(object : Callback<Status?> {
                override fun onFailure(call: Call<Status?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Status?>, response: Response<Status?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getConfig(callback: AppRepositoryCallback<Config?>) {
        ApiClient
            .kunangKunangAPIServices
            .getConfig()
            .enqueue(object : Callback<Config?> {
                override fun onFailure(call: Call<Config?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Config?>, response: Response<Config?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun verifyPassword(password: String, callback: AppRepositoryCallback<Status?>) {
        ApiClient
            .kunangKunangAPIServices
            .verifyPassword(RoomRequest(password = password))
            .enqueue(object : Callback<Status?> {
                override fun onFailure(call: Call<Status?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Status?>, response: Response<Status?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun getHistory(roomId: Int, customerId: Int, callback: AppRepositoryCallback<History?>) {
        ApiClient
            .kunangKunangAPIServices
            .getHistory(roomId, customerId)
            .enqueue(object : Callback<History?> {
                override fun onFailure(call: Call<History?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<History?>, response: Response<History?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun requestHelp(roomName: String, callback: AppRepositoryCallback<Status?>) {
        ApiClient
            .kunangKunangAPIServices
            .requestHelp(roomName)
            .enqueue(object : Callback<Status?> {
                override fun onFailure(call: Call<Status?>, t: Throwable) {
                   callback.onDataError()
                }

                override fun onResponse(call: Call<Status?>, response: Response<Status?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }

    fun submitReview(review: Review, callback: AppRepositoryCallback<Status?>) {
        ApiClient
            .kunangKunangAPIServices
            .submitReview(review)
            .enqueue(object : Callback<Status?> {
                override fun onFailure(call: Call<Status?>, t: Throwable) {
                    callback.onDataError()
                }

                override fun onResponse(call: Call<Status?>, response: Response<Status?>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            callback.onDataLoaded(it.body())
                        } else {
                            callback.onDataError()
                        }
                    }
                }
            })
    }
}