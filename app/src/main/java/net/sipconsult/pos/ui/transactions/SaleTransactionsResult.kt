package net.sipconsult.pos.ui.transactions

import net.sipconsult.pos.data.network.response.SalesTransactions


data class SaleTransactionsResult(
    val success: SalesTransactions? = null,
    val error: Int? = null
)