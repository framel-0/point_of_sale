package net.sipconsult.pos.ui.shoppingcart

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_cart.view.*
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.CartItem
import net.sipconsult.pos.internal.glide.GlideApp

class ShoppingCartViewHolder(
    itemView: View,
    onSubClick: (CartItem) -> Unit,
    onAddClick: (CartItem) -> Unit,
    onDeleteClick: (CartItem) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var cartItemPosition: Int = 0
    private lateinit var _cartItem: CartItem

    fun bind(cartItem: CartItem, position: Int) {
        cartItemPosition = position
        _cartItem = cartItem
        itemView.textProductName.text = cartItem.product.description
        itemView.textProductDescription.visibility = View.GONE

        itemView.textQuantity.text = cartItem.getProductQuantityString()

        itemView.textProductSalesPrice.text = cartItem.product.displaySalesPrice()

        GlideApp.with(itemView.context).load(cartItem.product.imageUrl)
            .placeholder(R.drawable.juben_logo_landscape).into(itemView.imageProduct)
    }

    init {
        itemView.imageButtonDelete.setOnClickListener {
            onDeleteClick(_cartItem)
        }
        itemView.imageButtonSub.setOnClickListener {
            onSubClick(_cartItem)
        }
        itemView.imageButtonAdd.setOnClickListener {
            onAddClick(_cartItem)
        }
    }


}