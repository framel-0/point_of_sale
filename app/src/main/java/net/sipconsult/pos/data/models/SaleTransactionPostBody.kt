package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class SaleTransactionPostBody(
    @SerializedName("OperatorUserId")
    val operatorUserId: String,
    @SerializedName("SalesAgentUserId")
    val salesAgentUserId: String,
    @SerializedName("LocationCode")
    val locationCode: String,
    @SerializedName("ReceiptNumber")
    val receiptNumber: String,
    @SerializedName("Description")
    val description: String = "",
    @SerializedName("DiscountTypeId")
    var discountTypeId: Int? = null,
    @SerializedName("VoucherId")
    var voucherId: Int? = null,
    @SerializedName("DeliveryCost")
    var deliveryCost: Double? = null,
    @SerializedName("SalesTransactionPaymentMethod")
    val salesTransactionPaymentMethod: List<SalesTransactionPostPaymentMethod>,
    @SerializedName("SalesTransactionProduct")
    val salesTransactionProduct: List<SalesTransactionPostProduct>
)