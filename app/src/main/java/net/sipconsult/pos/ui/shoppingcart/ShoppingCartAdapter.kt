package net.sipconsult.pos.ui.shoppingcart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.CartItem

class ShoppingCartAdapter(
    private val onSubClick: (CartItem) -> Unit,
    private val onAddClick: (CartItem) -> Unit,
    private val onDeleteClick: (CartItem) -> Unit
) : RecyclerView.Adapter<ShoppingCartViewHolder>() {

    private var _cartItems: List<CartItem> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding =
//            CartListItemBinding.inflate(layoutInflater)
        val itemView = layoutInflater.inflate(R.layout.list_item_cart, parent, false)

        return ShoppingCartViewHolder(
            itemView,
            onSubClick,
            onAddClick,
            onDeleteClick
        )
    }

    fun setCartItems(cartItems: List<CartItem>) {
        _cartItems = cartItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = _cartItems.size

    override fun onBindViewHolder(holder: ShoppingCartViewHolder, position: Int) {
        val cartItem = _cartItems[position]

        holder.bind(cartItem, position)

    }
}