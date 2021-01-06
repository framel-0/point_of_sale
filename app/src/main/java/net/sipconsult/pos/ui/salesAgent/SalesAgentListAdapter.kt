package net.sipconsult.pos.ui.salesAgent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.SalesAgentsItem

class SalesAgentListAdapter(
    private val onSalesAgentClick: (SalesAgentsItem) -> Unit
) : RecyclerView.Adapter<SalesAgentViewHolder>() {

    private var _salesAgents = arrayListOf<SalesAgentsItem>()

    var index = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesAgentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_item_sales_agent, parent, false)

        return SalesAgentViewHolder(itemView, onSalesAgentClick)
    }

    fun setSalesAgent(salesAgents: ArrayList<SalesAgentsItem>) {
        _salesAgents = salesAgents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = _salesAgents.size

    override fun onBindViewHolder(holder: SalesAgentViewHolder, position: Int) {
        val saleAgent = _salesAgents[position]
        holder.bind(saleAgent, position)
    }
}