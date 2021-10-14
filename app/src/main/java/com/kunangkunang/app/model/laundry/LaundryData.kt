package com.kunangkunang.app.model.laundry

import com.google.gson.annotations.SerializedName

data class LaundryData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("image")
    val image: String? = null
)