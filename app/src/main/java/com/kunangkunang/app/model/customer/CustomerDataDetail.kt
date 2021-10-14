package com.kunangkunang.app.model.customer

import com.google.gson.annotations.SerializedName

data class CustomerDataDetail(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("ktp")
    val ktp: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("nationaly")
    val nationality: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("company")
    val company: String? = null,

    @SerializedName("benefit")
    val benefit: String? = null,

    @SerializedName("place_issue")
    val placeIssue: String? = null,

    @SerializedName("card_number")
    val cardNumber: String? = null,

    @SerializedName("place_birth")
    val placeBirth: String? = null,

    @SerializedName("date_birth")
    val dateBirth: String? = null,

    @SerializedName("payment_method")
    val paymentMethod: String? = null,

    @SerializedName("date_issue")
    val dateIssue: String? = null,

    @SerializedName("cc_expired")
    val ccExpired: String? = null,

    @SerializedName("expired")
    val expired: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("street")
    val street: String? = null,

    @SerializedName("postal_code")
    val postalCode: String? = null,

    @SerializedName("code")
    val code: String? = null
)