package com.kunangkunang.app.model.transaction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("transaction")
    @Expose
    val transactionData: TransactionData
)