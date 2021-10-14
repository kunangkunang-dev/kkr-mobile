package com.kunangkunang.app.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReviewData(
    @SerializedName("sectionName")
    @Expose
    var sectionName: String? = null,

    @SerializedName("data")
    @Expose
    var data: List<ReviewDataSection?>? = null
)