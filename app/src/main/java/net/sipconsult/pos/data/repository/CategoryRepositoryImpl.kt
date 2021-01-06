package net.sipconsult.pos.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.sipconsult.pos.data.network.CategoryNetworkDataSource
import net.sipconsult.pos.data.network.response.ProductCategories


class CategoryRepositoryImpl(
    private val categoryNetworkDataSource: CategoryNetworkDataSource
) : CategoryRepository {

    init {
        categoryNetworkDataSource.downloadCategories.observeForever {
            persistFetchedCategories()
        }
    }

    override suspend fun getCategories(): LiveData<ProductCategories> {
        TODO("Not yet implemented")
    }

    private fun persistFetchedCategories(){
        GlobalScope.launch(Dispatchers.IO) {

        }
    }
}