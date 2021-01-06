package net.sipconsult.pos.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sipconsult.pos.data.datasource.client.local.ClientLocalDataSource
import net.sipconsult.pos.data.datasource.client.network.ClientNetworkDataSource
import net.sipconsult.pos.data.models.ClientItem

class ClientRepositoryImpl(
    private val networkDataSource: ClientNetworkDataSource,
    private val localDataSource: ClientLocalDataSource
) : ClientRepository {

    init {
        networkDataSource.downloadClients.observeForever { currentClients ->
            persistFetchedClients(currentClients)
        }
    }

    override suspend fun getClients(): LiveData<List<ClientItem>> {
        return withContext(Dispatchers.IO) {
            initClientsData()
            return@withContext localDataSource.clients
        }
    }

    override fun getClientsLocal(): LiveData<List<ClientItem>> {
        return localDataSource.clients
    }

    private fun persistFetchedClients(fetchedProducts: List<ClientItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            localDataSource.updateClients(fetchedProducts)
        }
    }

    private suspend fun initClientsData() {
        fetchClients()

    }

    private suspend fun fetchClients() {
        networkDataSource.fetchClients()
    }
}