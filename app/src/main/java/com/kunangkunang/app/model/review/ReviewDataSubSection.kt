package com.kunangkunang.app.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReviewDataSubSection(
    @SerializedName("excelent")
    @Expose
    var excellent: Int? = 0,

    @SerializedName("good")
    @Expose
    var good: Int? = 0,

    @SerializedName("fair")
    @Expose
    var fair: Int? = 0,

    @SerializedName("poor")
    @Expose
    var poor: Int? = 0,

    @SerializedName("no")
    @Expose
    var no: Int? = 0,

    @SerializedName("yes")
    @Expose
    var yes: Int? = 0
)