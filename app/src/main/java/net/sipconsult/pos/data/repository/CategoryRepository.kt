package net.sipconsult.pos.data.repository

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.network.response.ProductCategories

interface CategoryRepository {
    suspend fun getCategories(): LiveData<ProductCategories>
}