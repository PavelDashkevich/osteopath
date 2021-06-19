package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerListLoader(
    private val handler: CustomerListLoaderSubscriber,
    private val repository: OsteoDbRepository
) {
    fun requestCustomers() {
        CoroutineScope(Dispatchers.Main).launch {
            val customers: List<CustomerEntity> = repository.getAllCustomers()

            handler.onCustomersLoaded(customers)
        }
    }
}

interface CustomerListLoaderSubscriber {
    fun onCustomersLoaded(customers: List<CustomerEntity>)
}