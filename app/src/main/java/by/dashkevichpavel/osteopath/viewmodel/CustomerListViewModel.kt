package by.dashkevichpavel.osteopath.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.dashkevichpavel.osteopath.model.CustomerListLoader
import by.dashkevichpavel.osteopath.model.CustomerListLoaderSubscriber
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity

class CustomerListViewModel(
    repository: OsteoDbRepository
) : ViewModel(), CustomerListLoaderSubscriber {
    private val model = CustomerListLoader(this, repository)

    var customerList = MutableLiveData<List<CustomerEntity>>(mutableListOf())
    var isCustomersLoading = MutableLiveData(false)

    fun loadCustomers() {
        isCustomersLoading.value = true
        model.requestCustomers()
    }

    override fun onCustomersLoaded(customers: List<CustomerEntity>) {
        isCustomersLoading.value = false
        customerList.value = customers
    }
}