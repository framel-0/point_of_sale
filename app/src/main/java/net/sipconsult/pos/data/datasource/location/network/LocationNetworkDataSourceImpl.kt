package net.sipconsult.pos.data.datasource.location.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.pos.data.network.ShopApiService
import net.sipconsult.pos.data.network.response.Locations
import net.sipconsult.pos.internal.NoConnectivityException

class LocationNetworkDataSourceImpl(
    private val shopApiService: ShopApiService
) : LocationNetworkDataSource {

    private val _downloadLocations = MutableLiveData<Locations>()

    override val downloadLocations: LiveData<Locations>
        get() = _downloadLocations

    override suspend fun fetchLocations() {
        try {
            val fetchedProducts = shopApiService.getLocationsAsync()
            _downloadLocations.postValue(fetchedProducts)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchProducts: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "LocatoinNetworkDataSrc"
    }
}