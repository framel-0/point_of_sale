package net.sipconsult.pos.data.datasource.client.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.pos.data.network.ShopApiService
import net.sipconsult.pos.data.network.response.Clients
import net.sipconsult.pos.internal.NoConnectivityException

class ClientNetworkDataSourceImpl(private val shopApiService: ShopApiService) :
    ClientNetworkDataSource {

    private val _downloadClients = MutableLiveData<Clients>()

    override val downloadClients: LiveData<Clients>
        get() = _downloadClients

    override suspend fun fetchClients() {
        try {
            val fetchedClients = shopApiService.getClientsAsync()
            _downloadClients.postValue(fetchedClients)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchProducts: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "ClientNetworkDataSrc"
    }
}