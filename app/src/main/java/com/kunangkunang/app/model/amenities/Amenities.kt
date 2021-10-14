package com.kunangkunang.app.model.amenities

import com.google.gson.annotations.SerializedName

data class Amenities (
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: List<AmenitiesData?>? = null
)