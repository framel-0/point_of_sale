package net.sipconsult.pos.data.datasource.product.local

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.db.ProductsDao
import net.sipconsult.pos.data.models.ProductItem

class ProductLocalDataSourceImpl(
    private val productsDao: ProductsDao
) : ProductLocalDataSource {
/*    val list = arrayListOf<ProductItem>(
        ProductItem(
            id = 1,
            imageUrl = "images/grapes.jpg",
            name = "Black Grape",
            salePrice = 100.0,
            costPrice = 200.2,
            categoryId = 1,
            description = "",
            quantity = 1,
            barcode = 6181002001067,

        ),
        ProductItem(
            id = 2,
            imageUrl = "images/tomato.jpg",
            name = "Tomato",
            salePrice = 112.0,
            costPrice = 200.2,
            categoryId = 1,
            description = "",
            quantity = 1,
            barcode = 618100200106
        ),
        ProductItem(
            id = 3,
            imageUrl = "images/guava.jpg",
            name = "Guava",
            salePrice = 105.0,
            costPrice = 200.2,
            categoryId = 1,
            description = "",
            quantity = 1,
            barcode = 618100200106
        ),
        ProductItem(
            id = 4,
            imageUrl = "images/kiwi.jpg",
            name = "Kiwi",
            salePrice = 90.0,
            costPrice = 200.2,
            categoryId = 1,
            description = "",
            quantity = 1,
            barcode = 618100200106
        ),
        ProductItem(
            id = 5,
            imageUrl = "images/lemons.jpg",
            name = "Lemon",
            salePrice = 70.0,
            costPrice = 200.2,
            categoryId = 1,
            description = "",
            quantity = 1,
            barcode = 618100200106
        ),
        ProductItem(
            id = 6,
            imageUrl = "images/apple.jpg",
            name = "Apple",
            salePrice = 50.0,
            costPrice = 200.2,categoryId = 1,
            description = "",
            quantity = 1,
            barcode = 618100200106
        )
    )*/


    //    private val _products = MutableLiveData<Products>().apply {
//        value =
//            productsDao.getProducts
//    }
    override val products: LiveData<List<ProductItem>>
        get() = productsDao.getProducts

    override fun updateProducts(products: List<ProductItem>) {
//        UpsertAsyncTask(productsDao).execute(products)
        productsDao.deleteAll()
        productsDao.upsertAll(products)
    }

    companion object {
        class UpsertAsyncTask(private val productsDao: ProductsDao) :
            AsyncTask<List<ProductItem>, Void, Void>() {
            override fun doInBackground(vararg params: List<ProductItem>): Void? {
                productsDao.upsertAll(params[0])
                return null
            }

        }


    }
}