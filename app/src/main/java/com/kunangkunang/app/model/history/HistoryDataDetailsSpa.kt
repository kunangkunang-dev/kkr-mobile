package com.kunangkunang.app.model.history

import com.google.gson.annotations.SerializedName

data class HistoryDataDetailsSpa(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("spa_id")
    val spaId: Int? = null,

    @SerializedName("from")
    val from: String? = null,

    @SerializedName("to")
    val to: String? = null,

    @SerializedName("days")
    val days: String? = null
)