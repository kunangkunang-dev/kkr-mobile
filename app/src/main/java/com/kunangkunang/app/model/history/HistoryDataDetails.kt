package com.kunangkunang.app.model.history

import com.google.gson.annotations.SerializedName

data class HistoryDataDetails(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("header_id")
    val headerId: Int? = null,

    @SerializedName("item_name")
    val itemName: String? = null,

    @SerializedName("item_id")
    val itemId: Int? = null,

    @SerializedName("category_id")
    val categoryId: Int? = null,

    @SerializedName("qty")
    val qty: Int? = null,

    @SerializedName("models")
    val models: String? = null,

    @SerializedName("child_spa_id")
    val childSpaId: Int? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("spa_start")
    val spaStart: String? = null,

    @SerializedName("spa_end")
    val spaEnd: String? = null,

    @SerializedName("spa_schedule")
    val spaSchedule: HistoryDataDetailsSpa? = null,

    @SerializedName("notes")
    val notes: String? = null
)