package com.kunangkunang.app.model.status

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("message")
    val message: String? = null
)