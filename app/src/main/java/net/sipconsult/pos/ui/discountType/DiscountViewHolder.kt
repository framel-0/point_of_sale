package net.sipconsult.pos.ui.discountType

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_discount_type.view.*
import net.sipconsult.pos.data.models.DiscountTypesItem

class DiscountViewHolder(
    itemView: View,
    onDiscountTypeClick: (DiscountTypesItem) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private lateinit var _discountTypesItem: DiscountTypesItem
    fun bind(discountTypesItem: DiscountTypesItem, position: Int) {
        _discountTypesItem = discountTypesItem

        itemView.textDiscountName.text = discountTypesItem.name
        itemView.textDiscountPercentage.text = discountTypesItem.percentageStr
    }

    init {
        itemView.setOnClickListener { onDiscountTypeClick(_discountTypesItem) }
    }
}