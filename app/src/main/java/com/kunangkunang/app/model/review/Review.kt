package com.kunangkunang.app.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("no_transaction")
    @Expose
    var transactionNo: String? = null,

    @SerializedName("review")
    @Expose
    var review: List<ReviewData?>? = null,

    @SerializedName("getInfoHotel")
    @Expose
    var info: ReviewInfo? = null,

    @SerializedName("comments")
    @Expose
    var comments: String? = null
)