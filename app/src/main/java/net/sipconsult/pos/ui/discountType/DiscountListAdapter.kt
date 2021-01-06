package net.sipconsult.pos.ui.discountType

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.DiscountTypesItem

class DiscountListAdapter(private val onDiscountTypeClick: (DiscountTypesItem) -> Unit) :
    RecyclerView.Adapter<DiscountViewHolder>() {

    private var _discountTypes = arrayListOf<DiscountTypesItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item_discount_type, parent, false)
        return DiscountViewHolder(itemView, onDiscountTypeClick)
    }

    override fun getItemCount(): Int = _discountTypes.size

    override fun onBindViewHolder(holder: DiscountViewHolder, position: Int) {
        val discountTypesItem = _discountTypes[position]

        holder.bind(discountTypesItem, position)
    }

    fun setDiscountTypes(discountTypes: ArrayList<DiscountTypesItem>) {
        _discountTypes = discountTypes
        notifyDataSetChanged()
    }
}