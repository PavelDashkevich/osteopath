package by.dashkevichpavel.osteopath.features.customerprofile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CustomerProfileViewModel(
    private val repository: OsteoDbRepository
) : ViewModel() {
    val customer = MutableLiveData<Customer?>(null)
    val disfunctions = MutableLiveData<MutableList<Disfunction>>(mutableListOf())
    val sessions = MutableLiveData<MutableList<Session>>(mutableListOf())
    val toolbarTitle = MutableLiveData<String?>(null)

    var jobDisfunctionsLoadingListener: Job? = null
    var jobSessionsLoadingListener: Job? = null

    fun selectCustomer(customerId: Long) {
        if (customerId == 0L) {
            customer.value = Customer()
            return
        }

        if (customer.value == null) {
            loadCustomerData(customerId)
        }
    }

    fun setCustomerName(name: String) {
        customer.value?.let { it.name = name }

        updateToolbarTitle()
    }

    fun setCustomerPhone(phone: String) {
        customer.value?.let { it.phone = phone }
    }

    fun setCustomerInstagram(userName: String) {
        customer.value?.let { it.instagram = userName }
    }

    fun setCustomerBirthDateInMillis(birthDateInMillis: Long) {
        customer.value?.let { it.birthDate.time = birthDateInMillis }
    }

    fun startListeningForDisfunctionsChanges() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        customer.value?.let { customerObject ->
            if (jobDisfunctionsLoadingListener == null) {
                jobDisfunctionsLoadingListener = viewModelScope.launch {
                    repository
                        .getAllDisfunctionsByCustomerId(customerObject.id)
                        .collect { disfunctions ->
                            onDisfunctionsListLoaded(disfunctions)
                    }
                }
            }
        }
    }

    fun startListeningForSessionsChanges() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        customer.value?.let { customerObject ->
            if (jobSessionsLoadingListener == null) {
                jobSessionsLoadingListener = viewModelScope.launch {
                    repository
                        .getSessionsWithDisfunctionsByCustomerId(customerObject.id)
                        .collect { sessions ->
                            onSessionsListLoaded(sessions)
                        }
                }
            }
        }
    }

    private fun loadCustomerData(customerId: Long) {
        viewModelScope.launch {
            val loadedCustomer = repository.getCustomerById(customerId)

            loadedCustomer?.let {
                onCustomerDataLoaded(it)
            }
        }
    }

    private fun updateToolbarTitle() {
        customer.value?.let {
            toolbarTitle.value = it.name
        }
    }

    private fun onDisfunctionsListLoaded(newDisfunctions: List<Disfunction>) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        val listOfDisfunctions = newDisfunctions as MutableList<Disfunction>
        disfunctions.value = listOfDisfunctions
        customer.value?.disfunctions = listOfDisfunctions
    }

    private fun onSessionsListLoaded(newSessions: List<Session>) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        val listOfSessions = newSessions as MutableList<Session>
        sessions.value = listOfSessions
        customer.value?.sessions = listOfSessions
    }

    private fun onCustomerDataLoaded(loadedCustomer: Customer) {
        customer.value = loadedCustomer
        updateToolbarTitle()
    }
}