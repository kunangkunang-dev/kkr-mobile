package com.kunangkunang.app.model.transaction

import com.google.gson.annotations.SerializedName

data class TransactionResponse (
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: List<TransactionResponse?>?

)