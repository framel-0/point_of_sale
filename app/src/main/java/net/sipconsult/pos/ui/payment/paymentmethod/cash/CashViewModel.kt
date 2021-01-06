package net.sipconsult.pos.ui.payment.paymentmethod.cash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.pos.data.models.CartItem
import net.sipconsult.pos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.pos.util.Receipt
import java.text.DecimalFormat
import java.util.*

class CashViewModel : ViewModel() {

    var amount: Double = 0.0
    private var dateNow = Date()
    lateinit var receipt: Receipt

    private val cartItems: LiveData<MutableList<CartItem>> = ShoppingCartRepository.cartItems
    val totalPrice: LiveData<Double> = ShoppingCartRepository.totalPrice

    val editTextTender = MutableLiveData<String>()

    private val _change = MutableLiveData<String>()
    val change: LiveData<String> = _change

    fun deduct(tender: Double) {
        amount = tender
        val change: Double = tender - totalPrice.value!!
        val formater = DecimalFormat("#.##")
        _change.value = formater.format(change)
    }

    private val items = cartItems.value
    private val total = totalPrice.value


}