package net.sipconsult.pos.data.datasource.product.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.network.response.Products

interface ProductNetworkDataSource {
    val downloadProducts: LiveData<Products>

    suspend fun fetchProducts()
}