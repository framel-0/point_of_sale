package net.sipconsult.pos.data.models

import com.google.gson.annotations.SerializedName

class RefundTransactionPostProduct(
    @SerializedName("productCode")
    val productCode: String,
    @SerializedName("quantity")
    val quantity: Int
)