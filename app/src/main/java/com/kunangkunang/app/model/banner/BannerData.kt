package com.kunangkunang.app.model.banner

import com.google.gson.annotations.SerializedName

data class BannerData (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("image")
    val image: String? = null
)