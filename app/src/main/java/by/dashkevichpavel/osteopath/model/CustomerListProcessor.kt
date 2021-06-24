package by.dashkevichpavel.osteopath.model

import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class CustomerListProcessor(
    private val handler: CustomerListProcessorSubscriber,
    private val repository: OsteoDbRepository,
    private val scope: CoroutineScope
) {
    private var filterValues = FilterValues()
    private var searchQuery: String = ""
    private var allCustomers: List<CustomerEntity> = emptyList()
    private var jobFlow: Job? = null

    init {
        startCustomersTableChangeListening()
    }

    fun setFilter(newFilterValues: FilterValues) {
        if (newFilterValues != filterValues) {
            filterValues = newFilterValues
            if (jobFlow != null) {
                updateFilteredCustomersList()
            }
        }
    }

    fun setQueryString(newSearchQuery: String) {
        if (!newSearchQuery.equals(searchQuery, true)) {
            searchQuery = newSearchQuery
            updateFilteredCustomersList()
        }
    }

    private fun startCustomersTableChangeListening() {
        if (jobFlow == null) {
            jobFlow = scope.launch {
                repository.getAllCustomersAsFlow().collect { listOfCustomers ->
                    allCustomers = listOfCustomers
                    updateFilteredCustomersList()
                }
            }
        }
    }

    private fun updateFilteredCustomersList() {
        var newFilteredList: List<CustomerEntity> = allCustomers

        // filter by category ang by age
        if (!filterValues.isFilterOff()) {
            val listOfSelectedCustomerStatusIds: MutableList<Int> = mutableListOf()

            if (filterValues.byCategoryWork) {
                listOfSelectedCustomerStatusIds.add(CustomerStatus.WORK.id)
            }

            if (filterValues.byCategoryWorkDone) {
                listOfSelectedCustomerStatusIds.add(CustomerStatus.WORK_DONE.id)
            }

            if (filterValues.byCategoryNoHelp) {
                listOfSelectedCustomerStatusIds.add(CustomerStatus.NO_HELP.id)
            }

            if (listOfSelectedCustomerStatusIds.isNotEmpty()) {
                newFilteredList = newFilteredList.filter { customer ->
                    listOfSelectedCustomerStatusIds.contains(customer.customerStatusId)
                }
            }

            if (!(filterValues.byAgeChildren && filterValues.byAgeAdults)) {
                val now = Date()
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = now.time
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                calendar.add(Calendar.YEAR, -18)

                val age18 = Date(calendar.timeInMillis)

                newFilteredList = if (filterValues.byAgeChildren) {
                    newFilteredList.filter { customer ->
                        customer.birthDate.time > age18.time
                    }
                } else {
                    newFilteredList.filter { customer ->
                        customer.birthDate.time <= age18.time
                    }
                }
            }
        }

        // filter by search query
        if (searchQuery.isNotEmpty()) {
            newFilteredList = newFilteredList.filter { customer ->
                customer.name.contains(searchQuery, true)
            }
        }

        handler.onCustomersProcessed(newFilteredList)
    }
}

interface CustomerListProcessorSubscriber {
    fun onCustomersProcessed(customers: List<CustomerEntity>)
}