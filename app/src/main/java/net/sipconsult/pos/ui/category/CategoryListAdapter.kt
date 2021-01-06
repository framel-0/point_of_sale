package net.sipconsult.pos.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.ProductCategoryItem
import net.sipconsult.pos.databinding.CategoryListItemBinding

class CategoryListAdapter(private val onCategoryClick: (Int) -> Unit) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    private var _categories: List<ProductCategoryItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            CategoryListItemBinding.inflate(
                layoutInflater,
                parent,
                false,
                R.layout.category_list_item
            )
//        val itemView = layoutInflater.inflate(R.layout.list_item_cart, parent, false)

        return CategoryViewHolder(binding, onCategoryClick)
    }

    override fun getItemCount(): Int = _categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = _categories[position]

        holder.bind(category, position)
    }

    fun setCategories(categories: List<ProductCategoryItem>) {
        _categories = categories
        notifyDataSetChanged()
    }
}