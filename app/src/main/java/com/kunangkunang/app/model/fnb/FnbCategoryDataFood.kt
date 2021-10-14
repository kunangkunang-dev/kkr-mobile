package com.kunangkunang.app.model.fnb

import com.google.gson.annotations.SerializedName

data class FnbCategoryDataFood (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("category_id")
    val categoryId: Int? = null,

    @SerializedName("image")
    val image: String? = null
)