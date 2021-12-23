package com.kunangkunang.app.model.login

import com.google.gson.annotations.SerializedName

data class StaffRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("password")
    val password: String? = null

)