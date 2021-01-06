package net.sipconsult.pos.ui.payment.paymentmethod.loyalty

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.loyalty_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.pos.R
import net.sipconsult.pos.ScanningActivity
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.data.models.Voucher
import net.sipconsult.pos.databinding.LoyaltyFragmentBinding
import net.sipconsult.pos.ui.base.ScopedFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class LoyaltyFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private var _binding: LoyaltyFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var viewModel: LoyaltyViewModel
    private val viewModelFactory: LoyaltyViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(LoyaltyViewModel::class.java)

        _binding = LoyaltyFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buildUI()
    }

    private fun buildUI() {

        if (sharedViewModel.scanSuccessful) {
            groupLoyaltyScan.visibility = View.GONE
            groupLoyaltyLoading.visibility = View.GONE
            groupLoyaltyResult.visibility = View.VISIBLE
        } else {
            groupLoyaltyScan.visibility = View.VISIBLE
            groupLoyaltyLoading.visibility = View.GONE
            groupLoyaltyResult.visibility = View.GONE
        }


//        groupLoyaltyScan.visibility = View.GONE

//        textLoyaltyTender.visibility = View.GONE
//        editTextLoyaltyTender.visibility = View.GONE

        viewModel.voucherResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                groupLoyaltyLoading.visibility = View.GONE

                result.error?.let {
                    sharedViewModel.scanSuccessful = false
                    groupLoyaltyScan.visibility = View.VISIBLE
                    showVoucherFailed(it)
                }
                result.success?.let {
                    sharedViewModel.scanSuccessful = true
                    groupLoyaltyResult.visibility = View.VISIBLE
                    updateUiWithVoucher(it)
                }
            })

        sharedViewModel.editTextLoyaltyAmount.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                sharedViewModel.loyaltyAmount = it.trim().toDouble()
                sharedViewModel.deduct()
            } else {
                sharedViewModel.loyaltyAmount = 0.0
                sharedViewModel.deduct()
            }
        })
        sharedViewModel.selectedPaymentMethod.observe(viewLifecycleOwner, Observer {

                paymentMethod ->
            if (paymentMethod != null) {

                when (paymentMethod.id) {
                    1 -> {
                        findNavController().navigate(R.id.cashFragment)
                    }
                    2 -> {
                        findNavController().navigate(R.id.mobileMoneyFragment)
                    }
                    4 -> {
                        findNavController().navigate(R.id.chequeFragment)
                    }
                    3 -> {
                        findNavController().navigate(R.id.cardFragment)
                    }
                    6 -> {
                        findNavController().navigate(R.id.complimentaryFragment)
                    }
                    7 -> {
                        findNavController().navigate(R.id.cashDollarFragment)
                    }
                }
            }
        })

        buttonScanQrCode.setOnClickListener {
            startExternalScanSunmi()
        }

        disableEditText(editTextLoyaltyDue)
        disableEditText(editTextLoyaltyChange)
    }

    private fun updateUiWithVoucher(voucher: Voucher) {
        val price = sharedViewModel.totalPrice.value
//        editTextLoyaltyDue.setText(price)
        editTextLoyaltyValue.setText(voucher.value.toString())
        disableEditText(editTextLoyaltyValue)
        sharedViewModel.voucherId = voucher.id
        sharedViewModel.loyaltyAmount = voucher.value
        sharedViewModel.deduct()

    }

    private fun showVoucherFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
//        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
//        editText.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun startScanZxing() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setPrompt("Scan QR Code")
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    private fun startExternalScanSunmi() {
        val intent = Intent(context, ScanningActivity::class.java)
        startActivityForResult(intent, EXTERNAL_SCANNER_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == INTERNAL_SCANNER_CODE && data != null) {
            val bundle = data.extras
            val result = bundle!!.getSerializable("data") as ArrayList<HashMap<String, String>>

            val it: MutableIterator<Any>? = result.iterator()

            while (it!!.hasNext()) {
                val hashMap = it.next() as HashMap<String, String>

                val scanType = hashMap["TYPE"]!!
                val scanResult = hashMap["VALUE"]!!

                Log.i("sunmi_scanner_type: ", scanType)//this is the type of the code
//                Toast.makeText(activity, "Scan Type：$scanType", Toast.LENGTH_SHORT).show()

                Log.i("sunmi_scanner_result: ", scanResult)//this is the result of the code
//                Toast.makeText(activity, "Results：$scanResult", Toast.LENGTH_SHORT).show()

                val barcode: String = scanResult
                addScannedVoucher(barcode)

            }
        }
        if (requestCode == EXTERNAL_SCANNER_CODE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                val barcode: String = data.getStringExtra("product_barcode").toString()
                addScannedVoucher(barcode)

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            }

        }
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                scanResult(result)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun scanResult(result: IntentResult?) {
        Toast.makeText(activity, "Scanned: " + result!!.contents, Toast.LENGTH_SHORT).show()
        groupLoyaltyLoading.visibility = View.VISIBLE
        groupLoyaltyScan.visibility = View.GONE
        viewModel.voucherCode = result.contents.toString()
        ldIn()
    }

    private fun addScannedVoucher(barcode: String) {
        groupLoyaltyLoading.visibility = View.VISIBLE
        groupLoyaltyScan.visibility = View.GONE
        viewModel.voucherCode = barcode
        ldIn()
    }

    private fun ldIn() = launch {
        val result = viewModel.getVoucher.await()
        viewModel.updateVoucherResult(result)
    }

    companion object {
        private const val INTERNAL_SCANNER_CODE: Int = 100
        private const val EXTERNAL_SCANNER_CODE: Int = 101
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}