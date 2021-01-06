package net.sipconsult.pos.data.datasource.salesAgent.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.network.response.SalesAgents

interface SalesAgentNetworkDataSource {

    val downloadSalesAgents: LiveData<SalesAgents>

    suspend fun fetchSalesAgents()
}