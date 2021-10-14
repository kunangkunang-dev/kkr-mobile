package com.kunangkunang.app.model.customer

import com.google.gson.annotations.SerializedName

data class CustomerData(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("order_number")
    val orderNumber: String? = null,

    @SerializedName("room_id")
    val roomId: Int? = null,

    @SerializedName("customer_id")
    val customerId: Int? = null,

    @SerializedName("check_in_date")
    val checkInDate: String? = null,

    @SerializedName("check_out_date")
    val checkOutDate: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("days_of_stay")
    val daysOfStay: Int? = null,

    @SerializedName("customer")
    val customer: CustomerDataDetail? = null
)