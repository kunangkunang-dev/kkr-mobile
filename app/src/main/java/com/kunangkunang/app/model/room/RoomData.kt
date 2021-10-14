package com.kunangkunang.app.model.room

import com.google.gson.annotations.SerializedName

data class RoomData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("room_number")
    val roomNumber: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("checklist_id")
    val checkListId: Int? = null,

    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("password")
    val password: String? = null
)