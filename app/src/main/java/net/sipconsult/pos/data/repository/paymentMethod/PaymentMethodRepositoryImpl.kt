package net.sipconsult.pos.data.repository.paymentMethod

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sipconsult.pos.data.datasource.paymentMethod.local.PaymentMethodLocalDataSource
import net.sipconsult.pos.data.datasource.paymentMethod.network.PaymentMethodNetworkDataSource
import net.sipconsult.pos.data.models.PaymentMethodItem
import net.sipconsult.pos.data.models.Voucher
import net.sipconsult.pos.internal.Result

class PaymentMethodRepositoryImpl(
    private val networkDataSource: PaymentMethodNetworkDataSource,
    private val localDataSource: PaymentMethodLocalDataSource
) :
    PaymentMethodRepository {
    init {
        networkDataSource.downloadPaymentMethods.observeForever { currentPaymentMethods ->
            persistFetchedPaymentMethods(currentPaymentMethods)
        }
    }

    override suspend fun getPaymentMethods(): LiveData<List<PaymentMethodItem>> {

        return withContext(Dispatchers.IO) {
            initPaymentMethodsData()
            return@withContext localDataSource.paymentMethods
        }
    }

    override suspend fun getVoucher(code: String): Result<Voucher> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.getVoucher(code)
        }
    }

    private fun persistFetchedPaymentMethods(fetchedProducts: List<PaymentMethodItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            localDataSource.updatePaymentMethods(fetchedProducts)
        }
    }

    private suspend fun initPaymentMethodsData() {
        fetchPaymentMethods()

    }

    private suspend fun fetchPaymentMethods() {
        networkDataSource.fetchPaymentMethods()
    }


}