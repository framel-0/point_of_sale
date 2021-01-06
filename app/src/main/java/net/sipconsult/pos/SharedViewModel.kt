package net.sipconsult.pos

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.pos.data.models.*
import net.sipconsult.pos.data.network.response.TransactionResponse
import net.sipconsult.pos.data.provider.LocationProvider
import net.sipconsult.pos.data.provider.PosNumberProvider
import net.sipconsult.pos.data.repository.discountType.DiscountTypeRepository
import net.sipconsult.pos.data.repository.location.LocationRepository
import net.sipconsult.pos.data.repository.paymentMethod.PaymentMethodRepository
import net.sipconsult.pos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.pos.data.repository.transaction.TransactionRepository
import net.sipconsult.pos.data.repository.user.UserRepository
import net.sipconsult.pos.internal.Event
import net.sipconsult.pos.internal.Result
import net.sipconsult.pos.internal.lazyDeferred
import net.sipconsult.pos.ui.login.AuthenticationState
import net.sipconsult.pos.ui.payment.TransactionResult
import net.sipconsult.pos.util.Receipt
import net.sipconsult.pos.util.ReceiptBuilder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class SharedViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val discountTypeRepository: DiscountTypeRepository,
    private val locationProvider: LocationProvider,
    private val posNumberProvider: PosNumberProvider,
    private val locationRepository: LocationRepository,
    val context: Context
) : ViewModel() {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("AndroidHiveLogin", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPref.edit()

    //    lateinit var user: LoggedInUser
    private val locationId = locationProvider.getLocation()

    var transactionType: Int = 1
    var salesTransactionId: Int = 0
    private var dateNow = Date()
    private var receiptNumber: String = ""
    private var total: Double = 0.0
    lateinit var receipt: Receipt
    private val decimalFormater = DecimalFormat("0.00")
    private var username: String = ""
    var email: String = ""


    private var isReceiptNumberGenerated: Boolean = false
    var isPrintReceipt: Boolean = false
    var isDeliveryCostAdd: Boolean = false
    var isDeliveryCostSub: Boolean = false

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState

    private val _user = MutableLiveData<LoggedInUser>()
    val user: LiveData<LoggedInUser> = _user

    var cartItems: LiveData<MutableList<CartItem>> = ShoppingCartRepository.cartItems
    var refundCartItems: LiveData<MutableList<CartItem>> = ShoppingCartRepository.refundCartItems
    val location: LocationsItem = locationRepository.getLocation(locationId)

    val totalCartPrice: LiveData<Double> = ShoppingCartRepository.totalPrice

    private var _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    private var _totalDiscountPrice = MutableLiveData<Double>()
    var totalDiscountPrice = _totalDiscountPrice


    private val _paymentMethods = MutableLiveData<ArrayList<PaymentMethodItem>>()
    val paymentMethods: LiveData<ArrayList<PaymentMethodItem>> = _paymentMethods

    private val _selectedPaymentMethod = MutableLiveData<PaymentMethodItem>()
    val selectedPaymentMethod: LiveData<PaymentMethodItem> = _selectedPaymentMethod

    val editTextDeliveryCost = MutableLiveData<String>()
    var deliveryCost: Double = 0.0
    private val _discountType = MutableLiveData<DiscountTypesItem>()
    val discountType: LiveData<DiscountTypesItem> = _discountType

    private val _salesAgent = MutableLiveData<SalesAgentsItem>()
    val salesAgent: LiveData<SalesAgentsItem> = _salesAgent

    private val _transactionResult = MutableLiveData<Event<TransactionResult>>()
    val transactionResult: LiveData<Event<TransactionResult>> = _transactionResult

    val editTextTender = MutableLiveData<String>()
    var cashAmount: Double = 0.0
    var cash: Double = 0.0

    val editTextCashDollarAmount = MutableLiveData<String>()
    var cashDollarAmount: Double = 0.0
    var cashDollar: Double = 0.0

    val editTextMobileMoneyPhoneNumber = MutableLiveData<String>()
    var mobileMoneyPhoneNumber: String = ""
    var editTextMobileMoneyAmount = MutableLiveData<String>()
    var mobileMoneyAmount: Double = 0.0
    var mobileMoney: Double = 0.0

    val editTextCardNumber = MutableLiveData<String>()
    var cardNumber: String = ""
    val editTextCardAmount = MutableLiveData<String>()
    var cardAmount: Double = 0.0
    var card: Double = 0.0

    val editTextChequeNumber = MutableLiveData<String>()
    var chequeNumber: String = ""
    val editTextChequeAmount = MutableLiveData<String>()
    var chequeAmount: Double = 0.0
    var cheque: Double = 0.0

    val editTextComplimentaryNumber = MutableLiveData<String>()
    var complimentaryNumber: String = ""
    val editTextComplimentaryAmount = MutableLiveData<String>()
    var complimentaryAmount: Double = 0.0
    var complimentary: Double = 0.0

    var scanSuccessful: Boolean = false
    var voucherId: Int? = null
    val editTextLoyaltyAmount = MutableLiveData<String>()
    var loyaltyAmount: Double = 0.0
    var loyalty: Double = 0.0

    var totalAmount: Double = 0.0

    private val _change = MutableLiveData<String>()
    val change: LiveData<String> = _change

    init {

        if (userRepository.isLoggedIn())
        // In this example, the user is always unauthenticated when MainActivity is launched
            _authenticationState.value = AuthenticationState.AUTHENTICATED
        else
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED

        if (userRepository.isLoggedIn()) {
            userRepository.loggedInUser.observeForever {
                _user.value = it
                username = it.displayName
                email = it.email
            }
        }

        _totalPrice.postValue(ShoppingCartRepository.totalCartPrice)
    }

    fun setTotalPrice() {
        if (transactionType == 1) {
            _totalPrice.postValue(ShoppingCartRepository.totalCartPrice)
        } else if (transactionType == 2) {
            _totalPrice.postValue(ShoppingCartRepository.refundTotalCartPrice)
        }
    }

//    fun setRefundTotalPrice() {
//        _refundTotalPrice.postValue(ShoppingCartRepository.refundTotalCartPrice)
//    }

    fun logout() {
        userRepository.logout()
        _authenticationState.postValue(AuthenticationState.UNAUTHENTICATED)
    }

    fun authenticate() {
        _authenticationState.value = AuthenticationState.AUTHENTICATED
    }


    fun deduct() {
        val totalAmount =
            cashAmount + mobileMoneyAmount + cardAmount + chequeAmount + loyaltyAmount + complimentaryAmount + cashDollarAmount
        val change: Double = totalAmount - totalPrice.value!!
        _change.value = decimalFormater.format(change)
    }

    fun addDeliveryCost() {
        val newTotalPrice: Double = totalPrice.value!! + deliveryCost
        _totalPrice.value = (decimalFormater.format(newTotalPrice).toDouble())
    }

    fun subDeliveryCost() {
        val newTotalPrice: Double = totalPrice.value!! - deliveryCost
        _totalPrice.value = (decimalFormater.format(newTotalPrice).toDouble())
    }

    fun setPaymentMethods(paymentMethods: ArrayList<PaymentMethodItem>) {
        _paymentMethods.value = paymentMethods
    }

    fun setPaymentMethod(paymentMethod: PaymentMethodItem) {
        _selectedPaymentMethod.value = paymentMethod
    }

    fun setDiscountType(discountType: DiscountTypesItem) {
        _discountType.value = discountType
    }

    fun setSalesAgent(salesAgent: SalesAgentsItem) {
        _salesAgent.value = salesAgent
    }

    val getPaymentMethods by lazyDeferred {
        paymentMethodRepository.getPaymentMethods()
    }

    val discountTypes by lazyDeferred {
        discountTypeRepository.getDiscountTypes()
    }

    private fun compareToDay(date1: Date?, date2: Date?): Int {
        if (date1 == null || date2 == null) {
            return 0
        }
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(date1).compareTo(sdf.format(date2))
    }

    private val generateReceiptNumber: String
        get() {

//            var receiptNumber = ""
            val dateNow = Date(System.currentTimeMillis())

            val millis = dateNow.time
            val posNumber = posNumberProvider.getPOSNumber()

            val currentDate: Long = sharedPref.getLong(KEY_CURRENT_DATE, 0L)
            var currentIndex: Long = sharedPref.getLong(KEY_CURRENT_INDEX, -1L)
            if (currentDate == 0L)
                editor.putLong(KEY_CURRENT_DATE, millis).apply()

            val myCurrentDate = Date(sharedPref.getLong(KEY_CURRENT_DATE, 0))

            val formatReceiptNumber = SimpleDateFormat("yyMMdd", Locale.getDefault())
            val dateToStr = formatReceiptNumber.format(dateNow)

            val userInitial: String = user.value!!.abbrv

            if (compareToDay(dateNow, myCurrentDate) == 0) {
                if (currentIndex == -1L) {
                    editor.putLong(KEY_CURRENT_INDEX, 1).apply()
                }
                currentIndex = sharedPref.getLong(KEY_CURRENT_INDEX, 0L)

                receiptNumber =
                    String.format("%s%s%o%s", userInitial, dateToStr, currentIndex, posNumber)

                val newIndex = currentIndex + 1
                editor.putLong(KEY_CURRENT_INDEX, newIndex).apply()

            } else {

                editor.putLong(KEY_CURRENT_DATE, millis).apply()
                if (currentIndex == -1L) {
                    editor.putLong(KEY_CURRENT_INDEX, 1).apply()
                }

                editor.putLong(KEY_CURRENT_INDEX, 1).apply()
                currentIndex = sharedPref.getLong(KEY_CURRENT_INDEX, 0L)

                receiptNumber =
                    String.format("%s%s%o%s", userInitial, dateToStr, currentIndex, posNumber)

                val newIndex = currentIndex + 1
                editor.putLong(KEY_CURRENT_INDEX, newIndex).apply()
            }

            return if (transactionType == 1) {
                receiptNumber
            } else {
                receiptNumber + "R"
            }
        }

    fun generateReceiptNumber() {
        if (!isReceiptNumberGenerated) {
            receiptNumber = generateReceiptNumber
            isReceiptNumberGenerated = true
        }

    }

    fun updateTotalPrice(newTotalPrice: Double) {
        _totalPrice.value = (
                decimalFormater.format(newTotalPrice).toDouble()
                )
    }

    fun updateDiscountPrice(discountPrice: Double) {
        _totalDiscountPrice.value = (
                decimalFormater.format(discountPrice).toDouble()
                )

    }

    private fun calVAT(): String {
        val vatDiscount = 3.toDouble() / 103
        val vTotal: Double = total - deliveryCost
        val vatAmount = vatDiscount.times(vTotal)
//        val newTotalPrice: Double = total - vatAmount
        return decimalFormater.format(vatAmount)

    }

    private fun calSubTotal(): String {
        val vatDiscount = 100.toDouble() / 103
        val vatAmount: Double = vatDiscount.times(total)
        val sTotal: Double = total - deliveryCost
        val subTotal: Double = (vatDiscount.times(sTotal))
        return decimalFormater.format(subTotal)

    }

    private fun paymentMethodsStr(): String {
        val paymentMethods = paymentMethods.value!!

        val sb = StringBuilder()

//        for (i in 0 until paymentMethods.size) {
        for (str in paymentMethods) {
            val prefix = ", "
            sb.append(prefix)
            sb.append(str.name.trim())
        }
//        }

        return sb.toString()

    }

    private fun receiptPaymentMethod(): ArrayList<PaymentMethodItem> {
        val paymentMethodItems = arrayListOf<PaymentMethodItem>()
        for (pm in paymentMethods.value!!) {
            lateinit var paymentMethodItem: PaymentMethodItem
            when (pm.id) {
                1 -> {
                    pm.amountPaid = cashAmount
                    pm.displayName = "Cash Amount Paid"
                    paymentMethodItem = pm
                }
                2 -> {
                    pm.amountPaid = mobileMoneyAmount
                    pm.displayName = "Mobile Money Amount Paid"
                    paymentMethodItem = pm
                }
                3 -> {
                    pm.amountPaid = cardAmount
                    pm.displayName = "Card Amount Paid"
                    paymentMethodItem = pm
                }
                4 -> {
                    pm.amountPaid = chequeAmount
                    pm.displayName = "Cheque Amount Paid"
                    paymentMethodItem = pm
                }
                5 -> {
                    pm.amountPaid = loyaltyAmount
                    pm.displayName = "Gift Voucher Amount Paid"
                    paymentMethodItem = pm
                }
                6 -> {
                    pm.amountPaid = complimentaryAmount
                    pm.displayName = "Complimentary Amount Paid"
                    paymentMethodItem = pm
                }
                7 -> {
                    pm.amountPaid = cashDollarAmount
                    pm.displayName = "Cash Dollar Amount Paid"
                    paymentMethodItem = pm
                }
            }
            paymentMethodItems.add(paymentMethodItem)
        }

        return paymentMethodItems
    }

    fun buildSalesTransactionReceipt() {
        val items = cartItems.value!!
        total = totalPrice.value!!.toDouble()
        val dateStr = DateFormat.format("dd/MM/yyyy", dateNow).toString()
        val timeStr = DateFormat.format("HH:mm:ss", dateNow).toString()
        val locationR = locationRepository.getLocation(locationId)
        val userR = user.value!!
        val salesAgentR = salesAgent.value!!
        val deliveryCostR = deliveryCost
        val subTotal = calSubTotal()
        val vat = calVAT()
        val paymentMethodsR = receiptPaymentMethod()
        val paymentMethodsSR = paymentMethodsStr()
        val totalString = decimalFormater.format((total))

        receipt =
            if (discountType.value != null) {
                ReceiptBuilder()
                    .header("JUBEN HOUSE OF BEAUTY")
                    .text(locationR.address)
                    .text("Tel: ${locationR.telephone}")
                    .text("Mobile: ${locationR.mobileNumber1}")
                    .text("WhatsApp: ${locationR.mobileNumber2}")
                    .text("Tin: C0005355370")
                    .subHeader("SALES RECEIPT")
                    .divider()
                    .text("Date: $dateStr")
                    .text("Time: $timeStr")
                    .text("Receipt No: $receiptNumber")
                    .text("Operator: ${userR.displayName}")
                    .text("Sales Agent: ${salesAgentR.displayName}")
                    .text("Shop: ${locationR.name}")
                    .divider()
                    .subHeader("Items")
                    .menuItems(items)
                    .dividerDouble()
                    .menuLine("SubTotal", "GHC $subTotal")
                    .menuLine("3% VAT Rate", "GHC $vat")
                    .menuLine(
                        "Discount ${discountType.value!!.percentageStr}",
                        "GHC ${totalDiscountPrice.value!!}"
                    )
                    .menuLine("Delivery Charge", "GHC $deliveryCostR")
                    .menuLine("Total", "GHC $totalString")
                    .menuPaymentMethod(paymentMethodsR)
                    .menuLine("Change", "GHC ${change.value}")
                    .menuLine("Payment Method", paymentMethodsSR)
                    .dividerDouble()
                    .stared("THANK YOU")
                    .build()
            } else {
                ReceiptBuilder()
                    .header("JUBEN HOUSE OF BEAUTY")
                    .text(locationR.address)
                    .text("Tel: ${locationR.telephone}")
                    .text("Mobile: ${locationR.mobileNumber1}")
                    .text("WhatsApp: ${locationR.mobileNumber2}")
                    .text("Tin: C0005355370")
                    .subHeader("SALES RECEIPT")
                    .divider()
                    .text("Date: $dateStr")
                    .text("Time: $timeStr")
                    .text("Receipt No: $receiptNumber")
                    .text("Operator: ${userR.displayName}")
                    .text("Sales Agent: ${salesAgentR.displayName}")
                    .text("Shop: ${locationR.name}")
                    .divider()
                    .subHeader("Items")
                    .menuItems(items)
                    .dividerDouble()
                    .menuLine("SubTotal", "GHC $subTotal")
                    .menuLine("3% VAT Rate", "GHC $vat")
                    .menuLine("Delivery Charge", "GHC $deliveryCostR")
                    .menuLine("Total", "GHC $totalString")
                    .menuPaymentMethod(paymentMethodsR)
                    .menuLine("Change", "GHC ${change.value}")
                    .menuLine("Payment Method", paymentMethodsSR)
                    .dividerDouble()
                    .stared("THANK YOU")
                    .build()
            }
    }

    fun buildRefundTransactionReceipt() {
        val items = refundCartItems.value!!
        total = totalPrice.value!!.toDouble()
        val dateStr = DateFormat.format("dd/MM/yyyy", dateNow).toString()
        val timeStr = DateFormat.format("HH:mm:ss", dateNow).toString()
        val totalString = decimalFormater.format(total)
        val locationR = locationRepository.getLocation(locationId)
        val userR = user.value!!
        val subTotal = calSubTotal()
        val vat = calVAT()
        val paymentMethodsR = receiptPaymentMethod()
        val paymentMethodsSR = paymentMethodsStr()



        receipt = ReceiptBuilder()
            .header("JUBEN HOUSE OF BEAUTY")
            .text(locationR.address)
            .text("Tel: ${locationR.telephone}")
            .text("Mobile: ${locationR.mobileNumber1}")
            .text("WhatsApp: ${locationR.mobileNumber2}")
            .text("Tin: C0005355370")
            .subHeader("REFUND RECEIPT")
            .divider()
            .text("Date: $dateStr")
            .text("Time: $timeStr")
            .text("Receipt No: $receiptNumber")
            .text("Operator: ${userR.displayName}")
            .text("Shop: ${locationR.name}")
            .divider()
            .subHeader("Items")
            .menuItems(items)
            .dividerDouble()
            .menuLine("SubTotal", "GHC $subTotal")
            .menuLine("3% VAT Rate", "GHC $vat")
            .menuLine("Total", "GHC $totalString")
            .menuPaymentMethod(paymentMethodsR)
            .menuLine("Payment Method", paymentMethodsSR)
            .dividerDouble()
            .stared("THANK YOU")
            .build()

    }


    val postTransaction by lazyDeferred {

        val locationP = locationRepository.getLocation(locationId)
        val products = arrayListOf<SalesTransactionPostProduct>()
        for (item in cartItems.value!!) {
            val product = SalesTransactionPostProduct(
                productCode = item.product.code,
                quantity = item.quantity
            )
            products.add(product)
        }

        val paymentMethods = arrayListOf<SalesTransactionPostPaymentMethod>()

        if (deliveryCost >= 1) {

            val paymentMethodItems = arrayListOf<PaymentMethodItem>()
            for (pm in _paymentMethods.value!!) {
                lateinit var paymentMethodItem: PaymentMethodItem
                when (pm.id) {
                    1 -> {
                        pm.amountPaid = cashAmount
                        paymentMethodItem = pm
                    }
                    2 -> {
                        pm.amountPaid = mobileMoneyAmount
                        paymentMethodItem = pm
                    }
                    3 -> {
                        pm.amountPaid = cardAmount
                        paymentMethodItem = pm
                    }
                    4 -> {
                        pm.amountPaid = chequeAmount
                        paymentMethodItem = pm
                    }
                    5 -> {
                        pm.amountPaid = loyaltyAmount
                        paymentMethodItem = pm
                    }
                    6 -> {
                        pm.amountPaid = complimentaryAmount
                        paymentMethodItem = pm
                    }
                    7 -> {
                        pm.amountPaid = cashDollarAmount
                        paymentMethodItem = pm
                    }
                }
                paymentMethodItems.add(paymentMethodItem)
            }

            var largestPaymentMethod = paymentMethodItems[0]
            for (payMthd in paymentMethodItems) {
                if (largestPaymentMethod.amountPaid < payMthd.amountPaid) {
                    largestPaymentMethod = payMthd
                }
            }

//            lateinit var paymentMethodItem: PaymentMethodItem
            when (largestPaymentMethod.id) {
                1 -> {
                    cashAmount -= deliveryCost
                }
                2 -> {
                    mobileMoneyAmount -= deliveryCost
                }
                3 -> {
                    cardAmount -= deliveryCost
                }
                4 -> {
                    chequeAmount -= deliveryCost
                }
                5 -> {
                    loyaltyAmount -= deliveryCost
                }
                6 -> {
                    complimentaryAmount -= deliveryCost
                }
                7 -> {
                    cashDollarAmount -= deliveryCost
                }
            }

        }

        for (paymentMethod in _paymentMethods.value!!) {
            lateinit var salesTransactionPostPaymentMethod: SalesTransactionPostPaymentMethod
            when (paymentMethod.id) {
                1 -> {
                    val availableChange = change.value!!.toDouble()
                    salesTransactionPostPaymentMethod = if (availableChange > 0) {
                        SalesTransactionPostPaymentMethod(
                            paymentMethodId = paymentMethod.id,
                            amount = (cashAmount - availableChange)
                        )
                    } else {
                        SalesTransactionPostPaymentMethod(
                            paymentMethodId = paymentMethod.id,
                            amount = cashAmount
                        )
                    }

                }
                2 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = mobileMoneyAmount
                    )
                }
                3 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = cardAmount
                    )
                }
                4 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = chequeAmount
                    )
                }
                5 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = loyaltyAmount
                    )
                }
                6 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = complimentaryAmount
                    )
                }
                7 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = cashDollarAmount
                    )
                }
            }
            paymentMethods.add(salesTransactionPostPaymentMethod)
        }
        cash = cashAmount
        mobileMoney = mobileMoneyAmount
        card = cardAmount
        cheque = chequeAmount

        val body = SaleTransactionPostBody(
            locationCode = locationP.code,
            operatorUserId = user.value!!.id,
            receiptNumber = receiptNumber,
            salesAgentUserId = salesAgent.value!!.id,
            salesTransactionProduct = products,
            salesTransactionPaymentMethod = paymentMethods
        )

        if (discountType.value != null) {
            body.discountTypeId = discountType.value?.id
        }

        if (voucherId != null) {
            body.voucherId = voucherId
        }

        if (editTextDeliveryCost.value != null && deliveryCost > 0.0) {
            body.deliveryCost = deliveryCost
        }

        transactionRepository.postTransaction(body)
    }

    val postRefundTransaction by lazyDeferred {

        val locationP = locationRepository.getLocation(locationId)
        val products = arrayListOf<RefundTransactionPostProduct>()
        for (item in refundCartItems.value!!) {
            val product = RefundTransactionPostProduct(
                productCode = item.product.code,
                quantity = item.quantity
            )
            products.add(product)
        }

        val paymentMethods = arrayListOf<RefundTransactionPostPaymentMethod>()

/*
        if (deliveryCost >= 1) {

            for (paymentMethod in _paymentMethods.value!!) {
                when (paymentMethod.id) {
                    1 -> {
                        cashAmount -= deliveryCost
                    }
                    2 -> {
                        mobileMoneyAmount -= deliveryCost
                    }
                    3 -> {
                        cardAmount -= deliveryCost
                    }
                    4 -> {
                        chequeAmount -= deliveryCost
                    }
                }
            }
        }
*/

        for (paymentMethod in _paymentMethods.value!!) {
            lateinit var salesTransactionPostPaymentMethod: RefundTransactionPostPaymentMethod
            when (paymentMethod.id) {
                1 -> {
                    val availableChange = change.value!!.toDouble()
                    salesTransactionPostPaymentMethod = if (availableChange > 0) {
                        RefundTransactionPostPaymentMethod(
                            paymentMethodId = paymentMethod.id,
                            amount = (cashAmount - availableChange)
                        )
                    } else {
                        RefundTransactionPostPaymentMethod(
                            paymentMethodId = paymentMethod.id,
                            amount = cashAmount
                        )
                    }

                }
                2 -> {
                    salesTransactionPostPaymentMethod = RefundTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = mobileMoneyAmount
                    )
                }
                3 -> {
                    salesTransactionPostPaymentMethod = RefundTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = cardAmount
                    )
                }
                4 -> {
                    salesTransactionPostPaymentMethod = RefundTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = chequeAmount
                    )
                }
                5 -> {
                    salesTransactionPostPaymentMethod = RefundTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = loyaltyAmount
                    )
                }
            }
            paymentMethods.add(salesTransactionPostPaymentMethod)
        }
        cash = cashAmount
        mobileMoney = mobileMoneyAmount
        card = cardAmount
        cheque = chequeAmount

        val body = RefundTransactionPostBody(
            salesTransactionId = salesTransactionId,
            locationCode = locationP.code,
            operatorUserId = user.value!!.id,
            receiptNumber = receiptNumber,
            refundTransactionProduct = products,
            refundTransactionPaymentMethod = paymentMethods
        )

        transactionRepository.postRefundTransaction(body)
    }

    fun updateTransactionResult(result: Result<TransactionResponse>) {

        _transactionResult.value = if (result is Result.Success) {
            Event(TransactionResult(success = result.data.successful))
        } else {
            Event(TransactionResult(error = R.string.transaction_failed))
        }

    }
