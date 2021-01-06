package net.sipconsult.pos.data.models

import com.google.gson.annotations.SerializedName

data class MobileMoneyItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String
)