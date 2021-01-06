package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class SalesTransactionProduct(
    @SerializedName("id")
    val id: Int,
    @SerializedName("productCodeNavigation")
    val product: SProductItem,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("tansactionId")
    val tansactionId: Int
)