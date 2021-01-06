package net.sipconsult.pos.data.repository.transaction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sipconsult.pos.data.datasource.transaction.local.TransactionLocalDataSource
import net.sipconsult.pos.data.datasource.transaction.network.TransactionNetworkDataSource
import net.sipconsult.pos.data.models.RefundTransactionPostBody
import net.sipconsult.pos.data.models.SaleTransactionPostBody
import net.sipconsult.pos.data.models.SalesTransactionsItem
import net.sipconsult.pos.data.network.response.SalesTransactions
import net.sipconsult.pos.data.network.response.TransactionResponse
import net.sipconsult.pos.internal.Result

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource,
    private val networkDataSource: TransactionNetworkDataSource
) :
    TransactionRepository {

    override suspend fun postTransaction(body: SaleTransactionPostBody): Result<TransactionResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.postTransaction(body)
        }
    }

    override suspend fun postRefundTransaction(body: RefundTransactionPostBody): Result<TransactionResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.postRefundTransaction(body)
        }
    }

    override suspend fun fetchSaleTransaction(transactionId: Int): Result<SalesTransactionsItem> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.fetchSaleTransaction(transactionId)
        }
    }

    override suspend fun fetchSaleTransactions(operatorId: String): Result<SalesTransactions> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.fetchSaleTransactions(operatorId)
        }
    }

    override suspend fun fetchLocationSaleTransactions(locationCode: String): Result<SalesTransactions> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.fetchLocationSaleTransactions(locationCode)
        }
    }
}