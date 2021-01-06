package net.sipconsult.pos.ui.receipt

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.receipt_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.pos.MainActivity
import net.sipconsult.pos.R
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.ui.base.ScopedFragment
import net.sipconsult.pos.ui.login.AuthenticationState
import net.sipconsult.pos.util.BluetoothUtil
import net.sipconsult.pos.util.SunmiPrintHelper
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein


class ReceiptFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receipt_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        val navController = NavHostFragment.findNavController(this)

        sharedViewModel.authenticationState.observe(
            viewLifecycleOwner,
            Observer { authenticationState ->
                when (authenticationState) {
//                AuthenticationState.AUTHENTICATED -> showWelcomeMessage()
                    AuthenticationState.UNAUTHENTICATED -> navController.navigate(R.id.loginFragment)
                }
            })

        buildUI()

    }

    private fun buildUI() = launch {

        buttonReceiptBack.visibility = View.GONE

        textReceiptInfo.text = sharedViewModel.receipt.receiptPreview

        buttonPrintReceipt.setOnClickListener {
            sharedViewModel.isPrintReceipt = true
            if (!BluetoothUtil.isBlueToothPrinter) {
                SunmiPrintHelper.instance.setAlign(1)
                val drawable = context?.resources?.getDrawable(R.drawable.juben_logo)
                val bitmap = (drawable as BitmapDrawable).bitmap
                SunmiPrintHelper.instance.printBitmap(
                    Bitmap.createScaledBitmap(
                        bitmap,
                        200,
                        200,
                        false
                    ), 1
                )
                SunmiPrintHelper.instance.printText(
                    sharedViewModel.receipt.receiptPreview,
                    24.0F,
                    isBold = true,
                    isUnderLine = false
                )
                SunmiPrintHelper.instance.cutpaper()
            }
        }

        buttonNextOrder.setOnClickListener {
            if (sharedViewModel.isPrintReceipt) {
                sharedViewModel.resetAll()
//                findNavController().navigate(R.id.nav_home)
//                (activity as MainActivity).recreate()
//                activity?.viewModelStore?.clear()
                activity?.finish()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(context, "Please Print Receipt", Toast.LENGTH_SHORT)
                    .show()
            }


        }

        buttonReceiptBack.setOnClickListener {
//            findNavController().popBackStack()

        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

}