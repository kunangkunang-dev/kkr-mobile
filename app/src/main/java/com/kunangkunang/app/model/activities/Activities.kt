package com.kunangkunang.app.model.activities

import com.google.gson.annotations.SerializedName
import com.kunangkunang.app.model.amenities.AmenitiesData

data class Activities (
    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("data")
    val data: List<ActivitiesData?>? = null
)