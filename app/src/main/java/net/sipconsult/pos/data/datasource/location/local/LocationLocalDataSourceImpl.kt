package net.sipconsult.pos.data.datasource.location.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.db.LocationDao
import net.sipconsult.pos.data.models.LocationsItem

class LocationLocalDataSourceImpl(
    private val locationDao: LocationDao
) : LocationLocalDataSource {
    override val locations: LiveData<List<LocationsItem>>
        get() = locationDao.getLocations

    override fun location(locationCode: String): LocationsItem {
        val location = locationDao.getLocation(locationCode)
        return location
    }

    override fun updateLocations(locations: List<LocationsItem>) {
        locationDao.deleteAll()
        locationDao.upsertAll(locations)
    }

    companion object {
//        class UpsertAsyncTask(private val locationDao: LocationDao,private val locationId: Int) :
//            AsyncTask<List<ProductItem>, Void, Void>() {
//            override fun doInBackground(vararg params: List<ProductItem>): Void? {
//                return locationDao.getLocation(locationId)
//
//            }
//
//        }


    }
}