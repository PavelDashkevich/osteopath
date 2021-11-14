package by.dashkevichpavel.osteopath.features.settings.scheduler.workingdays

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.SchedulerSettingsRepository
import by.dashkevichpavel.osteopath.model.WorkingDaySettings
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettingsTimeField
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WorkingDaysSettingsViewModel(
    private val schedulerSettingsRepository: SchedulerSettingsRepository
) : ViewModel() {
    val workingDaysSettings = MutableLiveData<List<WorkingDaySettings>>(mutableListOf())
    val needToNavigateUp = MutableLiveData(false)
    val needToShowTimePeriodError = MutableLiveData(false)
    private var jobSave: Job? = null

    fun loadSettings() {
        viewModelScope.launch {
            workingDaysSettings.postValue(schedulerSettingsRepository.getWorkingDaysSettings())
        }
    }

    fun setIsWorkingDay(dayOfWeek: Int, isChecked: Boolean) {
        workingDaysSettings.value?.getOrNull(dayOfWeek)?.let { daySettings ->
            daySettings.isWorkingDay = isChecked
            updateWorkingDaysSettings()
        }
    }

    fun setRestIncluded(dayOfWeek: Int, isChecked: Boolean) {
        workingDaysSettings.value?.getOrNull(dayOfWeek)?.let { daySettings ->
            daySettings.restIncluded = isChecked
            updateWorkingDaysSettings()
        }
    }

    fun setTimeForField(dayOfWeek: Int, timeInMillis: Long, timeField: WorkingDaySettingsTimeField) {
        workingDaysSettings.value?.getOrNull(dayOfWeek)?.let { daySettings ->
            when (timeField) {
                WorkingDaySettingsTimeField.DAY_START ->
                    daySettings.dayStartInMillis = validateTimeFieldAndGetCorrectValue(
                        timeInMillis,
                        daySettings.dayEndInMillis
                    )
                WorkingDaySettingsTimeField.DAY_END ->
                    daySettings.dayEndInMillis = validateTimeFieldAndGetCorrectValue(
                        daySettings.dayStartInMillis,
                        timeInMillis,
                        validateAsStartOfPeriod = false
                    )
                WorkingDaySettingsTimeField.REST_START ->
                    daySettings.restStartInMillis = validateTimeFieldAndGetCorrectValue(
                        timeInMillis,
                        daySettings.restEndInMillis
                    )
                WorkingDaySettingsTimeField.REST_END ->
                    daySettings.restEndInMillis = validateTimeFieldAndGetCorrectValue(
                        daySettings.restStartInMillis,
                        timeInMillis,
                        validateAsStartOfPeriod = false
                    )
            }
            updateWorkingDaysSettings()
        }
    }

    fun errorShown() {
        needToShowTimePeriodError.value = false
    }

    fun navigateUp() {
        if (jobSave != null) return

        jobSave = viewModelScope.launch {
            workingDaysSettings.value?.let { settings ->
                schedulerSettingsRepository.saveWorkingDaysSettings(settings)
            }
            needToNavigateUp.postValue(true)
        }
    }

    private fun updateWorkingDaysSettings() {
        workingDaysSettings.value = workingDaysSettings.value
    }

    private fun validateTimeFieldAndGetCorrectValue(
        timeStart: Long,
        timeEnd: Long,
        validateAsStartOfPeriod: Boolean = true
    ): Long {
        needToShowTimePeriodError.value = (timeStart > timeEnd)
        val getValidValue: (Long, Long) -> Long = if (validateAsStartOfPeriod) ::minOf else ::maxOf
        return getValidValue(timeStart, timeEnd)
    }
}