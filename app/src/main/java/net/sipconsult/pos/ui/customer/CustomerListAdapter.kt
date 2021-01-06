package net.sipconsult.pos.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.CustomerItem


class CustomerListAdapter(private val onCustomerClick: (CustomerItem) -> Unit) :
    RecyclerView.Adapter<CustomerViewHolder>() {

    private var _customers = arrayListOf<CustomerItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item_customer, parent, false)
        return CustomerViewHolder(itemView, onCustomerClick)
    }

    override fun getItemCount(): Int = _customers.size


    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customerItem = _customers[position]

        holder.bind(customerItem, position)
    }

    fun setCustomers(customers: ArrayList<CustomerItem>) {
        _customers = customers
        notifyDataSetChanged()
    }


}