package by.dashkevichpavel.osteopath.features.session

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionEventsHandler
import by.dashkevichpavel.osteopath.helpers.savechanges.SavableInterface
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesViewModelHelper
import by.dashkevichpavel.osteopath.helpers.setDatePartFromTimeInMillis
import by.dashkevichpavel.osteopath.helpers.setTimePartFromTimeInMillis
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class SessionViewModel(
    private val repository: LocalDbRepository
) : ViewModel(), SavableInterface {
    val session: MutableLiveData<Session> = MutableLiveData(Session())
    private var initialSession: Session = Session()
    val disfunctions: MutableLiveData<MutableList<Disfunction>> =
        MutableLiveData(getDisfunctionsFromSession())
    val sessionDateTime: MutableLiveData<Date> = MutableLiveData(session.value?.dateTime)
    val sessionDateTimeEnd: MutableLiveData<Date> = MutableLiveData(session.value?.dateTimeEnd)
    val addDisfunctionActionEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    private var allCustomerDisfunctions: MutableList<Disfunction> = mutableListOf()
    private var jobSave: Job? = null
    val currentSessionId = MutableLiveData(0L)
    val saveChangesHelper = SaveChangesViewModelHelper(this)
    val sessionDeletionHandler = ItemDeletionEventsHandler(this::onSessionDeleteConfirmation)
    val customerName = MutableLiveData("")

    fun selectSession(sessionId: Long, customerId: Long) {
        if (sessionId == 0L) {
            setSession(Session(customerId = customerId))
        }

        viewModelScope.launch {
            if (sessionId != 0L) {
                onSessionLoaded(
                    repository.getSessionById(sessionId) ?: Session(customerId = customerId)
                )
            }

            allCustomerDisfunctions =
                repository.getDisfunctionsByCustomerId(customerId) as MutableList<Disfunction>
            setAddDisfunctionActionAccessibility()

            loadCustomerName(customerId)
        }
    }

    fun deleteDisfunction(disfunctionId: Long) {
        session.value?.let { sessionObject ->
            sessionObject.disfunctions.removeAll { disfunction ->
                disfunction.id == disfunctionId
            }
            disfunctions.value = getDisfunctionsFromSession()
        }
        setAddDisfunctionActionAccessibility()
    }

    fun setSessionDate(timeInMillis: Long) {
        session.value?.let { sessionObject ->
            sessionObject.dateTime.setDatePartFromTimeInMillis(timeInMillis)
            sessionObject.dateTimeEnd.setDatePartFromTimeInMillis(timeInMillis)
            sessionDateTime.value = sessionObject.dateTime
            sessionDateTimeEnd.value = sessionObject.dateTimeEnd
        }
    }

    fun setSessionTime(timeInMillis: Long) {
        session.value?.let { sessionObject ->
            sessionObject.dateTime.setTimePartFromTimeInMillis(timeInMillis)
            sessionDateTime.value = sessionObject.dateTime
        }
    }

    fun setSessionTimeEnd(timeInMillis: Long) {
        session.value?.let { sessionObject ->
            sessionObject.dateTimeEnd.time = sessionObject.dateTime.time
            sessionObject.dateTimeEnd.setTimePartFromTimeInMillis(timeInMillis)
            sessionDateTimeEnd.value = sessionObject.dateTimeEnd
        }
    }

    fun setPlan(plan: String) {
        session.value?.plan = plan
    }

    fun setBodyCondition(bodyCondition: String) {
        session.value?.bodyCondition = bodyCondition
    }

    fun setIsDone(isDone: Boolean) {
        session.value?.isDone = isDone
    }

    fun getSessionDefaultDateTimeInMillis(): Long =
        session.value?.dateTime?.time ?: System.currentTimeMillis()

    fun getSessionDefaultDateTimeEndInMillis(): Long =
        session.value?.dateTimeEnd?.time ?: System.currentTimeMillis()

    fun getCustomerId(): Long = session.value?.customerId ?: 0L

    fun getSelectedDisfunctionsIds(): List<Long> =
        disfunctions.value?.map { disfunction -> disfunction.id } ?: listOf()

    fun setSelectedDisfunctionsIds(newSelectedIds: List<Long>) {
        val mapOfDisfunctionsByIds: Map<Long, Disfunction> =
            allCustomerDisfunctions.associateBy { disfunction -> disfunction.id }
        newSelectedIds.forEach { id ->
            mapOfDisfunctionsByIds[id]?.let { disfunction ->
                disfunctions.value?.let { items ->
                    items.add(disfunction)
                    session.value?.disfunctions = items
                }
            }
        }
        setAddDisfunctionActionAccessibility()
    }

    fun setCustomer(customerId: Long) {
        if (session.value?.id == 0L && session.value?.customerId != customerId) {
            session.value?.customerId = customerId
            disfunctions.value = mutableListOf()
            setAddDisfunctionActionAccessibility()
            initialSession.customerId = customerId

            viewModelScope.launch { loadCustomerName(customerId) }
        }
    }

    private suspend fun loadCustomerName(customerId: Long) {
        val customer = repository.getCustomerById(customerId)
        customer?.let {
            customerName.value = customer.name
        }
    }

    private fun getDisfunctionsFromSession(): MutableList<Disfunction> {
        return session.value?.disfunctions ?: mutableListOf()
    }

    private fun onSessionLoaded(newSession: Session) {
        setSession(newSession)
        disfunctions.value = getDisfunctionsFromSession()
        sessionDateTime.value = newSession.dateTime
        sessionDateTimeEnd.value = newSession.dateTimeEnd
        setAddDisfunctionActionAccessibility()
    }

    private fun setAddDisfunctionActionAccessibility() {
        addDisfunctionActionEnabled.value =
            (allCustomerDisfunctions.size == disfunctions.value?.size ?: 0)
    }

    private fun setSession(newSession: Session) {
        session.value = newSession
        initialSession = newSession.copy(
            disfunctions = mutableListOf(),
            dateTime = Date(newSession.dateTime.time),
            dateTimeEnd = Date(newSession.dateTimeEnd.time),
        )
        initialSession.disfunctions.addAll(newSession.disfunctions)
        updateSessionId()
    }

    fun getSessionId(): Long = currentSessionId.value ?: 0L

    private fun updateSessionId() {
        currentSessionId.value = session.value?.id ?: 0L
    }

    private fun onSessionDeleteConfirmation(sessionId: Long, userAction: DialogUserAction) {
        if (userAction == DialogUserAction.POSITIVE) {
            viewModelScope.launch {
                repository.deleteSessionById(sessionId)
            }
        }
    }

    override fun isDataModified(): Boolean = initialSession.isModified(session.value)

    override fun saveData() {
        session.value?.let { session ->
            if (jobSave == null || jobSave?.isCompleted != false) {
                jobSave = viewModelScope.launch {
                    saveChangesHelper.startSaving()
                    session.id = repository.insertSession(session)
                    saveChangesHelper.finishSaving()
                    updateSessionId()
                    saveChangesHelper.navigateUp()
                }
            }
        }

        if (session.value == null) saveChangesHelper.navigateUp()
    }
}