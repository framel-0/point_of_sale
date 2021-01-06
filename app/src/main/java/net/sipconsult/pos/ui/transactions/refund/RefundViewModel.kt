package net.sipconsult.pos.ui.transactions.refund

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.CartItem
import net.sipconsult.pos.data.models.ProductItem
import net.sipconsult.pos.data.models.SalesTransactionsItem
import net.sipconsult.pos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.pos.data.repository.transaction.TransactionRepository
import net.sipconsult.pos.internal.Result
import net.sipconsult.pos.internal.lazyDeferred

class RefundViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    var transactionId: Int = 0
    var productItems: List<ProductItem> = arrayListOf()

    private val _transactionResult = MutableLiveData<SaleTransactionResult>()
    val transactionResult: LiveData<SaleTransactionResult> = _transactionResult

    val cartItems: LiveData<MutableList<CartItem>> = ShoppingCartRepository.refundCartItems

    val totalCartPrice: LiveData<Double> = ShoppingCartRepository.refundTotalPrice


    val getSaleTransaction by lazyDeferred {
        transactionRepository.fetchSaleTransaction(transactionId)
    }

    fun updateTransactionResult(result: Result<SalesTransactionsItem>) {
        if (result is Result.Success) {
            _transactionResult.value =
                SaleTransactionResult(
                    success = result.data
                )
        } else {
            _transactionResult.value =
                SaleTransactionResult(error = R.string.voucher_failed)
        }
    }

    fun addCartItem(product: ProductItem, originalQuantity: Int) {
        val cartItem = CartItem(product)
        cartItem.let { ShoppingCartRepository.addRefundCartItem(it, originalQuantity) }
    }

    fun removeCartItem(cartItem: CartItem) {
        ShoppingCartRepository.removeRefundCartItem(cartItem)
    }

    fun increaseCartItemQuantity(cartItem: CartItem) {
        ShoppingCartRepository.increaseRefundCartItemQuantity(cartItem)
    }

    fun decreaseCartItemQuantity(cartItem: CartItem) {
        ShoppingCartRepository.decreaseRefundCartItemQuantity(cartItem)
    }

    fun removeALLCartItem() {
        ShoppingCartRepository.removeALLRefundCartItem()
    }
}