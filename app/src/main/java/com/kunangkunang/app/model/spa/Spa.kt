package com.kunangkunang.app.model.spa

import com.google.gson.annotations.SerializedName

data class Spa (
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: List<SpaData?>? = null
)