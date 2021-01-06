package net.sipconsult.pos.data.datasource.discountTypes.local

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.db.DiscountTypeDao
import net.sipconsult.pos.data.models.DiscountTypesItem

class DiscountTypesLocalDataSourceImpl(private val discountTypeDao: DiscountTypeDao) :
    DiscountTypesLocalDataSource {

    override val discountTypes: LiveData<List<DiscountTypesItem>>
        get() = discountTypeDao.getDiscountTypes

    override fun updateDiscountTypes(discountTypes: List<DiscountTypesItem>) {
        discountTypeDao.deleteAll()
        discountTypeDao.upsertAll(discountTypes)
    }
}