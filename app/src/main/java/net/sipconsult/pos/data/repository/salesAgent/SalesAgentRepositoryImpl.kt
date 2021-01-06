package net.sipconsult.pos.data.repository.salesAgent

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sipconsult.pos.data.datasource.salesAgent.local.SalesAgentLocalDataSource
import net.sipconsult.pos.data.datasource.salesAgent.network.SalesAgentNetworkDataSource
import net.sipconsult.pos.data.models.SalesAgentsItem

class SalesAgentRepositoryImpl(
    private val networkDataSource: SalesAgentNetworkDataSource,
    private val localDataSource: SalesAgentLocalDataSource
) : SalesAgentRepository {

    init {
        networkDataSource.downloadSalesAgents.observeForever { currentLocations ->
            persistFetchedSalesAgents(currentLocations)
        }
    }

    override suspend fun getSalesAgents(): LiveData<List<SalesAgentsItem>> {
        return withContext(Dispatchers.IO) {
            initSalesAgentsData()
            return@withContext localDataSource.salesAgents
        }
    }

    override fun getSalesAgent(salesAgentId: Int): SalesAgentsItem {
        return localDataSource.salesAgent(salesAgentId)
    }

    override fun getSalesAgentsLocal(): LiveData<List<SalesAgentsItem>> {
        return localDataSource.salesAgents
    }

    private fun persistFetchedSalesAgents(fetchedSalesAgents: List<SalesAgentsItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            localDataSource.updateSalesAgent(fetchedSalesAgents)
        }
    }

    private suspend fun initSalesAgentsData() {
        fetchSalesAgents()

    }

    private suspend fun fetchSalesAgents() {
        networkDataSource.fetchSalesAgents()
    }
}
