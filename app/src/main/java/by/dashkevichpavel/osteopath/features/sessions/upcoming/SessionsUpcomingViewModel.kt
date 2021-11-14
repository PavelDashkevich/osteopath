package by.dashkevichpavel.osteopath.features.sessions.upcoming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeInterval
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeIntervalItemSession
import by.dashkevichpavel.osteopath.helpers.jobs.FlowJobController
import by.dashkevichpavel.osteopath.model.SessionAndCustomer
import by.dashkevichpavel.osteopath.repositories.data.LocalDbRepository
import kotlinx.coroutines.flow.collect
import java.util.*

class SessionsUpcomingViewModel(
    private val repository: LocalDbRepository
) : ViewModel() {
    val sessions = MutableLiveData<List<TimeIntervalItemSession>>(listOf())
    private val flowJobController = FlowJobController(
        viewModelScope,
        suspend {
            repository.getUpcomingSessions(Date().time).collect { listOfSessions ->
                sessions.value = listOfSessions.map { sessionAndCustomer ->
                    TimeIntervalItemSession(TimeInterval.SessionTime(sessionAndCustomer))
                }
            }
        }
    )

    fun startSessionsTableObserving() = flowJobController.start()
    fun stopSessionsTableObserving() = flowJobController.stop()
}