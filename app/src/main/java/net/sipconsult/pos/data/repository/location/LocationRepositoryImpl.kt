package net.sipconsult.pos.data.repository.location

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sipconsult.pos.data.datasource.location.local.LocationLocalDataSource
import net.sipconsult.pos.data.datasource.location.network.LocationNetworkDataSource
import net.sipconsult.pos.data.models.LocationsItem

class LocationRepositoryImpl(
    private val networkDataSource: LocationNetworkDataSource,
    private val localDataSource: LocationLocalDataSource
) : LocationRepository {

    init {
        networkDataSource.downloadLocations.observeForever { currentLocations ->
            persistFetchedLocations(currentLocations)
        }
    }

    override suspend fun getLocations(): LiveData<List<LocationsItem>> {
        return withContext(Dispatchers.IO) {
            initLocationsData()
            return@withContext localDataSource.locations
        }
    }

    override fun getLocation(locationCode: String): LocationsItem {
        val location = localDataSource.location(locationCode)
        return location
    }


    override fun getLocationsLocal(): LiveData<List<LocationsItem>> {
        return localDataSource.locations
    }

    private fun persistFetchedLocations(fetchedLocations: List<LocationsItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            localDataSource.updateLocations(fetchedLocations)
        }
    }

    private suspend fun initLocationsData() {
        fetchLocations()

    }

    private suspend fun fetchLocations() {
        networkDataSource.fetchLocations()
    }
}
