package com.kunangkunang.app.model.login

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("room_id")
    val roomId: Int? = null,

    @SerializedName("password")
    val password: String? = null

)