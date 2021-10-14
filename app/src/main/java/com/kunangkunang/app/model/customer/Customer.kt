package com.kunangkunang.app.model.customer

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: CustomerData? = null
)