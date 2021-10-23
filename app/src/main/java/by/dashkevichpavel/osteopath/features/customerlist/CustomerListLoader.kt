package by.dashkevichpavel.osteopath.features.customerlist

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
    private val operationsChannel = Channel<Operation>(BUFFER_SIZE)
    private var jobFlow: Job? = null

    init {
        scope.launch {
            while (isActive) {
                when (operationsChannel.receive()) {
                    Operation.StartJob -> {
                        if (jobFlow == null || jobFlow?.isCompleted == true) {
                            jobFlow = scope.launch {
                                repository.getAllCustomersAsFlow().collect { listOfCustomers ->
                                    customerListLoaderSubscriber.onCustomersLoaded(listOfCustomers)
                                }
                            }
                        }
                    }
                    Operation.StopJob -> {
                        if (jobFlow?.isCompleted == false) {
                            jobFlow?.cancelAndJoin()
                        }
                    }
                }
            }
        }
        startCustomersTableObserving()
    }

    fun startCustomersTableObserving() {
        scope.launch {
            operationsChannel.send(Operation.StartJob)
        }
    }

    fun stopCustomersTableObserving() {
        scope.launch {
            operationsChannel.send(Operation.StopJob)
        }
    }

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

    companion object {
        private const val BUFFER_SIZE = 3
    }

    private sealed class Operation {
        object StartJob : Operation()
        object StopJob : Operation()
    }
}

interface CustomerListLoaderSubscriber {
    fun onCustomersLoaded(customers: List<Customer>)
}