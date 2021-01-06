package net.sipconsult.pos.ui.payment.paymentmethod.mobilemoney

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.mobile_money_fragment.*
import net.sipconsult.pos.R
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.databinding.MobileMoneyFragmentBinding
import net.sipconsult.pos.ui.base.ScopedFragment

class MobileMoneyFragment : ScopedFragment() {

    private var _binding: MobileMoneyFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        _binding = MobileMoneyFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel.editTextMobileMoneyPhoneNumber.observe(
            viewLifecycleOwner,
            Observer { phoneNumber ->

                if (!phoneNumber.isNullOrEmpty()) {
                    sharedViewModel.mobileMoneyPhoneNumber = phoneNumber.trim()
                } else {
                    sharedViewModel.mobileMoneyPhoneNumber = ""
                }

            })

        sharedViewModel.editTextMobileMoneyAmount.observe(viewLifecycleOwner, Observer { amount ->
            if (!amount.isNullOrEmpty()) {
                sharedViewModel.mobileMoneyAmount = amount.trim().toDouble()
                sharedViewModel.deduct()
            } else {
                sharedViewModel.mobileMoneyAmount = 0.0
                sharedViewModel.deduct()
            }

        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        buildUI()
    }

    private fun buildUI() {
        sharedViewModel.selectedPaymentMethod.observe(
            viewLifecycleOwner,
            Observer { paymentMethod ->
                if (paymentMethod != null) {

                    when (paymentMethod.id) {
                        1 -> {
                            findNavController().navigate(R.id.cashFragment)
                        }
                        4 -> {
                            findNavController().navigate(R.id.chequeFragment)
                        }
                        5 -> {
                            findNavController().navigate(R.id.loyaltyFragment)
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

//        textMobileMoney.visibility = View.GONE
//        editTextMobileMoneyPhoneNumber.visibility = View.GONE

        disableEditText(editTextMobileMoneyDue)
        disableEditText(editTextMobileMoneyChange)

    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
//        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
//        editText.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}