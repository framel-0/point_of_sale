package net.sipconsult.pos.ui.category

import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.data.models.ProductCategoryItem
import net.sipconsult.pos.databinding.CategoryListItemBinding

class CategoryViewHolder(
    itemBinding: CategoryListItemBinding,
    onCategoryClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {

    private var category = itemBinding.category
    private var pos: Int = 0
    fun bind(category: ProductCategoryItem, position: Int) {
        pos = position
        this.category = category
    }

    init {
        itemBinding.root.setOnClickListener {
            onCategoryClick(pos)
        }
    }
}