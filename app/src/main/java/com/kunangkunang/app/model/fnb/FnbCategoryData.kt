package com.kunangkunang.app.model.fnb

import com.google.gson.annotations.SerializedName

data class FnbCategoryData (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("food")
    val food: List<FnbCategoryDataFood?>? = null
)