package net.sipconsult.pos.data.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.network.response.ProductCategories

interface CategoryNetworkDataSource {

    val downloadCategories: LiveData<ProductCategories>

    suspend fun fetchCategories()
}