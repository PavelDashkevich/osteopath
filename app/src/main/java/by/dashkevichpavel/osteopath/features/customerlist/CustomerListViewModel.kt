package by.dashkevichpavel.osteopath.features.customerlist

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import by.dashkevichpavel.osteopath.repositories.sharedprefs.CustomerFilterSharedPreferences

class CustomerListViewModel(
    applicationContext: Context,
    repository: LocalDbRepository
) : ViewModel(), CustomerListProcessorSubscriber, CustomerListLoaderSubscriber {
    // searchViewState* vars save state of SearchView on configuration change
    var searchViewStateIconified: Boolean = true
    var searchViewStateKeyboardShown: Boolean = true
    var searchViewStateQueryString: CharSequence = ""

    private var filterSharedPreferences: CustomerFilterSharedPreferences =
        CustomerFilterSharedPreferences(applicationContext)

    var filteredCustomerList = MutableLiveData<List<Customer>>(listOf())
    var isCustomersLoading = MutableLiveData(false)
    var isSearchOrFilterResult: Boolean = false
        private set

    private val customerListProcessor = CustomerListProcessor(this)
    private val customerListLoader = CustomerListLoader(
        this,
        repository,
        viewModelScope
    )

    fun setFilter() {
        val filterValues = filterSharedPreferences.loadValues()
        customerListProcessor.setFilter(filterValues)
    }

    fun setQueryString(newSearchQuery: String) {
        customerListProcessor.setQueryString(newSearchQuery)
    }

    fun loadTestData() {
        customerListLoader.loadTestData()
    }

    fun getCustomerList(): List<Customer> = filteredCustomerList.value ?: listOf()

    fun startCustomerListObserving() = customerListLoader.startCustomersTableObserving()

    fun stopCustomerListObserving() = customerListLoader.stopCustomersTableObserving()

    override fun onCustomersProcessed(customers: List<Customer>, isSearchOrFilterResult: Boolean) {
        filteredCustomerList.value = customers
        this.isSearchOrFilterResult = isSearchOrFilterResult
    }

    override fun onCustomersLoaded(customers: List<Customer>) {
        customerListProcessor.setList(customers)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("OsteoApp", "CustomerListViewModel: onCleared()")
    }
}