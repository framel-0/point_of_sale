package net.sipconsult.pos.ui.payment.paymentmethod.mobilemoney

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.MobileMoneyItem

class MobileMoneyListAdapter(private val onMobileMoneyClick: (Int) -> Unit) :
    RecyclerView.Adapter<MobileMoneyViewHolder>() {

    private var _mobileMoneys = arrayListOf<MobileMoneyItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MobileMoneyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item_mobile_money, parent, false)

        return MobileMoneyViewHolder(itemView, onMobileMoneyClick)
    }

    override fun getItemCount(): Int = _mobileMoneys.size

    override fun onBindViewHolder(holder: MobileMoneyViewHolder, position: Int) {
        val mobileMoney = _mobileMoneys[position]
        holder.bind(position, mobileMoney)
    }

    fun setMobileMoney(mobileMoneys: ArrayList<MobileMoneyItem>) {
        _mobileMoneys = mobileMoneys
        notifyDataSetChanged()

    }
}