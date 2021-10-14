package com.kunangkunang.app.model.login

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("room_number")
    val roomNumber: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("checklist_id")
    val checklistId: Int? = null,

    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("checklist")
    val checklist: LoginDataChecklist? = null
)