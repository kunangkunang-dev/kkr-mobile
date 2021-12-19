package com.kunangkunang.app.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReviewDataSubSection(
    @SerializedName("excelent")
    @Expose
    var excellent: Int? = null,

    @SerializedName("good")
    @Expose
    var good: Int? = null,

    @SerializedName("fair")
    @Expose
    var fair: Int? = null,

    @SerializedName("poor")
    @Expose
    var poor: Int? = null,

    @SerializedName("no")
    @Expose
    var no: Int? = null,

    @SerializedName("yes")
    @Expose
    var yes: Int? = null
)