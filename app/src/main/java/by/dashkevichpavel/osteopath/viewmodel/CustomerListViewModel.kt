package by.dashkevichpavel.osteopath.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.CustomerListLoader
import by.dashkevichpavel.osteopath.model.CustomerListLoaderSubscriber
import by.dashkevichpavel.osteopath.model.CustomerStatus
import by.dashkevichpavel.osteopath.model.FilterValues
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class CustomerListViewModel(
    repository: OsteoDbRepository
) : ViewModel(){
    private val model = CustomerListLoader(repository)

    // searchViewState* vars save state of SearchView on configuration change
    var searchViewStateIconified: Boolean = true
    var searchViewStateFocused: Boolean = false
    var searchViewStateKeyboardShown: Boolean = true
    var searchViewStateQueryString: CharSequence = ""

    var jobFlow: Job? = null

    var filteredCustomerList = MutableLiveData<List<CustomerEntity>>(mutableListOf())
    var isCustomersLoading = MutableLiveData(false)

    private var filterValues = FilterValues()
    private var searchQuery: String = ""
    private var allCustomers: List<CustomerEntity> = emptyList()

    fun startCustomersTableChangeListening() {
        if (jobFlow == null) {
            //isCustomersLoading.value = true
            jobFlow = viewModelScope.launch {
                model.requestCustomersAsFlow().collect { listOfCustomers ->
                    allCustomers = listOfCustomers
                    updateFilteredCustomersList()
                }
            }
        }
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

                Log.d("OsteoApp", "updateFilteredCustomersList(): age18.time = ${age18.time}")
                Log.d("OsteoApp", "updateFilteredCustomersList(): newFilteredList.size = ${newFilteredList.size}")
                for (customer in newFilteredList) {
                    Log.d("OsteoApp", "updateFilteredCustomersList(): customer.birthDate.time = ${customer.birthDate.time}")
                }

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

        filteredCustomerList.value = newFilteredList
    }
}