/*

    fun saveSaleTransaction(
//        salesTransaction: SalesTransactionItem,
//        products: List<Product>,
//        client: Client,
//        paymentMethod: PaymentMethod,
//        location: Location,
//        operator: Operator
    ) {
        val salesTransactionItem = SalesTransactionItem(
            date = dateNow,
            receiptNumber = receiptNumber,
            totalPrice = total,
            totalQuantity = totalQuantity.value!!,
            status = 1

        )
        val products = arrayListOf<Product>()
        for (item in cartItems.value!!) {
            val product = Product(
                categoryId = item.product.categoryId,
                description = item.product.description,
                imageUrl = item.product.imageUrl,
                name = item.product.name,
                salePrice = item.product.salePrice,
                costPrice = item.product.costPrice,
                barcode = item.product.barcode,
                quantity = item.quantity
            )
            products.add(product)
        }
        val paymentMethod = PaymentMethod(
            code = paymentMethod.value!!.code,
            name = paymentMethod.value!!.name
        )
        val client = Client(
            code = "",
            firstName = "",
            lastName = "",
            phoneNumber1 = "",
            phoneNumber2 = "",
            email = "",
            description = "",
            address = ""

        )
        val location = Location(
            code = "",
            name = ""

        )
        val operator = Operator(
            userId = user.id,
            firstName = user.firstName,
            lastName = user.lastName

        )
        transactionRepository.saveSalesTransaction(
            salesTransactionItem,
            products,
            client,
            paymentMethod,
            location,
            operator
        )
    }
*/

    fun resetAll() {
        ShoppingCartRepository.removeALLCartItem()
        ShoppingCartRepository.removeALLRefundCartItem()
        _paymentMethods.postValue(null)
        _discountType.postValue(null)
        _salesAgent.postValue(null)
        _transactionResult.postValue(null)
        editTextTender.postValue(null)
        cashAmount = 0.0
        mobileMoneyAmount = 0.0
        cardAmount = 0.0
        totalAmount = 0.0
        isReceiptNumberGenerated = false
        isPrintReceipt = false
    }

    fun resetPaymentMethodAndDiscount() {
        _paymentMethods.postValue(null)
        _discountType.postValue(null)
        _salesAgent.postValue(null)
    }

    fun resetTransaction() {
        _transactionResult.postValue(null)
    }

    fun updatePaymentMethods(paymentMethods: ArrayList<PaymentMethodItem>) {
        _paymentMethods.value = paymentMethods
    }

    companion object {
        private const val TAG: String = "SharedViewModel"
        private const val PREF_NAME = "AndroidHiveLogin"

        private const val KEY_CURRENT_DATE = "current_date"
        private const val KEY_CURRENT_INDEX = "current_index"
    }

}