package net.sipconsult.pos.ui.payment.paymentmethod.mobilemoney

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_mobile_money.view.*
import net.sipconsult.pos.data.models.MobileMoneyItem

class MobileMoneyViewHolder(
    itemView: View,
    onMobileMoneyClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var mobileMoneyPosition = 0

    fun bind(position: Int, mobileMoney: MobileMoneyItem) {
        mobileMoneyPosition = position
        itemView.textMobileMoneyMethod.text = mobileMoney.name
    }

    init {
        itemView.setOnClickListener {
            onMobileMoneyClick(mobileMoneyPosition)
        }
    }

}