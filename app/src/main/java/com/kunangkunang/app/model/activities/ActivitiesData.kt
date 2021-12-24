package com.kunangkunang.app.model.activities

import com.google.gson.annotations.SerializedName

data class ActivitiesData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("description")
    val description: String? = null
)