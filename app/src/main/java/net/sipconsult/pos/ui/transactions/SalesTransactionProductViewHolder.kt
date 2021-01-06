package net.sipconsult.pos.ui.transactions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_product.view.*
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.SalesTransactionProduct
import net.sipconsult.pos.internal.glide.GlideApp

class SalesTransactionProductViewHolder(
    itemView: View,
    onProductClick: (SalesTransactionProduct) -> Unit
) :
    RecyclerView.ViewHolder(itemView) {

    private var productPosition: Int = 0
    private lateinit var _transactionProduct: SalesTransactionProduct

    fun bind(transactionProduct: SalesTransactionProduct, position: Int) {
        productPosition = position
        _transactionProduct = transactionProduct
        itemView.textProductName.text = transactionProduct.product.description

        itemView.textProductSalesPrice.text = transactionProduct.product.displaySalesPrice()


//        GlideApp.with(itemView.context).load(R.drawable.juben_logo).into(itemView.imageProductName)

        GlideApp.with(itemView.context).load(transactionProduct.product.imageUrl)
            .placeholder(R.drawable.juben_logo_landscape).into(itemView.imageProductName)
    }

    init {
        itemView.setOnClickListener {
            onProductClick(_transactionProduct)
        }
    }
}