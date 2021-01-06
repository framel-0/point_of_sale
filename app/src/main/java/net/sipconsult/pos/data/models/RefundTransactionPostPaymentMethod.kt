package net.sipconsult.pos.data.models

import com.google.gson.annotations.SerializedName

class RefundTransactionPostPaymentMethod(
    @SerializedName("paymentMethodId")
    val paymentMethodId: Int,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String = ""
)