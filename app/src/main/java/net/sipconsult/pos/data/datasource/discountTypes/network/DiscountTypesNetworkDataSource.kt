package net.sipconsult.pos.data.datasource.discountTypes.network

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.network.response.DiscountTypes

interface DiscountTypesNetworkDataSource {
    val downloadDiscountTypes: LiveData<DiscountTypes>

    suspend fun fetchDiscountTypes()
}