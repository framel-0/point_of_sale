package net.sipconsult.pos.data.datasource.paymentMethod.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.PaymentMethodItem

interface PaymentMethodLocalDataSource {

    val paymentMethods: LiveData<List<PaymentMethodItem>>

    fun updatePaymentMethods(paymentMethods: List<PaymentMethodItem>)
}