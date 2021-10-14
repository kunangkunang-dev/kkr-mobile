package com.kunangkunang.app.model.logout

import com.google.gson.annotations.SerializedName

data class Logout(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("message")
    val message: String? = null
)