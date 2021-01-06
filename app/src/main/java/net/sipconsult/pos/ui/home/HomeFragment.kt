package net.sipconsult.pos.ui.home

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
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_sales_agent.*
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.android.synthetic.main.shopping_cart_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.pos.R
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.data.models.CartItem
import net.sipconsult.pos.data.models.SalesAgentsItem
import net.sipconsult.pos.ui.base.ScopedFragment
import net.sipconsult.pos.ui.login.AuthenticationState
import net.sipconsult.pos.ui.salesAgent.SalesAgentListAdapter
import net.sipconsult.pos.ui.shoppingcart.ShoppingCartAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import woyou.aidlservice.jiuiv5.IWoyouService
import java.text.DecimalFormat
import java.util.*


class HomeFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
//    private var _binding: HomeFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private val viewModelFactory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)

        val intent = Intent()
        intent.setPackage("woyou.aidlservice.jiuiv5")
        intent.action = "woyou.aidlservice.jiuiv5.IWoyouService"
        context?.bindService(intent, connService, Context.BIND_AUTO_CREATE)

        val navController = NavHostFragment.findNavController(this)

        sharedViewModel.authenticationState.observe(
            viewLifecycleOwner,
            Observer { authenticationState ->
                when (authenticationState) {
//                AuthenticationState.AUTHENTICATED -> showWelcomeMessage()
                    AuthenticationState.UNAUTHENTICATED -> navController.navigate(R.id.loginFragment)
                }
            })

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
        sharedViewModel.cartItems.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                listShoppingCart.visibility = View.INVISIBLE
                groupCart.visibility = View.VISIBLE
                buttonPayment.isEnabled = false
                buttonClearCart.isEnabled = false
            } else {
                listShoppingCart.visibility = View.VISIBLE
                groupCart.visibility = View.INVISIBLE
                buttonPayment.isEnabled = true
                buttonClearCart.isEnabled = true
            }
            shoppingCartAdapter.setCartItems(it.asReversed())
        })

//        sharedViewModel.salesAgent.observe(viewLifecycleOwner, Observer { saleAgent ->
//            buttonPayment.isEnabled = saleAgent != null
//        })

        sharedViewModel.totalCartPrice.observe(viewLifecycleOwner, Observer { totalPrice ->

            totalPrice?.let {
                val total = DecimalFormat("0.00").format(it)
                textTotal.text = String.format("Total: Ghc %s", total)
                displayTotalPrice(String.format("Total:GHC%s", total))
            }

        })

        buttonClearCart.setOnClickListener {
            viewModel.removeALLCartItem()
            textProductFoundNotFound.text = ""
        }

        buttonPayment.setOnClickListener {
            textProductFoundNotFound.text = ""
            if (sharedViewModel.salesAgent.value != null) {
                sharedViewModel.transactionType = 1
                sharedViewModel.setTotalPrice()
                val action =
                    HomeFragmentDirections.actionNavHomeToPaymentFragment(transactionType = 1)
                this.findNavController().navigate(action)
            } else {
                Toast.makeText(context, "Please Select Sales Agent", Toast.LENGTH_SHORT).show()
            }
        }

        bindUI()
    }

    private fun bindUI() = launch {
        val salesAgents = viewModel.salesAgents.await()
        if (view != null) {
            salesAgents.observe(viewLifecycleOwner, Observer { salesAgents ->
                if (salesAgents == null) return@Observer
                groupSalesAgentLoading.visibility = View.GONE
                setupSalesAgentRecyclerView(salesAgents as ArrayList<SalesAgentsItem>)
            })
        }

    }

    private fun setupSalesAgentRecyclerView(products: ArrayList<SalesAgentsItem>) {
        val salesAgentListAdapter =
            SalesAgentListAdapter(::onSalesAgentClick)
        listSalesAgent.adapter = salesAgentListAdapter
        listSalesAgent.addItemDecoration(
            DividerItemDecoration(
                listSalesAgent.context,
                DividerItemDecoration.VERTICAL
            )
        )
        salesAgentListAdapter.setSalesAgent(products)

    }

    private fun onSalesAgentClick(salesAgentsItem: SalesAgentsItem) {
        sharedViewModel.setSalesAgent(salesAgentsItem)
        Toast.makeText(
            context,
            "Sales Agent ${salesAgentsItem.displayName} Selected",
            Toast.LENGTH_SHORT
        ).show()
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

    private fun onDeleteClick(cartItem: CartItem) {
        viewModel.removeCartItem(cartItem)
    }

    private fun onSubClick(cartItem: CartItem) {
        viewModel.decreaseCartItemQuantity(cartItem)
    }

    private fun onAddClick(cartItem: CartItem) {
        viewModel.increaseCartItemQuantity(cartItem)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
