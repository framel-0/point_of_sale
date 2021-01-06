package net.sipconsult.pos.ui.products

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_product.view.*
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.ProductItem
import net.sipconsult.pos.internal.glide.GlideApp

class ProductViewHolder(itemView: View, onProductClick: (ProductItem) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private var productPosition: Int = 0
    private lateinit var _product: ProductItem

    fun bind(product: ProductItem, position: Int) {
        productPosition = position
        _product = product
        itemView.textProductName.text = product.description

        itemView.textProductSalesPrice.text = product.displaySalesPrice()


//        GlideApp.with(itemView.context).load(R.drawable.juben_logo).into(itemView.imageProductName)
        GlideApp.with(itemView.context).load(product.imageUrl)
            .placeholder(R.drawable.juben_logo_landscape).into(itemView.imageProductName)
    }

    init {
        itemView.setOnClickListener {
            onProductClick(_product)
        }
    }
}