package com.kunangkunang.app.model.news

import com.google.gson.annotations.SerializedName

data class DetailNewsData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("content")
    val content: String? = null
)