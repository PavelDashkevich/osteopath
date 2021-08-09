package by.dashkevichpavel.osteopath.features.customerprofile

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.helpers.contacts.ContactInfoLoader
import by.dashkevichpavel.osteopath.helpers.savechanges.SavableInterface
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesViewModelHelper
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CustomerProfileViewModel(
    private val repository: LocalDbRepository
) : ViewModel(), SavableInterface {
    val customer = MutableLiveData<Customer?>(null)
    private var initialCustomer: Customer = Customer()
    val disfunctions = MutableLiveData<MutableList<Disfunction>>(mutableListOf())
    val sessions = MutableLiveData<MutableList<Session>>(mutableListOf())
    val toolbarTitle = MutableLiveData<String?>(null)
    val saveChangesHelper = SaveChangesViewModelHelper(this)
    private var jobSave: Job? = null

    var jobDisfunctionsLoadingListener: Job? = null
    var jobSessionsLoadingListener: Job? = null

    fun selectCustomer(customerId: Long) {
        if (customerId == 0L) {
            setCustomer(Customer())
            return
        }

        if (customer.value == null) {
            loadCustomerData(customerId)
        }
    }

    fun setCustomerName(name: String) {
        customer.value?.name = name

        updateToolbarTitle()
    }

    fun setCustomerPhone(phone: String) {
        customer.value?.phone = phone
    }

    fun setCustomerInstagram(userName: String) {
        customer.value?.instagram = userName
    }

    fun setCustomerBirthDateInMillis(birthDateInMillis: Long) {
        customer.value?.birthDate?.time = birthDateInMillis
    }

    fun startListeningForDisfunctionsChanges() {
        customer.value?.let { customerObject ->
            if (customerObject.id == 0L) return@let

            if (jobDisfunctionsLoadingListener == null) {
                jobDisfunctionsLoadingListener = viewModelScope.launch {
                    repository
                        .getAllDisfunctionsByCustomerIdAsFlow(customerObject.id)
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
            if (customerObject.id == 0L) return@let

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

    fun swipe(position: Int) {
        if (customer.value?.id == 0L && position != 0) {
            saveCustomer(false)
        }
    }

    fun extractContactData(contentResolver: ContentResolver, contactUri: Uri?) {
        contactUri?.let { uri ->
            viewModelScope.launch {
                val contactInfoLoader = ContactInfoLoader(contentResolver)
                val contactInfo = contactInfoLoader.getContactInfo(uri)

                customer.value?.let { cust ->
                    customer.value = cust.copy(
                        name = if (contactInfo.name.isNotBlank())
                                    contactInfo.name
                                else
                                    cust.name,
                        phone = if (contactInfo.phones.isNotEmpty())
                                    contactInfo.phones[0]
                                else
                                    cust.phone
                    )
                }
            }
        }
    }

    private fun loadCustomerData(customerId: Long) {
        viewModelScope.launch {
            val loadedCustomer = repository.getCustomerById(customerId)

            loadedCustomer?.let { newCustomer ->
                onCustomerDataLoaded(newCustomer)
            }
        }
    }

    private fun updateToolbarTitle() {
        customer.value?.let {
            toolbarTitle.value = it.name
        }
    }

    private fun onDisfunctionsListLoaded(newDisfunctions: List<Disfunction>) {
        val listOfDisfunctions = newDisfunctions as MutableList<Disfunction>
        disfunctions.value = listOfDisfunctions
        customer.value?.disfunctions = listOfDisfunctions
    }

    private fun onSessionsListLoaded(newSessions: List<Session>) {
        val listOfSessions = newSessions as MutableList<Session>
        sessions.value = listOfSessions
        customer.value?.sessions = listOfSessions
    }

    private fun onCustomerDataLoaded(loadedCustomer: Customer) {
        setCustomer(loadedCustomer)
        updateToolbarTitle()
    }

    private fun setCustomer(newCustomer: Customer) {
        customer.value = newCustomer
        initialCustomer = newCustomer.copy()
    }

    override fun isDataModified(): Boolean = initialCustomer.isModified(customer.value)

    override fun saveData() {
        saveCustomer(true)
    }

    private fun saveCustomer(navigateUp: Boolean) {
        customer.value?.let { cust ->
            if (jobSave == null || jobSave?.isCompleted != false) {
                jobSave = viewModelScope.launch {
                    saveChangesHelper.startSaving()
                    cust.id = repository.insertCustomer(cust)
                    startListeningForDisfunctionsChanges()
                    startListeningForSessionsChanges()
                    saveChangesHelper.finishSaving()
                    if (navigateUp) saveChangesHelper.navigateUp()
                }
            }
        }

        if (customer.value == null && navigateUp) saveChangesHelper.navigateUp()
    }
}