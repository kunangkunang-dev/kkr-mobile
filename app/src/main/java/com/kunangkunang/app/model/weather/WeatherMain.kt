package com.kunangkunang.app.model.weather

import com.google.gson.annotations.SerializedName

data class WeatherMain (
    @SerializedName("temp")
    val temp: Double? = null,

    @SerializedName("feels_like")
    val feelsLike: Double? = null
)