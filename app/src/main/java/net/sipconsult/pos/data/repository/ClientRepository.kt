package net.sipconsult.pos.data.repository

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.ClientItem

interface ClientRepository {
    suspend fun getClients(): LiveData<List<ClientItem>>

    fun getClientsLocal(): LiveData<List<ClientItem>>
}