package com.kunangkunang.app.model.weather

import com.google.gson.annotations.SerializedName

data class Weather (
    @SerializedName("weather")
    val weather: List<WeatherData?>? = null,

    @SerializedName("main")
    val main: WeatherMain? = null,

    @SerializedName("name")
    val name: String? = null
)