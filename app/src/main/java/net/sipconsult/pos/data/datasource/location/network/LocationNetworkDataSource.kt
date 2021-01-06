package net.sipconsult.pos.data.datasource.location.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.network.response.Locations

interface LocationNetworkDataSource {
    val downloadLocations: LiveData<Locations>

    suspend fun fetchLocations()
}