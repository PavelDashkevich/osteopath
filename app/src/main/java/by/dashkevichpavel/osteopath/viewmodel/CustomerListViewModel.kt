package by.dashkevichpavel.osteopath.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.CustomerListProcessor
import by.dashkevichpavel.osteopath.model.CustomerListProcessorSubscriber
import by.dashkevichpavel.osteopath.model.FilterValues
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity

class CustomerListViewModel(
    repository: OsteoDbRepository
) : ViewModel(), CustomerListProcessorSubscriber {
    // searchViewState* vars save state of SearchView on configuration change
    var searchViewStateIconified: Boolean = true
    var searchViewStateFocused: Boolean = false
    var searchViewStateKeyboardShown: Boolean = true
    var searchViewStateQueryString: CharSequence = ""

    var filteredCustomerList = MutableLiveData<List<CustomerEntity>>(mutableListOf())
    var isCustomersLoading = MutableLiveData(false)
    var isSearchOrFilterResult: Boolean = false

    private val model = CustomerListProcessor(this, repository, viewModelScope)

    fun setFilter(newFilterValues: FilterValues) {
        model.setFilter(newFilterValues)
    }

    fun setQueryString(newSearchQuery: String) {
        model.setQueryString(newSearchQuery)
    }

    override fun onCustomersProcessed(customers: List<CustomerEntity>, isSearchOrFilterResult: Boolean) {
        filteredCustomerList.value = customers
        this.isSearchOrFilterResult = isSearchOrFilterResult
    }
}