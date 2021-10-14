package com.kunangkunang.app.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReviewDataSection(
    @SerializedName("subSection")
    @Expose
    var subSection: String? = null,

    @SerializedName("subData")
    @Expose
    var subData: ReviewDataSubSection? = null
)