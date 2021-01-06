package net.sipconsult.pos.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.pos.data.network.response.ProductCategories
import net.sipconsult.pos.internal.NoConnectivityException

class CategoryNetworkDataSourceImpl(
    private val shopApiService: ShopApiService
) : CategoryNetworkDataSource {

    private val _downloadCategories = MutableLiveData<ProductCategories>()

    override val downloadCategories: LiveData<ProductCategories>
        get() = _downloadCategories

    override suspend fun fetchCategories() {
        try {
            val fetchedCategory = shopApiService.getProductCategoriesAsync().await()
            _downloadCategories.postValue(fetchedCategory)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchCategories: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "CategoryNetworkDataSrc"
    }
}