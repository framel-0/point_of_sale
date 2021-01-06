package net.sipconsult.pos.ui.payment

data class TransactionResult(
    val success: Boolean? = null,
    val error: Int? = null
)
