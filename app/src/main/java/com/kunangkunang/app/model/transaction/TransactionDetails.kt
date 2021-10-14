package com.kunangkunang.app.model.transaction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionDetails(
    @SerializedName("item_id")
    @Expose
    val itemId: Int? = null,

    @SerializedName("item_name")
    @Expose
    val itemName: String? = null,

    @SerializedName("category_id")
    @Expose
    val categoryId: Int? = null,

    @SerializedName("qty")
    @Expose
    val qty: Int? = null,

    @SerializedName("child_spa_id")
    @Expose
    val spaId: Int? = null,

    @SerializedName("spa_start")
    @Expose
    val spaStart: String? = null,

    @SerializedName("spa_end")
    @Expose
    val spaEnd: String? = null,

    @SerializedName("price")
    @Expose
    val price: Int? = null
)