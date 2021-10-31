package by.dashkevichpavel.osteopath.features.customerlist

import by.dashkevichpavel.osteopath.helpers.jobs.FlowJobController
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.util.*

class CustomerListLoader(
    private val customerListLoaderSubscriber: CustomerListLoaderSubscriber,
    private val repository: LocalDbRepository,
    private val scope: CoroutineScope
) {
    private val flowJobController = FlowJobController(
        scope,
        suspend {
            repository.getAllCustomersAsFlow().collect { listOfCustomers ->
                customerListLoaderSubscriber.onCustomersLoaded(listOfCustomers)
            }
        }
    )

    init {
        startCustomersTableObserving()
    }

    fun startCustomersTableObserving() = flowJobController.start()
    fun stopCustomersTableObserving() = flowJobController.stop()

    fun putCustomerInArchive(customerId: Long) = updateCustomerIsArchived(customerId, true)

    fun removeCustomerFromArchive(customerId: Long) = updateCustomerIsArchived(customerId, false)

    fun deleteCustomer(customerId: Long) {
        scope.launch {
            repository.deleteCustomerById(customerId)
        }
    }

    private fun updateCustomerIsArchived(customerId: Long, isArchived: Boolean) {
        scope.launch {
            repository.updateCustomerIsArchived(customerId, isArchived)
        }
    }
}

interface CustomerListLoaderSubscriber {
    fun onCustomersLoaded(customers: List<Customer>)
}