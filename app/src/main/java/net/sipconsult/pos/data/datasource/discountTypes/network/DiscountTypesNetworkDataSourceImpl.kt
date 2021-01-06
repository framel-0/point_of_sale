package net.sipconsult.pos.data.datasource.discountTypes.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.pos.data.network.ShopApiService
import net.sipconsult.pos.data.network.response.DiscountTypes
import net.sipconsult.pos.internal.NoConnectivityException

class DiscountTypesNetworkDataSourceImpl(private val shopApiService: ShopApiService) :
    DiscountTypesNetworkDataSource {

    private val _downloadDiscountTypes = MutableLiveData<DiscountTypes>()
    override val downloadDiscountTypes: LiveData<DiscountTypes>
        get() = _downloadDiscountTypes

    override suspend fun fetchDiscountTypes() {
        try {
            val discountTypes = shopApiService.getDiscountTypesAsync()
            _downloadDiscountTypes.postValue(discountTypes)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchProducts: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "DiscountTypesNetDataSrc"
    }
}