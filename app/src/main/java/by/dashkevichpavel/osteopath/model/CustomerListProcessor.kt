package by.dashkevichpavel.osteopath.model

import java.util.*

class CustomerListProcessor(
    private val customerListProcessorSubscriber: CustomerListProcessorSubscriber
) {
    private var filterValues = FilterValues()
    private var searchQuery: String = ""
    private var allCustomers: List<Customer> = listOf()

    fun setList(customers: List<Customer>) {
        allCustomers = customers
        processCustomersList()
    }

    fun setFilter(newFilterValues: FilterValues) {
        if (newFilterValues != filterValues) {
            filterValues = newFilterValues
            processCustomersList()
        }
    }

    fun setQueryString(newSearchQuery: String) {
        if (!newSearchQuery.equals(searchQuery, true)) {
            searchQuery = newSearchQuery
            processCustomersList()
        }
    }

    private fun processCustomersList() {
        var newFilteredList: List<Customer> = allCustomers
        var isSearchOrFilterResult = false

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

            if ((filterValues.byAgeChildren || filterValues.byAgeAdults) &&
                (filterValues.byAgeChildren != filterValues.byAgeAdults)) {
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

            isSearchOrFilterResult = true
        }

        // filter by search query
        if (searchQuery.isNotEmpty()) {
            newFilteredList = newFilteredList.filter { customer ->
                customer.name.contains(searchQuery, true)
            }

            isSearchOrFilterResult = true
        }

        customerListProcessorSubscriber.onCustomersProcessed(newFilteredList, isSearchOrFilterResult)
    }
}

interface CustomerListProcessorSubscriber {
    fun onCustomersProcessed(customers: List<Customer>, isSearchOrFilterResult: Boolean)
}