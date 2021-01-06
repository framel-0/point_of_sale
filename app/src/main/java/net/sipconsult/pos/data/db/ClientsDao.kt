package net.sipconsult.pos.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.sipconsult.pos.data.models.ClientItem

@Dao
interface ClientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(products: List<ClientItem>)

    @Query("SELECT * FROM clients WHERE id = :clientId ")
    fun getProduct(clientId: Int): LiveData<ClientItem>


    @get:Query("SELECT * FROM clients")
    val getClients: LiveData<List<ClientItem>>
}