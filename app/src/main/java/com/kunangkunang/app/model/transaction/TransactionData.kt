package com.kunangkunang.app.model.transaction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionData(
    @SerializedName("room_id")
    @Expose
    val roomId: Int,

    @SerializedName("notes")
    @Expose
    val notes: String,

    @SerializedName("category")
    @Expose
    val categoryId: String,

    @SerializedName("created_at")
    @Expose
    val createdAt: String,

    @SerializedName("customer_id")
    @Expose
    val customerId: Int,

    @SerializedName("total")
    @Expose
    val total: Int,

    @SerializedName("checkin_number")
    @Expose
    val checkinNumber: String,

    @SerializedName("details")
    @Expose
    val details: List<TransactionDetails>
)