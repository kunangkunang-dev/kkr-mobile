package com.kunangkunang.app.model.weather

import com.google.gson.annotations.SerializedName

data class WeatherData (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("main")
    val main: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("icon")
    val icon: String? = null
)