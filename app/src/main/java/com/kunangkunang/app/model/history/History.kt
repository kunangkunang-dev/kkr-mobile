package com.kunangkunang.app.model.history

import com.google.gson.annotations.SerializedName

data class History (
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: List<HistoryData?>? = null
)