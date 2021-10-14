package com.kunangkunang.app.model.config

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: ConfigData? = null
)