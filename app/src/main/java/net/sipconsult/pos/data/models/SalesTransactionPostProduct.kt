package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class SalesTransactionPostProduct(
    @SerializedName("productCode")
    val productCode: String,
    @SerializedName("quantity")
    val quantity: Int
)