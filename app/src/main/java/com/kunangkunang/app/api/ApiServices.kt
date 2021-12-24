package com.kunangkunang.app.api

import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.model.activities.Activities
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
import com.kunangkunang.app.model.login.Staff
import com.kunangkunang.app.model.login.StaffRequest
import com.kunangkunang.app.model.logout.Logout
import com.kunangkunang.app.model.news.DetailNews
import com.kunangkunang.app.model.news.News
import com.kunangkunang.app.model.review.Review
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.spa.Spa
import com.kunangkunang.app.model.status.Status
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.model.weather.Weather
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") key: String = BuildConfig.OPEN_WEATHER_KEY
    ): Call<Weather>

    @GET("data/get-banner")
    fun getBanner(): Call<Banner>

    @GET("data/get-news")
    fun getNews(): Call<News>

    @GET("data/get-fnb-category")
    fun getFnbCategory(): Call<FnbCategory>

    @GET("data/get-laundry")
    fun getLaundry(): Call<Laundry>

    @GET("data/get-amenities")
    fun getAmenities(): Call<Amenities>

    @GET("data/get-tour")
    fun getActivities(): Call<Activities>

    @POST("event/send-transaction")
    fun postTransaction(@Body transaction: Transaction): Call<TransactionResponse>

    @GET("data/get-room")
    fun getRoom(): Call<Room>

    @GET("data/get-spa")
    fun getSpa(): Call<Spa>

    @POST("event/logout")
    fun logOut(
        @Body logoutData: RoomRequest
    ): Call<Logout>

//    @GET("api/news/{id}")
//    fun getDetailNews(@Path("id") id: Int): Call<DetailNews>

    @GET("data/get-news-by-id")
    fun getDetailNews(@Query("id") id: Int): Call<DetailNews>

    @GET("data/get-customer-info")
    fun getCustomerInfo(@Query("room_id") roomId: Int): Call<Customer>

    @POST("event/login")
    fun getLoginData(
        @Body loginData: RoomRequest
    ): Call<Login>

    @POST("event/staff-login")
    fun getStaffLoginData(
        @Body loginData: StaffRequest
    ): Call<Staff>

    @POST("api/room/checkout")
    fun checkOut(@Body checkout: Checkout): Call<Status>

    @POST("event/send-item")
    fun submitItem(@Body item: Item): Call<Status>

    @GET("data/get-config")
    fun getConfig(): Call<Config>

    @POST("event/check-password")
    fun verifyPassword(@Body loginData: RoomRequest): Call<Status>

    @GET("data/get-history")
    fun getHistory(
        @Query("room_id") roomId: Int?,
        @Query("customer_id") customerId: Int?
    ): Call<History>

    @GET("event/send-notification")
    fun requestHelp(@Query("room_name") roomName: String): Call<Status>

    @POST("event/send-review")
    fun submitReview(@Body review: Review): Call<Status>
}