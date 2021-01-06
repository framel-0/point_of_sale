package net.sipconsult.pos.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.SalesTransactionsItem
import java.util.*

class SalesTransactionListAdapter(private val onTransactionClick: (SalesTransactionsItem) -> Unit) :
    RecyclerView.Adapter<SalesTransactionViewHolder>(), Filterable {

    private var _salesTransaction = arrayListOf<SalesTransactionsItem>()
    private var salesTransactionFilter = arrayListOf<SalesTransactionsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesTransactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item_sales_transaction, parent, false)
        return SalesTransactionViewHolder(itemView, onTransactionClick)
    }

    override fun getItemCount(): Int = salesTransactionFilter.size

    override fun onBindViewHolder(holder: SalesTransactionViewHolder, position: Int) {
        val salesTransaction = salesTransactionFilter[position]
        holder.bind(salesTransaction, position)
    }

    fun setSalesTransaction(salesTransaction: ArrayList<SalesTransactionsItem>) {
        _salesTransaction = salesTransaction
        salesTransactionFilter = salesTransaction
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                salesTransactionFilter = if (charSearch.isEmpty()) {
                    _salesTransaction
                } else {
                    val resultList: ArrayList<SalesTransactionsItem> = arrayListOf()
                    for (transactionsItem in _salesTransaction) {
                        if (transactionsItem.receiptNumber.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(transactionsItem)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = salesTransactionFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                salesTransactionFilter = results?.values as ArrayList<SalesTransactionsItem>
                notifyDataSetChanged()
            }

        }
    }

}