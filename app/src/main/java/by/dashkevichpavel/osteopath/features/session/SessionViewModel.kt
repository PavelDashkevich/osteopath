package by.dashkevichpavel.osteopath.features.session

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.model.setDatePartFromTimeInMillis
import by.dashkevichpavel.osteopath.model.setTimePartFromTimeInMillis
import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepository
import kotlinx.coroutines.launch
import java.util.*

class SessionViewModel(
    private val repository: OsteoDbRepository
) : ViewModel() {
    val session: MutableLiveData<Session> = MutableLiveData(Session())
    val disfunctions: MutableLiveData<MutableList<Disfunction>> = MutableLiveData(getDisfunctionsFromSession())
    val sessionDateTime: MutableLiveData<Date> = MutableLiveData(session.value?.dateTime)

    fun setSession(sessionId: Long, customerId: Long) {
        if (sessionId == 0L) {
            session.value = Session(customerId = customerId)
        } else {
            viewModelScope.launch {
                onSessionLoaded(
                    repository.getSessionById(sessionId) ?: Session(customerId = customerId)
                )
            }
        }
    }

    fun deleteDisfunction(disfunctionId: Long) {
        session.value?.let { sessionObject ->
            sessionObject.disfunctions.removeAll { disfunction ->
                disfunction.id == disfunctionId
            }
            disfunctions.value = getDisfunctionsFromSession()
        }
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

    private fun getDisfunctionsFromSession(): MutableList<Disfunction> {
        return session.value?.disfunctions ?: mutableListOf()
    }

    private fun onSessionLoaded(newSession: Session) {
        session.value = newSession
        disfunctions.value = getDisfunctionsFromSession()
        sessionDateTime.value = newSession.dateTime
    }
}