package net.sipconsult.pos.data.datasource.client.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.network.response.Clients

interface ClientNetworkDataSource {
    val downloadClients: LiveData<Clients>

    suspend fun fetchClients()
}