package com.kunangkunang.app.model.history

import com.google.gson.annotations.SerializedName

data class HistoryData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("no_transaction")
    val transactionNo: String? = null,

    @SerializedName("room_id")
    val roomId: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("customer_id")
    val customerId: Int? = null,

    @SerializedName("created_at")
    val orderDate: String? = null,

    @SerializedName("details")
    val details: List<HistoryDataDetails?>? = null
)