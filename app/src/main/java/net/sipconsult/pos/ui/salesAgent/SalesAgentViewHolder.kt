package net.sipconsult.pos.ui.salesAgent

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_sales_agent.view.*
import net.sipconsult.pos.data.models.SalesAgentsItem

class SalesAgentViewHolder(
    itemView: View,
    onSalesAgentClick: (SalesAgentsItem) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private lateinit var _saleAgent: SalesAgentsItem
    private var _position: Int = 0
    private var index: Int = 0
    fun bind(
        saleAgent: SalesAgentsItem,
        position: Int
    ) {
        _saleAgent = saleAgent
        _position = position
        itemView.textSalesAgentName.text = saleAgent.displayName
//        if (index == _position) {
//            itemView.setBackgroundColor(Color.parseColor("#FFEB3B"));
//            itemView.textSalesAgentName.setTextColor(Color.parseColor("#ffffff"));
//        } else {
//            itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//            itemView.textSalesAgentName.setTextColor(Color.parseColor("#6200EA"));
//        }
    }


    init {
        itemView.setOnClickListener {
            onSalesAgentClick(_saleAgent)
//            index = _position
        }
    }
}