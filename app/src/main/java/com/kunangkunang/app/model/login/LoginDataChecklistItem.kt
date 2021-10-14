package com.kunangkunang.app.model.login

import com.google.gson.annotations.SerializedName

data class LoginDataChecklistItem(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("id_checklist")
    val checklistId: Int? = null,

    @SerializedName("item_name")
    val itemName: String? = null,

    @SerializedName("quantity")
    val quantity: Int? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("state")
    var state: Boolean = false
)