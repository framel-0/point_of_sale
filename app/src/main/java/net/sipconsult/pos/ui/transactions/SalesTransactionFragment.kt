package net.sipconsult.pos.ui.transactions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.sales_transaction_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.SalesTransactionsItem
import net.sipconsult.pos.ui.base.ScopedFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SalesTransactionFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory: SalesTransactionViewModelFactory by instance()
    private lateinit var viewModel: SalesTransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sales_transaction_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(SalesTransactionViewModel::class.java)
        bindUI()
    }

    private fun ldIn() = launch {
        val result = viewModel.getSaleTransactions.await()
        viewModel.updateTransactionResult(result)
    }

    private fun bindUI() = launch {

        ldIn()
        if (view != null) {
            viewModel.transactionsResult.observe(
                viewLifecycleOwner,
                Observer { result ->
                    result ?: return@Observer
                    groupSaleTransactionLoading.visibility = View.GONE

                    result.error?.let {
//                    showVoucherFailed(it)
                    }
                    result.success?.let {
                        setupTransactionRecyclerView(it)
                    }
                })
        }

    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }

    private fun setupSearchView(salesTransactionListAdapter: SalesTransactionListAdapter) {
        searchSalesTransaction.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                salesTransactionListAdapter.filter.filter(newText)
                return false
            }

        })
    }


    private fun setupTransactionRecyclerView(transactions: ArrayList<SalesTransactionsItem>) {
        val salesTransactionListAdapter =
            SalesTransactionListAdapter(::onTransactionClick)
        listSaleTransaction.adapter = salesTransactionListAdapter
        listSaleTransaction.addItemDecoration(
            DividerItemDecoration(
                listSaleTransaction.context,
                DividerItemDecoration.VERTICAL
            )
        )
        salesTransactionListAdapter.setSalesTransaction(transactions)
        setupSearchView(salesTransactionListAdapter)
    }

    private fun onTransactionClick(item: SalesTransactionsItem) {
        val action =
            SalesTransactionFragmentDirections.actionNavSalesTransactionToRefundFragment(item.id)
        this.findNavController().navigate(action)
    }

}