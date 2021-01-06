package net.sipconsult.pos.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.sipconsult.pos.data.models.LocationsItem

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(products: List<LocationsItem>)

    @Query("SELECT * FROM locations WHERE code = :locationCode")
    fun getLocation(locationCode: String): LocationsItem


    @get:Query("SELECT * FROM locations")
    val getLocations: LiveData<List<LocationsItem>>

    @Query("DELETE FROM locations")
    fun deleteAll()
}