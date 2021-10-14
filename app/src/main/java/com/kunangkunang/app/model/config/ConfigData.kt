package com.kunangkunang.app.model.config

import com.google.gson.annotations.SerializedName

data class ConfigData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("food_image")
    val fnbImage: String? = null,

    @SerializedName("food_icon")
    val fnbIcon: String? = null,

    @SerializedName("laundry_image")
    val laundryImage: String? = null,

    @SerializedName("laundry_icon")
    val laundryIcon: String? = null,

    @SerializedName("spa_image")
    val spaImage: String? = null,

    @SerializedName("spa_icon")
    val spaIcon: String? = null,

    @SerializedName("amenities_image")
    val amenitiesImage: String? = null,

    @SerializedName("amenities_icon")
    val amenitiesIcon: String? = null,

    @SerializedName("system_password")
    val systemPassword: String? = null,

    @SerializedName("general_icon")
    val generalIcon: String? = null,

    @SerializedName("business_name")
    val businessName: String? = null,

    @SerializedName("address_business")
    val businessAddress: String? = null
)