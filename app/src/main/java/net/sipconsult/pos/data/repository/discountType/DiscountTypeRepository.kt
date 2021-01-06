package net.sipconsult.pos.data.repository.discountType

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.DiscountTypesItem

interface DiscountTypeRepository {

    suspend fun getDiscountTypes(): LiveData<List<DiscountTypesItem>>
}