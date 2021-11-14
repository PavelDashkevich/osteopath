package by.dashkevichpavel.osteopath.features.settings.scheduler

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.SchedulerSettingsRepository
import by.dashkevichpavel.osteopath.model.WorkingDaySettings
import kotlinx.coroutines.launch

class SchedulerSettingsViewModel(
    private val schedulerSettingsRepository: SchedulerSettingsRepository
) : ViewModel() {
    val workingDaysSettings = MutableLiveData<List<WorkingDaySettings>>(listOf())
    val pauseAfterSession = MutableLiveData(0L)
    val sessionDurations = MutableLiveData<List<SessionDurationItem>>(listOf())

    fun loadSettings() {
        viewModelScope.launch {
            workingDaysSettings.postValue(schedulerSettingsRepository.getWorkingDaysSettings())
            pauseAfterSession.postValue(schedulerSettingsRepository.getPauseAfterSessionInMillis())
            sessionDurations.postValue(
                schedulerSettingsRepository.getSessionDurationsInMillis().map { timeInMillis: Long ->
                    SessionDurationItem(timeInMillis)
                }
            )
        }
    }

    fun setPauseAfterSession(timeInMillis: Long) {
        pauseAfterSession.value = timeInMillis
        schedulerSettingsRepository.savePauseAfterSession(timeInMillis)
    }

    fun getPauseAfterSession(): Long = pauseAfterSession.value ?: 0L

    fun addSessionDuration(durationInMillis: Long) {
        sessionDurations.value?.let { durations ->
            val newList = mutableListOf<SessionDurationItem>()
            newList.add(SessionDurationItem(durationInMillis))
            newList.addAll(durations)
            sessionDurations.value = newList
                .distinctBy { sessionDuration -> sessionDuration.duration }
                .sortedBy { sessionDuration -> sessionDuration.duration }
            saveSessionDurations()
        }
    }

    fun deleteSessionDuration(durationInMillis: Long) {
        sessionDurations.value?.let { durations ->
            sessionDurations.value = durations.filter { sessionDurationItem ->
                sessionDurationItem.duration != durationInMillis
            }
            saveSessionDurations()
        }
    }

    private fun saveSessionDurations() {
        sessionDurations.value?.let { durations ->
            schedulerSettingsRepository.saveSessionDurations(
                durations.map { sessionDurationItem -> sessionDurationItem.duration }
            )
        }
    }
}