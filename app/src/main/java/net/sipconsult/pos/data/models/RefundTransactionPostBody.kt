package net.sipconsult.pos.data.models

import com.google.gson.annotations.SerializedName

data class RefundTransactionPostBody(
    @SerializedName("salesTransactionId")
    val salesTransactionId: Int,
    @SerializedName("operatorUserId")
    val operatorUserId: String,
    @SerializedName("ReceiptNumber")
    val receiptNumber: String,
    @SerializedName("locationCode")
    val locationCode: String,
    @SerializedName("refundTransactionPaymentMethod")
    val refundTransactionPaymentMethod: List<RefundTransactionPostPaymentMethod>,
    @SerializedName("refundTransactionProduct")
    val refundTransactionProduct: List<RefundTransactionPostProduct>
)