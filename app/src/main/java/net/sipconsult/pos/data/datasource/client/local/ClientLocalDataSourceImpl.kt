package net.sipconsult.pos.data.datasource.client.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.db.ClientsDao
import net.sipconsult.pos.data.models.ClientItem

class ClientLocalDataSourceImpl(private val clientsDao: ClientsDao) :
    ClientLocalDataSource {
    override val clients: LiveData<List<ClientItem>>
        get() = clientsDao.getClients

    override fun updateClients(clients: List<ClientItem>) {
        clientsDao.upsertAll(clients)
    }
}