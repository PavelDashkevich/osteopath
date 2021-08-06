package by.dashkevichpavel.osteopath.features.customerlist

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import by.dashkevichpavel.osteopath.repositories.sharedprefs.CustomerFilterSharedPreferences

class CustomerListViewModel(
    repository: LocalDbRepository
) : ViewModel(), CustomerListProcessorSubscriber, CustomerListLoaderSubscriber {
    // searchViewState* vars save state of SearchView on configuration change
    var searchViewStateIconified: Boolean = true
    var searchViewStateKeyboardShown: Boolean = true
    var searchViewStateQueryString: CharSequence = ""

    private var filterSharedPreferences: CustomerFilterSharedPreferences? = null

    private var filteredCustomerList = MutableLiveData<List<Customer>>(listOf())
    private var isCustomersLoading = MutableLiveData(false)
    var isSearchOrFilterResult: Boolean = false
        private set

    private val customerListProcessor = CustomerListProcessor(this)
    private val customerListLoader = CustomerListLoader(
        this,
        repository,
        viewModelScope
    )

    fun init(context: Context) {
        filterSharedPreferences = CustomerFilterSharedPreferences(context)
        setFilter()
    }

    fun observeLoadingProgressChanges(owner: LifecycleOwner, onChanged: (Boolean) -> Unit) {
        isCustomersLoading.observe(owner, onChanged)
    }

    fun observeCustomerListChanges(owner: LifecycleOwner, onChanged: (List<Customer>) -> Unit) {
        filteredCustomerList.observe(owner, onChanged)
    }

    private fun setFilter() {
        val filterValues = filterSharedPreferences?.loadValues() ?: FilterValues()
        customerListProcessor.setFilter(filterValues)
    }

    fun setQueryString(newSearchQuery: String) {
        customerListProcessor.setQueryString(newSearchQuery)
    }

    fun loadTestData() {
        customerListLoader.loadTestData()
    }

    fun getCustomerList(): List<Customer> = filteredCustomerList.value ?: listOf()

    override fun onCustomersProcessed(customers: List<Customer>, isSearchOrFilterResult: Boolean) {
        filteredCustomerList.value = customers
        this.isSearchOrFilterResult = isSearchOrFilterResult
    }

    override fun onCustomersLoaded(customers: List<Customer>) {
        customerListProcessor.setList(customers)
    }

    override fun onCleared() {
        filterSharedPreferences = null
        super.onCleared()
    }
}