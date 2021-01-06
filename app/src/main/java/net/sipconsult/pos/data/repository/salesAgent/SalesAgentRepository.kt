package net.sipconsult.pos.data.repository.salesAgent

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.SalesAgentsItem

interface SalesAgentRepository {
    suspend fun getSalesAgents(): LiveData<List<SalesAgentsItem>>
    fun getSalesAgent(salesAgentId: Int): SalesAgentsItem
    fun getSalesAgentsLocal(): LiveData<List<SalesAgentsItem>>
}