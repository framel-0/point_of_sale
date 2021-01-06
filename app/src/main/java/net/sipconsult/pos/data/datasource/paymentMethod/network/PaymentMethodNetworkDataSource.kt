package net.sipconsult.pos.data.datasource.paymentMethod.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.Voucher
import net.sipconsult.pos.data.network.response.PaymentMethods
import net.sipconsult.pos.internal.Result

interface PaymentMethodNetworkDataSource {
    val downloadPaymentMethods: LiveData<PaymentMethods>

    suspend fun fetchPaymentMethods()

    suspend fun getVoucher(code: String): Result<Voucher>
}