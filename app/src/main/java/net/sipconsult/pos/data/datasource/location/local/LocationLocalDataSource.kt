package net.sipconsult.pos.data.datasource.location.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.LocationsItem

interface LocationLocalDataSource {
    val locations: LiveData<List<LocationsItem>>

    fun location(locationCode: String): LocationsItem

    fun updateLocations(locations: List<LocationsItem>)
}