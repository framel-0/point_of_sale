package net.sipconsult.pos.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.sipconsult.pos.data.models.DiscountTypesItem

@Dao
interface DiscountTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(products: List<DiscountTypesItem>)

    @Query("SELECT * FROM discount_types WHERE id = :productId ")
    fun getProduct(productId: Int): LiveData<DiscountTypesItem>


    @get:Query("SELECT * FROM discount_types")
    val getDiscountTypes: LiveData<List<DiscountTypesItem>>

    @Query("DELETE FROM discount_types")
    fun deleteAll()
}