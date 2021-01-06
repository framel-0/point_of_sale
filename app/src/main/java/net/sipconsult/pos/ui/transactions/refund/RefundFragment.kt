package net.sipconsult.pos.ui.transactions.refund

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.list_item_sales_transaction.*
import kotlinx.android.synthetic.main.refund_fragment.*
import kotlinx.android.synthetic.main.shopping_cart_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.pos.R
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.data.models.*
import net.sipconsult.pos.data.network.response.Category
import net.sipconsult.pos.ui.base.ScopedFragment
import net.sipconsult.pos.ui.shoppingcart.ShoppingCartAdapter
import net.sipconsult.pos.ui.transactions.SalesTransactionProductListAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import woyou.aidlservice.jiuiv5.IWoyouService
import java.text.DecimalFormat

class RefundFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var sharedViewModel: SharedViewModel

    private val viewModelFactory: RefundViewModelFactory by instance()
    private lateinit var viewModel: RefundViewModel

    private val args: RefundFragmentArgs by navArgs()

    private var woyouService: IWoyouService? = null
    private val connService: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            woyouService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            woyouService = IWoyouService.Stub.asInterface(service)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.refund_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        viewModel = ViewModelProvider(this, viewModelFactory).get(RefundViewModel::class.java)

        val intent = Intent()
        intent.setPackage("woyou.aidlservice.jiuiv5")
        intent.action = "woyou.aidlservice.jiuiv5.IWoyouService"
        context?.bindService(intent, connService, Context.BIND_AUTO_CREATE)

        bindUI()
    }

    private fun bindUI() = launch {

        viewModel.transactionId = args.transactionId

        ldIn()

        viewModel.transactionResult.observe(
            viewLifecycleOwner, Observer { result ->
                result ?: return@Observer
//                groupSaleTransactionLoading.visibility = View.GONE

                result.error?.let {
//                    showVoucherFailed(it)
                }
                result.success?.let { transaction ->
                    updateUiWithTransaction(transaction)
                    //        val products = viewModel.productItems
                }
            }
        )
        val shoppingCartAdapter =
            ShoppingCartAdapter(
                ::onSubClick,
                ::onAddClick,
                ::onDeleteClick
            )
        listShoppingCart.adapter = shoppingCartAdapter
        listShoppingCart.addItemDecoration(
            DividerItemDecoration(
                listShoppingCart.context,
                DividerItemDecoration.VERTICAL
            )
        )
        sharedViewModel.refundCartItems.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                listShoppingCart.visibility = View.INVISIBLE
                groupCart.visibility = View.VISIBLE
                buttonRefundProceed.isEnabled = false
                buttonRefundClearProducts.isEnabled = false
            } else {
                listShoppingCart.visibility = View.VISIBLE
                groupCart.visibility = View.INVISIBLE
                buttonRefundProceed.isEnabled = true
                buttonRefundClearProducts.isEnabled = true
            }
            shoppingCartAdapter.setCartItems(it.asReversed())
        })


        viewModel.totalCartPrice.observe(viewLifecycleOwner, Observer { totalPrice ->

            totalPrice?.let {
                val total = DecimalFormat("0.00").format(it)
                textTotal.text = String.format("Total: Ghc %s", total)
                displayTotalPrice(String.format("Total:GHC%s", total))
            }

        })

        buttonRefundClearProducts.setOnClickListener {
            viewModel.removeALLCartItem()
        }

        buttonRefundProceed.setOnClickListener {
            sharedViewModel.transactionType = 2
            sharedViewModel.salesTransactionId = viewModel.transactionId
            sharedViewModel.setTotalPrice()
            val action =
                RefundFragmentDirections.actionRefundFragmentToPaymentFragment(transactionType = 2)
            findNavController().navigate(action)

        }

//        setupRecyclerView(products as ArrayList<ProductItem>)
    }


    private fun ldIn() = launch {
        val result = viewModel.getSaleTransaction.await()
        viewModel.updateTransactionResult(result)
    }

    private fun updateUiWithTransaction(transaction: SalesTransactionsItem) {
        textSaleTransactionDate.text = transaction.date
        textSaleTransactionReceiptNumber.text = transaction.receiptNumber
        textSaleTransactionLocation.text = transaction.location?.name ?: ""
        textSaleTransactionTotalSales.text = transaction.totalSalesStr
        setupRecyclerView(transaction.salesTransactionProduct as ArrayList<SalesTransactionProduct>)
    }


    private fun setupRecyclerView(products: ArrayList<SalesTransactionProduct>) {
        val productRecyclerAdapter =
            SalesTransactionProductListAdapter(::onProductClick)
        listRefundProduct.adapter = productRecyclerAdapter
        productRecyclerAdapter.setProducts(products)
//        setupSearchView(productRecyclerAdapter)
    }

    fun displayName(name: String) {
        if (woyouService == null) {
//            Toast.makeText(context, "Service not ready", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            woyouService!!.sendLCDString(name, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun displayTotalPrice(totalPrice: String) {
        if (woyouService == null) {
//            Toast.makeText(context, "Service not ready", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            //woyouService.sendLCDCommand(2);
            woyouService!!.sendLCDDoubleString(totalPrice, "", null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun onProductClick(transactionProduct: SalesTransactionProduct) {
        val pr = Price(salePrice = transactionProduct.product.salePrice, costPrice = 0.0)
        val ct = Category(code = "", description = null)
        val pdt = ProductItem(
            barcode = transactionProduct.product.barcode,
            description = transactionProduct.product.description,
            code = transactionProduct.product.code,
            price = pr,
            quantity = transactionProduct.product.quantity,
            imageUrl = ""
        )
        viewModel.addCartItem(pdt, transactionProduct.quantity)
    }

    private fun onDeleteClick(cartItem: CartItem) {
        viewModel.removeCartItem(cartItem)
    }

    private fun onSubClick(cartItem: CartItem) {
        viewModel.decreaseCartItemQuantity(cartItem)
    }

    private fun onAddClick(cartItem: CartItem) {
        viewModel.increaseCartItemQuantity(cartItem)
    }
}