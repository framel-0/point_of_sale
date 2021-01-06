package net.sipconsult.pos.data.datasource.client.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.ClientItem

interface ClientLocalDataSource {
    val clients: LiveData<List<ClientItem>>

    fun updateClients(clients: List<ClientItem>)
}