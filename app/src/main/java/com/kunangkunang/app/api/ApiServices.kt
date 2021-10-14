package com.kunangkunang.app.api

import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.model.amenities.Amenities
import com.kunangkunang.app.model.banner.Banner
import com.kunangkunang.app.model.checkout.Checkout
import com.kunangkunang.app.model.config.Config
import com.kunangkunang.app.model.status.Status
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.fnb.FnbCategory
import com.kunangkunang.app.model.history.History
import com.kunangkunang.app.model.item.Item
import com.kunangkunang.app.model.laundry.Laundry
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.logout.Logout
import com.kunangkunang.app.model.news.DetailNews
import com.kunangkunang.app.model.news.News
import com.kunangkunang.app.model.review.Review
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.spa.Spa
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.model.weather.Weather
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {
    @GET("data/2.5/weather")
    fun getCurrentWeather(@Query("lat") lat: String,
                          @Query("lon") lon: String,
                          @Query("appid") key: String = BuildConfig.OPEN_WEATHER_KEY): Call<Weather>

    @GET("api/banner")
    fun getBanner(): Call<Banner>

    @GET("api/news")
    fun getNews(): Call<News>

    @GET("api/food_category")
    fun getFnbCategory(): Call<FnbCategory>

    @GET("api/laundry")
    fun getLaundry(): Call<Laundry>

    @GET("api/amenities")
    fun getAmenities(): Call<Amenities>

    @POST("api/transaction")
    fun postTransaction(@Body transaction: Transaction): Call<TransactionResponse>

    @GET("api/getroom")
    fun getRoom(): Call<Room>

    @GET("api/spa")
    fun getSpa(): Call<Spa>

    @POST("api/room/logout")
    fun logOut(@Query("room_id") roomId: Int,
               @Query("password") password: String): Call<Logout>

    @GET("api/news/{id}")
    fun getDetailNews(@Path("id") id: Int): Call<DetailNews>

    @POST("api/room/getCustomer")
    fun getCustomerInfo(@Query("room_id") roomId: Int): Call<Customer>

    @POST("api/checking_room")
    fun getLoginData(@Query("room_id") id: Int,
                     @Query("password") password: String): Call<Login>

    @POST("api/room/checkout")
    fun checkOut(@Body checkout: Checkout): Call<Status>

    @POST("api/room/sendItem")
    fun submitItem(@Body item: Item): Call<Status>

    @GET("api/system")
    fun getConfig(): Call<Config>

    @POST("api/checkPassword")
    fun verifyPassword(@Query("password") password: String): Call<Status>

    @GET("api/transaction")
    fun getHistory(@Query("room_id") roomId: Int?, @Query("customer_id") customerId: Int?): Call<History>

    @POST("api/sendNotif")
    fun requestHelp(@Query("message") roomName: String): Call<Status>

    @POST("api/customer_review")
    fun submitReview(@Body review: Review): Call<Status>
}