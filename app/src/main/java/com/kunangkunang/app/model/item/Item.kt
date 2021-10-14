package com.kunangkunang.app.model.item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("room_id")
    @Expose
    val roomId: Int? = null,

    @SerializedName("checkin_id")
    @Expose
    val checkinId: Int? = null,

    @SerializedName("employee_name")
    @Expose
    val employeeName: String? = null,

    @SerializedName("password")
    @Expose
    val password: String? = null,

    @SerializedName("details")
    @Expose
    val details: List<ItemDetails?>? = null
)