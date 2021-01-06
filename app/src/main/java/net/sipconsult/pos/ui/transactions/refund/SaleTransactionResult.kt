package net.sipconsult.pos.ui.transactions.refund

import net.sipconsult.pos.data.models.SalesTransactionsItem


data class SaleTransactionResult(
    val success: SalesTransactionsItem? = null,
    val error: Int? = null
)