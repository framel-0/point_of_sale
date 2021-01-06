package net.sipconsult.pos.data.repository.product

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.ProductItem

interface ProductRepository {
    suspend fun getProducts(): LiveData<List<ProductItem>>
    fun getProductsLocal(): LiveData<List<ProductItem>>

}