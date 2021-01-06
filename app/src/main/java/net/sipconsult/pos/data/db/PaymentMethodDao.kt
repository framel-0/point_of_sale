package net.sipconsult.pos.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.sipconsult.pos.data.models.PaymentMethodItem

@Dao
interface PaymentMethodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(paymentMethods: List<PaymentMethodItem>)

    @Query("SELECT * FROM payment_methods WHERE id = :id ")
    fun getPaymentMethod(id: Int): LiveData<PaymentMethodItem>

    @get:Query("SELECT * FROM payment_methods")
    val paymentMethods: LiveData<List<PaymentMethodItem>>

    @Query("DELETE FROM payment_methods")
    fun deleteAll()
}