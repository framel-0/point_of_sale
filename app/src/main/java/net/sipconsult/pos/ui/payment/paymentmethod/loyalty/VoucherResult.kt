package net.sipconsult.pos.ui.payment.paymentmethod.loyalty

import net.sipconsult.pos.data.models.Voucher

data class VoucherResult(
    val success: Voucher? = null,
    val error: Int? = null
)