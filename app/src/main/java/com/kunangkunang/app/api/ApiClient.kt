package com.kunangkunang.app.api

import com.kunangkunang.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val client = OkHttpClient()
        .newBuilder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val kunangKunang: Retrofit = Retrofit
        .Builder()
        .baseUrl(BuildConfig.KUNANG_KUNANG_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val openWeather: Retrofit = Retrofit
        .Builder()
        .baseUrl(BuildConfig.OPEN_WEATHER_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API service for kunang-kunang backend
    val kunangKunangAPIServices: ApiServices = kunangKunang.create(ApiServices::class.java)

    // API service for open weather backend
    val openWeatherAPIServices: ApiServices = openWeather.create(ApiServices::class.java)
}