package net.sipconsult.pos.data.datasource.discountTypes.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.DiscountTypesItem

interface DiscountTypesLocalDataSource {

    val discountTypes: LiveData<List<DiscountTypesItem>>

    fun updateDiscountTypes(discountTypes: List<DiscountTypesItem>)
}