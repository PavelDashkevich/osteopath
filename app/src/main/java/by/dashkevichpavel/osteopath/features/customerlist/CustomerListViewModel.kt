package by.dashkevichpavel.osteopath.features.customerlist

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.helpers.itemdeletion.DeletableInterface
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionEventsHandler
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepositorySingleton
import by.dashkevichpavel.osteopath.repositories.sharedprefs.CustomerFilterSharedPreferences
import kotlinx.coroutines.launch

class CustomerListViewModel(
    applicationContext: Context,
    private val repository: LocalDbRepository
) : ViewModel(),
    CustomerListProcessorSubscriber,
    CustomerListLoaderSubscriber {
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

    val customerDeletionHandler = ItemDeletionEventsHandler(this::onCustomerDeleteConfirmation)

    fun setFilter() {
        val filterValues = filterSharedPreferences.loadValues()
        customerListProcessor.setFilter(filterValues)
    }

    fun setQueryString(newSearchQuery: String) =
        customerListProcessor.setQueryString(newSearchQuery)

    fun getCustomerList(): List<Customer> = filteredCustomerList.value ?: listOf()

    fun startCustomerListObserving(applicationContext: Context) {
        repository.refreshDbInstance(applicationContext)
        customerListLoader.startCustomersTableObserving()
    }

    fun stopCustomerListObserving() = customerListLoader.stopCustomersTableObserving()

    fun putCustomerToArchive(customerId: Long) =
        customerListLoader.putCustomerInArchive(customerId)

    fun removeCustomerFromArchive(customerId: Long) =
        customerListLoader.removeCustomerFromArchive(customerId)

    private fun onCustomerDeleteConfirmation(itemId: Long, userAction: DialogUserAction) {
        when (userAction) {
            DialogUserAction.POSITIVE -> customerListLoader.deleteCustomer(itemId)
            DialogUserAction.NEUTRAL -> customerListLoader.putCustomerInArchive(itemId)
            else -> {}
        }
    }

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