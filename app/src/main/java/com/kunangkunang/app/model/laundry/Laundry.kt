package com.kunangkunang.app.model.laundry

import com.google.gson.annotations.SerializedName

data class Laundry (
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: List<LaundryData?>? = null
)