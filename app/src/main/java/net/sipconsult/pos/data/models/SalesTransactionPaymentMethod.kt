package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class SalesTransactionPaymentMethod(
    @SerializedName("id")
    val id: Int,
    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethodItem,
    @SerializedName("transactionId")
    val transactionId: Int
)