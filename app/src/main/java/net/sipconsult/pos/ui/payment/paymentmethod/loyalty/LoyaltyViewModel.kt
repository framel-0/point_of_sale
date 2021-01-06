package net.sipconsult.pos.ui.payment.paymentmethod.loyalty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.Voucher
import net.sipconsult.pos.data.repository.paymentMethod.PaymentMethodRepository
import net.sipconsult.pos.internal.Result
import net.sipconsult.pos.internal.lazyDeferred

class LoyaltyViewModel(private val paymentMethodRepository: PaymentMethodRepository) : ViewModel() {

    var voucherCode: String = ""

    private val _voucherResult = MutableLiveData<VoucherResult>()
    val voucherResult: LiveData<VoucherResult> = _voucherResult

    val getVoucher by lazyDeferred {

        // can be launched in a separate asynchronous job
        paymentMethodRepository.getVoucher(voucherCode)

    }

    fun updateVoucherResult(result: Result<Voucher>) {
        if (result is Result.Success) {
            _voucherResult.value =
                VoucherResult(
                    success = result.data
                )
        } else {
            _voucherResult.value =
                VoucherResult(error = R.string.voucher_failed)
        }

    }


}