package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

class CustomerListLoader(
    //private val handler: CustomerListLoaderSubscriber,
    private val repository: OsteoDbRepository
) {
    fun requestCustomersAsFlow(): Flow<List<CustomerEntity>> = repository.getAllCustomersAsFlow()
}

interface CustomerListLoaderSubscriber {
    fun onCustomersLoaded(customers: List<CustomerEntity>)
    fun onCustomersFound(customers: List<CustomerEntity>)
}