package com.kunangkunang.app.model.item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ItemDetails(
    @SerializedName("id")
    @Expose
    val id: Int? = null,

    @SerializedName("id_checklist")
    @Expose
    val checklistId: Int? = null,

    @SerializedName("item_name")
    @Expose
    val itemName: String? = null,

    @SerializedName("quantity")
    @Expose
    val quantity: Int? = null,

    @SerializedName("description")
    @Expose
    val description: String? = null,

    @SerializedName("available")
    @Expose
    val available: Int? = null
)