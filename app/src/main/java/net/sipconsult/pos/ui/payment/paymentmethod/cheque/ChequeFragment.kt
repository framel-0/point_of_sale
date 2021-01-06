package net.sipconsult.pos.ui.payment.paymentmethod.cheque

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
import kotlinx.android.synthetic.main.cheque_fragment.*
import net.sipconsult.pos.R
import net.sipconsult.pos.SharedViewModel
import net.sipconsult.pos.databinding.ChequeFragmentBinding

class ChequeFragment : Fragment() {

    private var _binding: ChequeFragmentBinding? = null

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

        _binding = ChequeFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel.editTextChequeNumber.observe(viewLifecycleOwner, Observer { chequeNumber ->

            if (!chequeNumber.isNullOrEmpty()) {
                sharedViewModel.chequeNumber = chequeNumber.trim()
            } else {
                sharedViewModel.chequeNumber = ""
            }

        })

        sharedViewModel.editTextChequeAmount.observe(viewLifecycleOwner, Observer { amount ->
            if (!amount.isNullOrEmpty()) {
                sharedViewModel.chequeAmount = amount.trim().toDouble()
                sharedViewModel.deduct()
            } else {
                sharedViewModel.chequeAmount = 0.0
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
                        2 -> {
                            findNavController().navigate(R.id.mobileMoneyFragment)
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

        keyboardCheque.visibility = View.GONE
//        editTextVisaChequeNumber.visibility = View.GONE
//        textVisa.visibility = View.GONE

        disableEditText(editTextChequeDue)
        disableEditText(editTextChequeChange)
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