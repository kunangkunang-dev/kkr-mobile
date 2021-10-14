package com.kunangkunang.app.model.news

import com.google.gson.annotations.SerializedName

data class NewsData (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("createdAt")
    val date: String? = null
)