package net.sipconsult.pos.ui.payment

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_payment_method.view.*
import net.sipconsult.pos.data.models.PaymentMethodItem

class PaymentMethodViewHolder(
    itemView: View,
    onPaymentMethodClick: (PaymentMethodItem) -> Unit,
    private val selectedPaymentMethod: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private lateinit var paymentMethod: PaymentMethodItem
    private var paymentMethodPosition = 0
    fun bind(
        paymentMethod: PaymentMethodItem,
        position: Int
    ) {
        paymentMethodPosition = position
        this.paymentMethod = paymentMethod
        itemView.textPaymentMethodName.text = paymentMethod.name
        itemView.checkBoxPaymentMethod.isChecked = paymentMethod.isSelected
    }

    init {

//        itemView.checkBoxPaymentMethod.setOnClickListener {
//            selectedPaymentMethod(paymentMethodPosition)
//        }

        itemView.checkBoxPaymentMethod.setOnClickListener {
            val cb = it as CheckBox
            selectedPaymentMethod(paymentMethodPosition, cb.isChecked)

        }

        itemView.textPaymentMethodName.setOnClickListener {
            onPaymentMethodClick(paymentMethod)
        }
    }
}