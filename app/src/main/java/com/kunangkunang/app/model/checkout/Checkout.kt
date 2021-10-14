package com.kunangkunang.app.model.checkout

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Checkout(
    @SerializedName("no_transaction")
    @Expose
    val transactionNo: String? = null
)