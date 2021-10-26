package by.dashkevichpavel.osteopath.features.customerprofile

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.helpers.contacts.ContactInfoLoader
import by.dashkevichpavel.osteopath.helpers.savechanges.SavableInterface
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesViewModelHelper
import by.dashkevichpavel.osteopath.helpers.thumbnails.ThumbnailHelper
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import java.io.File

class CustomerProfileViewModel(
    applicationContext: Context,
    private val repository: LocalDbRepository
) : ViewModel(), SavableInterface {
    val customer = MutableLiveData<Customer?>(null)
    private var initialCustomer: Customer = Customer()
    val disfunctions = MutableLiveData<MutableList<Disfunction>>(mutableListOf())
    val sessions = MutableLiveData<MutableList<Session>>(mutableListOf())
    val attachments = MutableLiveData<MutableList<Attachment>>(mutableListOf())
    val toolbarTitle = MutableLiveData<String?>(null)
    val saveChangesHelper = SaveChangesViewModelHelper(this)
    val currentCustomerId = MutableLiveData(0L)
    val currentCustomerIsArchived = MutableLiveData(false)
    private var jobSave: Job? = null

    var jobDisfunctionsLoadingListener: Job? = null
    var jobSessionsLoadingListener: Job? = null
    private var jobAttachmentsLoadingListener: Job? = null
    private var jobThumbnailsCreating: Job? = null

    // attachments
    private val checkedThumbnails = mutableMapOf<Long, Attachment>()
    private val thumbnailsToCreate = Channel<Attachment>(Channel.UNLIMITED)
    private val contentResolver = applicationContext.contentResolver
    private val thumbnailHelper = ThumbnailHelper(applicationContext)
    private val applicationContext = applicationContext

    // region general
    fun selectCustomer(customerId: Long) {
        if (customerId == 0L) {
            setCustomer(Customer())
            return
        }

        if (customer.value == null) {
            loadCustomerData(customerId)
        }
    }

    fun swipe(position: Int) {
        if (position != 0) {
            saveIfCustomerIsNew()
        }
    }

    private fun saveIfCustomerIsNew() {
        if (customer.value?.id == 0L) {
            saveCustomer(false)
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

    private fun onCustomerDataLoaded(loadedCustomer: Customer) {
        setCustomer(loadedCustomer)
        updateToolbarTitle()
    }

    private fun setCustomer(newCustomer: Customer) {
        customer.value = newCustomer
        initialCustomer = newCustomer.copy()
        updateCustomerId()
        updateCustomerIsArchived()
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
                    startListeningForAttachmentsChanges()
                    saveChangesHelper.finishSaving()
                    updateCustomerId()
                    if (navigateUp) saveChangesHelper.navigateUp()
                }
            }
        }

        if (customer.value == null && navigateUp) saveChangesHelper.navigateUp()
    }

    fun getCustomerName(): String = customer.value?.name ?: ""

    fun getCustomerId(): Long = currentCustomerId.value ?: 0L

    fun deleteCustomer(customerId: Long) {
        if (customerId == 0L) return

        viewModelScope.launch {
            repository.deleteCustomerById(customerId)
        }
    }
    // endregion general code

    // region Contacts tab
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

    fun setCustomerIsArchived(isArchived: Boolean) {
        customer.value?.isArchived = isArchived
        updateCustomerIsArchived()
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

    private fun updateToolbarTitle() {
        customer.value?.let {
            toolbarTitle.value = it.name
        }
    }

    private fun updateCustomerId() {
        currentCustomerId.value = customer.value?.id ?: 0L
    }

    private fun updateCustomerIsArchived() {
        currentCustomerIsArchived.value = customer.value?.isArchived ?: false
    }
    // endregion Contacts tab

    // region Disfunctions tab
    fun startListeningForDisfunctionsChanges() {
        customer.value?.let { customerObject ->
            if (customerObject.id == 0L) return@let

            if (jobDisfunctionsLoadingListener == null) {
                jobDisfunctionsLoadingListener = viewModelScope.launch {
                    repository
                        .getAllDisfunctionsByCustomerIdAsFlow(customerObject.id)
                        .collect { disfunctions -> onDisfunctionsListLoaded(disfunctions) }
                }
            }
        }
    }

    private fun onDisfunctionsListLoaded(newDisfunctions: List<Disfunction>) {
        val listOfDisfunctions = newDisfunctions.toMutableList()
        disfunctions.value = listOfDisfunctions
        customer.value?.disfunctions = listOfDisfunctions
    }

    fun changeDisfunctionStatus(disfunctionId: Long, newStatusId: Int) {
        viewModelScope.launch {
            repository.updateDisfunctionStatusById(disfunctionId, newStatusId)
        }
    }

    fun deleteDisfunction(disfunctionId: Long) {
        viewModelScope.launch {
            repository.deleteDisfunctionById(disfunctionId)
        }
    }
    // endregion Disfunctions tab

    // region Sessions tab
    fun startListeningForSessionsChanges() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        customer.value?.let { customerObject ->
            if (customerObject.id == 0L) return@let

            if (jobSessionsLoadingListener == null) {
                jobSessionsLoadingListener = viewModelScope.launch {
                    repository
                        .getSessionsWithDisfunctionsByCustomerIdAsFlow(customerObject.id)
                        .collect { sessions -> onSessionsListLoaded(sessions) }
                }
            }
        }
    }

    private fun onSessionsListLoaded(newSessions: List<Session>) {
        val listOfSessions = newSessions.toMutableList()
        sessions.value = listOfSessions
        customer.value?.sessions = listOfSessions
    }
    // endregion Sessions tab

    // region Attachments tab
    fun startListeningForAttachmentsChanges() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        customer.value?.let { customerObject ->
            if (customerObject.id == 0L) return@let

            if (jobAttachmentsLoadingListener == null) {
                jobAttachmentsLoadingListener = viewModelScope.launch {
                    repository
                        .getAttachmentsByCustomerIdAsFlow(customerObject.id)
                        .collect { attachments ->
                            checkPersistablePermissions(attachments)
                            checkThumbnails(attachments)
                            createThumbnails()
                            onAttachmentsListLoaded(attachments)
                        }
                }
            }
        }
    }

    private fun checkPersistablePermissions(newAttachments: List<Attachment>) {
        newAttachments.forEach { attachment ->
            val permission = applicationContext.checkCallingOrSelfUriPermission(
                Uri.parse(attachment.thumbnail),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d("OsteoApp", "checkCallingOrSelfUriPermission for ${attachment.path}")
            Log.d(
                "OsteoApp",
                if (permission == PackageManager.PERMISSION_GRANTED)
                    "granted"
                else
                    "denied"
            )
//            contentResolver.takePersistableUriPermission(
//                Uri.parse(attachment.path),
//                Intent.FLAG_GRANT_READ_URI_PERMISSION
//            )

            if (attachment.thumbnail.isNotEmpty()) {

            }
        }
    }

    private suspend fun checkThumbnails(newAttachments: List<Attachment>) = withContext(Dispatchers.IO){
        newAttachments.forEach { attachment ->
            if (!checkedThumbnails.keys.contains(attachment.id)) {
                checkedThumbnails[attachment.id] = attachment
                if (attachment.mimeType == "application/pdf" &&
                    (attachment.thumbnail.isEmpty() || !File(attachment.thumbnail).exists())) {
                    thumbnailsToCreate.send(attachment)
                }
            }
        }
    }

    private fun createThumbnails() {
        if (jobThumbnailsCreating?.isActive == true) return

        jobThumbnailsCreating = (viewModelScope + Dispatchers.IO).launch {
            while(!thumbnailsToCreate.isEmpty) {
                val attachment = thumbnailsToCreate.receive()
                val oldThumbnailPath = attachment.thumbnail
                attachment.thumbnail = thumbnailHelper.getPdfThumbnailUriPath(attachment)
                if (attachment.thumbnail != oldThumbnailPath) {
                    repository.updateAttachment(attachment)
                }
            }
        }
    }

    private fun onAttachmentsListLoaded(newAttachments: List<Attachment>) {
        val listOfAttachments = newAttachments.toMutableList()
        attachments.value = listOfAttachments
        customer.value?.attachments = listOfAttachments
    }

    fun addAttachment(fileUri: Uri) {
        viewModelScope.launch {
            if (customer.value?.id == 0L) {
                saveCustomer(false)
                jobSave?.join()
            }

            val customerId = customer.value?.id ?: 0L

            if (customerId == 0L) {
                Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: $fileUri not added to database, customerId = 0L")
            } else {
                repository.insertAttachment(
                    Attachment(
                        customerId = customerId,
                        path = fileUri.toString(),
                        mimeType = contentResolver.getType(fileUri) ?: ""
                    )
                )
            }
        }
    }
    // endregion Attachments tab
}