package net.sipconsult.pos.data.repository.shoppingCart

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.paperdb.Paper
import net.sipconsult.pos.data.models.CartItem
import java.text.DecimalFormat

object ShoppingCartRepository {

    private const val CART: String = "cart"
    private const val REFUND_CART: String = "refund_cart"

    val decimalFormater = DecimalFormat("0.00")


    private val _emptyCartItems = MutableLiveData<MutableList<CartItem>>()
    val emptyCartItems: LiveData<MutableList<CartItem>>
        get() = _emptyCartItems

    private val _cartItems = MutableLiveData<MutableList<CartItem>>()
    val cartItems: LiveData<MutableList<CartItem>>
        get() = _cartItems

    private val _refundCartItems = MutableLiveData<MutableList<CartItem>>()
    val refundCartItems: LiveData<MutableList<CartItem>>
        get() = _refundCartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double>
        get() = _totalPrice

    private val _refundTotalPrice = MutableLiveData<Double>()
    val refundTotalPrice: LiveData<Double>
        get() = _refundTotalPrice

    val totalCartPrice: Double
        get() = getCartPrice()

    val refundTotalCartPrice: Double
        get() = getRefundCartPrice()

    private val _totalQuantity = MutableLiveData<Int>()

    val totalQuantity: LiveData<Int>
        get() = _totalQuantity

    init {
        setCartItems()
        setRefundCartItems()

        setTotalPrice()
        setRefundTotalPrice()

        _totalQuantity.postValue(getCartQuantity())
    }


    private fun setCartItems() {
        _cartItems.value = getCart()
    }

    fun postCartItems() {
        _cartItems.postValue(getCart())
    }

    private fun setRefundCartItems() {
//        _cartItems.postValue(getCart())
        _refundCartItems.value = getRefundCart()
    }

    private fun setTotalPrice() {
//        _totalPrice.postValue(getCartPrice())
        _totalPrice.value = getCartPrice()
    }

    fun postTotalPrice() {
        _totalPrice.postValue(getCartPrice())
    }

    private fun setRefundTotalPrice() {
//        _totalPrice.postValue(getCartPrice())
        _refundTotalPrice.value = getRefundCartPrice()
    }

    fun addCartItem(cartItem: CartItem) {
        val cart =
            getCart()

        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem == null) {
            cart.add(cartItem)
        } else {
            targetItem.quantity++
        }

        saveCart(
            cart
        )
        setCartItems()
        setTotalPrice()
//        AddCartItemAsyncTask().execute(cartItem)
    }

    fun addRefundCartItem(cartItem: CartItem, originalQuantity: Int) {
        val cart =
            getRefundCart()

        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem == null) {
            cart.add(cartItem)
        } else {
            if (targetItem.quantity < originalQuantity)
                targetItem.quantity++
        }

        saveRefundCart(
            cart
        )

        setRefundCartItems()
        setRefundTotalPrice()
//        AddCartItemAsyncTask().execute(cartItem)
    }

    fun removeCartItem(cartItem: CartItem) {
        val cart =
            getCart()
        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
//        val cartItem = cart[cartItem]

        cart.remove(targetItem)

        saveCart(
            cart
        )

        setCartItems()
        setTotalPrice()
//        RemoveCartItemAsyncTask().execute(item)
    }

    fun removeRefundCartItem(cartItem: CartItem) {
        val cart =
            getRefundCart()
        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
//        val cartItem = cart[cartItem]

        cart.remove(targetItem)

        saveRefundCart(
            cart
        )

        setRefundCartItems()
        setRefundTotalPrice()
//        RemoveCartItemAsyncTask().execute(item)
    }


    fun increaseCartItemQuantity(cartItem: CartItem) {
        val cart =
            getCart()

        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem != null) {
            targetItem.quantity++
        }
        saveCart(
            cart
        )

        setCartItems()
        setTotalPrice()

//        IncreaseCartItemQuantityAsyncTask().execute(item)
    }

    fun increaseRefundCartItemQuantity(cartItem: CartItem) {
        val cart =
            getRefundCart()

        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem != null) {
            targetItem.quantity++
        }

        saveRefundCart(
            cart
        )

        setRefundCartItems()
        setRefundTotalPrice()

