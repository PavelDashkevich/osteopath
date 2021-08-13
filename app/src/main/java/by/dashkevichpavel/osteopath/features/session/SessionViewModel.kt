package by.dashkevichpavel.osteopath.features.session

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val addDisfunctionActionEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    private var allCustomerDisfunctions: MutableList<Disfunction> = mutableListOf()
    private var jobSave: Job? = null
    val saveChangesHelper = SaveChangesViewModelHelper(this)

    fun selectSession(sessionId: Long, customerId: Long) {
        if (sessionId == 0L) {
            setSession(Session(customerId = customerId))
        } else {
            viewModelScope.launch {
                onSessionLoaded(
                    repository.getSessionById(sessionId) ?: Session(customerId = customerId)
                )
            }
        }
        viewModelScope.launch {
            allCustomerDisfunctions =
                repository.getDisfunctionsByCustomerId(customerId) as MutableList<Disfunction>
            setAddDisfunctionActionAccessibility()
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
            sessionDateTime.value = sessionObject.dateTime
        }
    }

    fun setSessionTime(timeInMillis: Long) {
        session.value?.let { sessionObject ->
            sessionObject.dateTime.setTimePartFromTimeInMillis(timeInMillis)
            sessionDateTime.value = sessionObject.dateTime
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

    fun getSessionDefaultDateTimeInMillis(): Long {
        var dateTimeInMillis = session.value?.dateTime?.time ?: 0L

        if (dateTimeInMillis == 0L) {
            dateTimeInMillis = System.currentTimeMillis()
        }

        return dateTimeInMillis
    }

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

    private fun getDisfunctionsFromSession(): MutableList<Disfunction> {
        return session.value?.disfunctions ?: mutableListOf()
    }

    private fun onSessionLoaded(newSession: Session) {
        setSession(newSession)
        disfunctions.value = getDisfunctionsFromSession()
        sessionDateTime.value = newSession.dateTime
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
            dateTime = Date(newSession.dateTime.time)
        )
        initialSession.disfunctions.addAll(newSession.disfunctions)
    }

    override fun isDataModified(): Boolean = initialSession.isModified(session.value)

    override fun saveData() {
        session.value?.let { session ->
            if (jobSave == null || jobSave?.isCompleted != false) {
                jobSave = viewModelScope.launch {
                    saveChangesHelper.startSaving()
                    repository.insertSession(session)
                    saveChangesHelper.finishSaving()
                    saveChangesHelper.navigateUp()
                }
            }
        }

        if (session.value == null) saveChangesHelper.navigateUp()
    }
}