package net.sipconsult.pos.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.PaymentMethodItem

class PaymentMethodListAdapter(
    private val onSelectPaymentMethodClick: (PaymentMethodItem) -> Unit,
    private val onCheckPaymentMethodClick: (ArrayList<PaymentMethodItem>) -> Unit
) :
    RecyclerView.Adapter<PaymentMethodViewHolder>() {

    private var _paymentMethods = arrayListOf<PaymentMethodItem>()
    var selectedPaymentMethods = arrayListOf<PaymentMethodItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item_payment_method, parent, false)
        return PaymentMethodViewHolder(
            itemView,
            onSelectPaymentMethodClick,
            ::selectedPaymentMethod
        )
    }

    override fun getItemCount(): Int = _paymentMethods.size

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val paymentMethod = _paymentMethods[position]

        holder.bind(paymentMethod, position)
    }

    fun setPaymentMethods(paymentMethods: ArrayList<PaymentMethodItem>) {
        _paymentMethods = paymentMethods
        notifyDataSetChanged()
    }

    private fun selectedPaymentMethod(position: Int, isChecked: Boolean) {
        val paymentMethod = _paymentMethods[position]

        if (isChecked) {
            paymentMethod.isSelected = true
            selectedPaymentMethods.add(paymentMethod)
        } else {
            paymentMethod.isSelected = false
            selectedPaymentMethods.remove(paymentMethod)
        }
        onCheckPaymentMethodClick(selectedPaymentMethods)

    }

}