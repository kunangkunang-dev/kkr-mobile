package com.kunangkunang.app.model.fnb

import com.google.gson.annotations.SerializedName

data class FnbCategory (
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: List<FnbCategoryData?>? = null
)