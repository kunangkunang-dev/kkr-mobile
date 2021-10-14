package com.kunangkunang.app.model.news

import com.google.gson.annotations.SerializedName

data class DetailNews(
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: DetailNewsData? = null
)