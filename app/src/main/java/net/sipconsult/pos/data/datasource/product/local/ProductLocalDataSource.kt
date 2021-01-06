package net.sipconsult.pos.data.datasource.product.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.ProductItem

interface ProductLocalDataSource {
    val products: LiveData<List<ProductItem>>

    fun updateProducts(products: List<ProductItem>)
}