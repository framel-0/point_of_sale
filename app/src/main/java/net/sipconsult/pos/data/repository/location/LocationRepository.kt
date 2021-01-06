package net.sipconsult.pos.data.repository.location

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.LocationsItem

interface LocationRepository {

    suspend fun getLocations(): LiveData<List<LocationsItem>>
    fun getLocation(locationCode: String): LocationsItem
    fun getLocationsLocal(): LiveData<List<LocationsItem>>
}