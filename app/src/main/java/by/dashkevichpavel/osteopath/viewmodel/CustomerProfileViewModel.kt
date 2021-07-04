package by.dashkevichpavel.osteopath.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.launch

class CustomerProfileViewModel(
    private val repository: OsteoDbRepository
) : ViewModel() {
    var customer = MutableLiveData<Customer?>(null)
    var customerName = MutableLiveData<String?>(null)

    var toolbarStateTitle: CharSequence = ""

    fun selectCustomer(customerId: Int) {
        if (customerId == 0) {
            customer.value = Customer()
            return
        }

        if (customer.value == null) {
            loadCustomerData(customerId)
        }
    }

    fun setCustomerName(newCustomerName: String) {
        customer.value?.let {
            it.name = newCustomerName
        }

        updateCustomerName()
    }

    private fun loadCustomerData(customerId: Int) {
        viewModelScope.launch {
            val loadedCustomer = repository.getCustomerById(customerId)

            loadedCustomer?.let {
                onCustomerDataLoaded(it)
            }
        }
    }

    private fun updateCustomerName() {
        customer.value?.let {
            customerName.value = it.name
        }
    }

    private fun onCustomerDataLoaded(loadedCustomer: Customer) {
        customer.value = loadedCustomer
        updateCustomerName()
    }
}