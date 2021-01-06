package net.sipconsult.pos.data.repository.customer

import androidx.lifecycle.LiveData
import net.sipconsult.pos.data.models.CustomerItem

interface CustomerRepository {

    suspend fun getCustomer(): LiveData<List<CustomerItem>>
    fun getCustomer(customerCode: String): CustomerItem
    fun getCustomerLocal(): LiveData<List<CustomerItem>>
}