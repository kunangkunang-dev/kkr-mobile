package com.kunangkunang.app.model.login

import com.google.gson.annotations.SerializedName

data class Staff(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: String? = null
)