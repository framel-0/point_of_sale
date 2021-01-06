package net.sipconsult.pos.data.datasource.salesAgent.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.pos.data.network.ShopApiService
import net.sipconsult.pos.data.network.response.SalesAgents
import net.sipconsult.pos.internal.NoConnectivityException

class SalesAgentNetworkDataSourceImpl(
    private val shopApiService: ShopApiService
) : SalesAgentNetworkDataSource {

    private val _downloadSalesAgents = MutableLiveData<SalesAgents>()

    override val downloadSalesAgents: LiveData<SalesAgents>
        get() = _downloadSalesAgents

    override suspend fun fetchSalesAgents() {
        try {
            val fetchedSalesAgents = shopApiService.getSalesAgentsAsync()
            _downloadSalesAgents.postValue(fetchedSalesAgents)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchProducts: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "SalesAgentNetDataSrc"
    }
}