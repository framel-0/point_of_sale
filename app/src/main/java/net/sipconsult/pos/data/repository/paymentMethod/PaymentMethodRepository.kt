package net.sipconsult.pos.data.repository.paymentMethod

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.PaymentMethodItem
import net.sipconsult.pos.data.models.Voucher
import net.sipconsult.pos.internal.Result

interface PaymentMethodRepository {
    suspend fun getPaymentMethods(): LiveData<List<PaymentMethodItem>>
    suspend fun getVoucher(code: String): Result<Voucher>
}