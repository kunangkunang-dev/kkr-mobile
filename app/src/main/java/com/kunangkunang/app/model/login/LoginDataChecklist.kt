package com.kunangkunang.app.model.login

import com.google.gson.annotations.SerializedName

data class LoginDataChecklist(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("items")
    val item: List<LoginDataChecklistItem?>? = null

)