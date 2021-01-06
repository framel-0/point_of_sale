package net.sipconsult.pos.data.datasource.product.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.pos.data.network.ShopApiService
import net.sipconsult.pos.data.network.response.Products
import net.sipconsult.pos.internal.NoConnectivityException

class ProductNetworkDataSourceImpl(
    private val shopApiService: ShopApiService
) : ProductNetworkDataSource {

    private val _downloadProducts = MutableLiveData<Products>()

    override val downloadProducts: LiveData<Products>
        get() = _downloadProducts

    override suspend fun fetchProducts() {
        try {
            val fetchedProducts = shopApiService.getProductsAsync()
            _downloadProducts.postValue(fetchedProducts)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchProducts: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "ProductNetworkDataSrc"
    }
}