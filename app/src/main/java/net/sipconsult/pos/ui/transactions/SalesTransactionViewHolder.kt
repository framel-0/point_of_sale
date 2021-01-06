package net.sipconsult.pos.ui.transactions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_sales_transaction.view.*
import net.sipconsult.pos.data.models.SalesTransactionsItem

class SalesTransactionViewHolder(
    itemView: View,
    onTransactionClick: (SalesTransactionsItem) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private var _position: Int = 0
    private lateinit var _salesTransaction: SalesTransactionsItem
    fun bind(
        salesTransaction: SalesTransactionsItem,
        position: Int
    ) {
        _position = position
        _salesTransaction = salesTransaction
        itemView.textSaleTransactionDate.text = salesTransaction.date
        itemView.textSaleTransactionLocation.text = salesTransaction.location?.name ?: ""
        itemView.textSaleTransactionReceiptNumber.text = salesTransaction.receiptNumber
        itemView.textSaleTransactionTotalSales.text = salesTransaction.totalSalesStr
    }

    init {
        itemView.setOnClickListener {
            onTransactionClick(_salesTransaction)
        }
    }
}