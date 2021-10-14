package com.kunangkunang.app.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReviewInfo(
    @SerializedName("friends")
    @Expose
    var friends: Int? = 0,

    @SerializedName("travelAgent")
    @Expose
    var travelAgent: Int? = 0,

    @SerializedName("bookingOnline")
    @Expose
    var onlineBooking: Int? = 0,

    @SerializedName("others")
    @Expose
    var others: Int? = 0
)