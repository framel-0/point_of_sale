package net.sipconsult.pos.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.sipconsult.pos.data.models.ProductItem

@Dao
interface ProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(products: List<ProductItem>)

    @Query("SELECT * FROM products WHERE product_code = :productCode")
    fun getProduct(productCode: String): LiveData<ProductItem>

    @get:Query("SELECT * FROM products")
    val getProducts: LiveData<List<ProductItem>>

    @Query("DELETE FROM products")
    fun deleteAll()
}