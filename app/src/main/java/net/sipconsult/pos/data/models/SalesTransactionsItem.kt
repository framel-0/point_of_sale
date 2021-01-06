package net.sipconsult.pos.data.models


import com.google.gson.annotations.SerializedName

data class SalesTransactionsItem(
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("discountType")
    val discountType: DiscountTypesItem?,
    @SerializedName("locationCodeNavigation")
    val location: LocationsItem?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("totalSales")
    val totalSales: Double,
    @SerializedName("operator")
    val operator: Operator,
    @SerializedName("receiptNumber")
    val receiptNumber: String,
    @SerializedName("salesAgent")
    val salesAgentsItem: SalesAgentsItem,
    @SerializedName("salesTransactionPaymentMethod")
    val salesTransactionPaymentMethod: List<SalesTransactionPaymentMethod>,
    @SerializedName("salesTransactionProduct")
    val salesTransactionProduct: List<SalesTransactionProduct>
) {
    val totalSalesStr: String
        get() {

            return totalSales.toString()
        }
}