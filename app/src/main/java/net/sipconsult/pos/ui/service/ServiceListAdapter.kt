package net.sipconsult.pos.ui.service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.ServiceItem

class ServiceListAdapter(private val onServiceClick: (ServiceItem) -> Unit) :
    RecyclerView.Adapter<ServiceViewHolder>() {

    private var _services = arrayListOf<ServiceItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item_service, parent, false)
        return ServiceViewHolder(itemView, onServiceClick)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = _services[position]
        holder.bind(service, position)
    }

    override fun getItemCount(): Int = _services.size

    fun setServices(services: ArrayList<ServiceItem>) {
        _services = services
        notifyDataSetChanged()
    }
}