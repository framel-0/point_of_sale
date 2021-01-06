package net.sipconsult.pos.data.datasource.transaction.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.RefundTransactionPostBody
import net.sipconsult.pos.data.models.SaleTransactionPostBody
import net.sipconsult.pos.data.models.SalesTransactionsItem
import net.sipconsult.pos.data.network.response.SalesTransactions
import net.sipconsult.pos.data.network.response.TransactionResponse
import net.sipconsult.pos.internal.Result

interface TransactionNetworkDataSource {
    suspend fun postTransaction(body: SaleTransactionPostBody): Result<TransactionResponse>

    suspend fun postRefundTransaction(body: RefundTransactionPostBody): Result<TransactionResponse>

    val downloadSaleTransaction: LiveData<SalesTransactions>

    suspend fun fetchSaleTransaction(transactionId: Int): Result<SalesTransactionsItem>

    suspend fun fetchSaleTransactions(operatorId: String): Result<SalesTransactions>

    suspend fun fetchLocationSaleTransactions(locationCode: String): Result<SalesTransactions>
}