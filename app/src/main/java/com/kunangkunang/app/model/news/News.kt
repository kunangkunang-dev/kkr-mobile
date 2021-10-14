package com.kunangkunang.app.model.news

import com.google.gson.annotations.SerializedName

data class News (
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: List<NewsData?>? = null
)