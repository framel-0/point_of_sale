package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class Voucher(
    @SerializedName("code")
    val code: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("value")
    val value: Double,
    @SerializedName("valueRemains")
    val valueRemains: Double
)