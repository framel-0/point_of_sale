package net.sipconsult.pos.ui.transactions.refund.details

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
import kotlinx.android.synthetic.main.payment_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.pos.R
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.data.models.PaymentMethodItem
import net.sipconsult.pos.ui.base.ScopedFragment
import net.sipconsult.pos.ui.payment.PaymentMethodListAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import woyou.aidlservice.jiuiv5.IWoyouService
import java.text.DecimalFormat
import java.util.*

class RefundDetailFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var viewModel: RefundDetailViewModel

    private lateinit var sharedViewModel: SharedViewModel

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
        return inflater.inflate(R.layout.refund_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RefundDetailViewModel::class.java)

        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        val intent = Intent()
        intent.setPackage("woyou.aidlservice.jiuiv5")
        intent.action = "woyou.aidlservice.jiuiv5.IWoyouService"
        context?.bindService(intent, connService, Context.BIND_AUTO_CREATE)


        bindUI()

    }

    private fun bindUI() = launch {
        val paymentMethods = sharedViewModel.getPaymentMethods.await()
        paymentMethods.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            groupLoading.visibility = View.GONE
            setupRecyclerViewPaymentMethod(it as ArrayList<PaymentMethodItem>)

        })


        sharedViewModel.totalPrice.observe(viewLifecycleOwner, Observer { total ->
            textTotalAmountCart.text = String.format("GHC%s", DecimalFormat("0.00").format(total))

            sharedViewModel.change.observe(viewLifecycleOwner, Observer { change ->
                displayTotalPriceChange(
                    String.format(
                        "Total:GHC%s",
                        DecimalFormat("0.00").format(total)
                    ), String.format(
                        "Change:%s",
                        change
                    )
                )

            })


        })

        sharedViewModel.paymentMethods.observe(viewLifecycleOwner, Observer {

            if (it.isNullOrEmpty()) {
                groupPaymentMethod.visibility = View.VISIBLE
                framePaymentMethod.visibility = View.GONE
//                Toast.makeText(context, "Please selected Payment Method", Toast.LENGTH_SHORT).show()
            } else {
                groupPaymentMethod.visibility = View.GONE
                framePaymentMethod.visibility = View.VISIBLE
            }
        })
    }

    private fun displayTotalPriceChange(totalPrice: String, change: String) {
        if (woyouService == null) {
//            Toast.makeText(context, "Service not ready", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            //woyouService.sendLCDCommand(2);
            woyouService!!.sendLCDDoubleString(totalPrice, change, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerViewPaymentMethod(paymentMethods: ArrayList<PaymentMethodItem>) {
        val paymentMethodListAdapter =
            PaymentMethodListAdapter(::onPaymentMethodClick, ::onCheckPaymentMethodClick)
        listPaymentMethod.adapter = paymentMethodListAdapter
        paymentMethodListAdapter.setPaymentMethods(paymentMethods)
    }

    private fun onPaymentMethodClick(paymentMethod: PaymentMethodItem) {
        sharedViewModel.paymentMethods.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {

                val paymentMethodItem = it.find { p -> p.id == paymentMethod.id }

                if (paymentMethodItem != null) {
                    sharedViewModel.setPaymentMethod(paymentMethodItem)
                }

            }
        })
    }

    private fun onCheckPaymentMethodClick(paymentMethods: ArrayList<PaymentMethodItem>) {
        sharedViewModel.setPaymentMethods(paymentMethods)
        val paymentMethodItemCash = paymentMethods.find { p -> p.id == 1 }
        if (paymentMethodItemCash == null) {
            sharedViewModel.cashAmount = 0.0
            sharedViewModel.deduct()
        } else {
            sharedViewModel.setPaymentMethod(paymentMethodItemCash)
        }

        val paymentMethodItemMomo = paymentMethods.find { p -> p.id == 2 }
        if (paymentMethodItemMomo == null) {
            sharedViewModel.mobileMoneyAmount = 0.0
            sharedViewModel.deduct()
        } else {
            sharedViewModel.setPaymentMethod(paymentMethodItemMomo)
        }

        val paymentMethodItemCard = paymentMethods.find { p -> p.id == 3 }
        if (paymentMethodItemCard == null) {
            sharedViewModel.cardAmount = 0.0
            sharedViewModel.deduct()
        } else {
            sharedViewModel.setPaymentMethod(paymentMethodItemCard)
        }

        val paymentMethodItemCheque = paymentMethods.find { p -> p.id == 4 }
        if (paymentMethodItemCheque == null) {
            sharedViewModel.chequeAmount = 0.0
            sharedViewModel.deduct()
        } else {
            sharedViewModel.setPaymentMethod(paymentMethodItemCheque)
        }

        val paymentMethodItemLoyalty = paymentMethods.find { p -> p.id == 5 }
        if (paymentMethodItemLoyalty == null) {
            sharedViewModel.loyaltyAmount = 0.0
            sharedViewModel.voucherId = null
            sharedViewModel.deduct()
        } else {
            sharedViewModel.setPaymentMethod(paymentMethodItemLoyalty)
        }
    }


}