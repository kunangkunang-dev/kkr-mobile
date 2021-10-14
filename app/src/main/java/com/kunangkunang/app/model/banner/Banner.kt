package com.kunangkunang.app.model.banner

import com.google.gson.annotations.SerializedName

data class Banner(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val image: List<BannerData?>? = null
)