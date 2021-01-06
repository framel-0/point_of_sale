package net.sipconsult.pos.ui.service

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.data.models.ServiceItem

class ServiceViewHolder(itemView: View, onServiceClick: (ServiceItem) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var _service: ServiceItem
    fun bind(service: ServiceItem, position: Int) {
        _service = service
    }

    init {
        itemView.setOnClickListener { onServiceClick(_service) }
    }
}