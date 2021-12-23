package com.kunangkunang.app.model.spa

import com.google.gson.annotations.SerializedName

data class SpaDataSchedules(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("spa_id")
    val spaId: Int? = null,

    @SerializedName("from")
    val from: String? = null,

    @SerializedName("to")
    val to: String? = null,

    @SerializedName("days")
    val days: String? = null,

    val price: Int? = null,

    @SerializedName("spa_availability")
    var spaAvailability: Int? = null

)