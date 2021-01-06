package net.sipconsult.pos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sipconsult.pos.data.provider.LocationProvider
import net.sipconsult.pos.data.provider.PosNumberProvider
import net.sipconsult.pos.data.repository.discountType.DiscountTypeRepository
import net.sipconsult.pos.data.repository.location.LocationRepository
import net.sipconsult.pos.data.repository.paymentMethod.PaymentMethodRepository
import net.sipconsult.pos.data.repository.transaction.TransactionRepository
import net.sipconsult.pos.data.repository.user.UserRepository

class SharedViewModelFactory(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val discountTypeRepository: DiscountTypeRepository,
    private val locationProvider: LocationProvider,
    private val posNumberProvider: PosNumberProvider,
    private val locationRepository: LocationRepository,
    val context: Context
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SharedViewModel(
            paymentMethodRepository,
            transactionRepository,
            userRepository,
            discountTypeRepository,
            locationProvider,
            posNumberProvider,
            locationRepository,
            context
        ) as T
    }
}