//        IncreaseCartItemQuantityAsyncTask().execute(item)
    }

    fun decreaseCartItemQuantity(cartItem: CartItem) {
        val cart =
            getCart()
        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem != null) {
            if (targetItem.quantity > 1) {
                targetItem.quantity--
            }
        }

        saveCart(
            cart
        )

        setCartItems()
        setTotalPrice()

//        DecreaseCartItemQuantityAsyncTask().execute(item)
    }

    fun decreaseRefundCartItemQuantity(cartItem: CartItem) {
        val cart =
            getRefundCart()
        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem != null) {
            if (targetItem.quantity > 1) {
                targetItem.quantity--
            }
        }

        saveRefundCart(
            cart
        )

        setRefundCartItems()
        setRefundTotalPrice()

//        DecreaseCartItemQuantityAsyncTask().execute(item)
    }

    fun removeALLCartItem() {
        deleteCart()
        setCartItems()
        setTotalPrice()
    }

    fun removeALLRefundCartItem() {
        deleteRefundCart()
        setRefundCartItems()
        setRefundTotalPrice()
    }

    private fun saveCart(cart: MutableList<CartItem>) {
        Paper.book().write(CART, cart)
    }

    private fun saveRefundCart(cart: MutableList<CartItem>) {
        Paper.book().write(REFUND_CART, cart)
    }

    fun getCart(): MutableList<CartItem> {
        return Paper.book().read(CART, mutableListOf())
    }

    private fun getRefundCart(): MutableList<CartItem> {
        return Paper.book().read(REFUND_CART, mutableListOf())
    }

    fun deleteCart() = Paper.book().delete(CART)

    fun deleteRefundCart() = Paper.book().delete(REFUND_CART)

    fun getShoppingCartSize(): Int {
        var cartSize = 0
        getCart()
            .forEach {
                cartSize += it.quantity
            }

        return cartSize
    }

    private fun getCartPrice(): Double {
        var price = 0.0
        _cartItems.value?.forEach {
            price += (it.product.price.salePrice * it.quantity)
        }
        return price
    }

    private fun getRefundCartPrice(): Double {
        var price = 0.0
        _refundCartItems.value?.forEach {
            price += (it.product.price.salePrice * it.quantity)
        }
        return price
    }

    private fun getCartQuantity(): Int {
        var quantity = 0
        _cartItems.value?.forEach {
            quantity += it.quantity
        }
        return quantity
    }


    //    companion object {
    class AddCartItemAsyncTask :
        AsyncTask<CartItem, Void, Void>() {
        override fun doInBackground(vararg params: CartItem): Void? {
            val cart =
                getCart()

            val targetItem = cart.singleOrNull { it.product.code == params[0].product.code }
            if (targetItem == null) {
                cart.add(params[0])
            }

            saveCart(
                cart
            )
            return null
        }

    }

    class RemoveCartItemAsyncTask : AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            val cart =
                getCart()
            val cartItem = cart[params[0]!!]
            cart.remove(cartItem)
            saveCart(
                cart
            )
            return null
        }
    }

    class IncreaseCartItemQuantityAsyncTask :
        AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            val cart =
                getCart()
            val cartItem = cart[params[0]!!]

            val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
            if (targetItem != null) {
                targetItem.quantity++
                Log.d(
                    "ShoppingCartRepository",
                    "increaseCartItemQuantity: ${targetItem.quantity}"
                )
            }
            saveCart(
                cart
            )
            return null
        }
    }

    class DecreaseCartItemQuantityAsyncTask :
        AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            val cart =
                getCart()
            val cartItem = cart[params[0]!!]
            val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
            if (targetItem != null) {
                if (targetItem.quantity > 1) {
                    targetItem.quantity--
                    Log.d(
                        "ShoppingCartRepository",
                        "decreaseCartItemQuantity: ${targetItem.quantity}"
                    )
                }
            }
//
            saveCart(
                cart
            )
            return null
        }
    }

    class RemoveALLCartItemAsyncTask :
        AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            deleteCart()
            return null
        }
    }


//    }


}