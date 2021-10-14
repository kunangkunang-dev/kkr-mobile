package com.kunangkunang.app.model.room

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("data")
    val data: List<RoomData?>? = null
)