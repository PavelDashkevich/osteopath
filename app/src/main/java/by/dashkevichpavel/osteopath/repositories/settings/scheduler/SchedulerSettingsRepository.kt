package by.dashkevichpavel.osteopath.repositories.settings.scheduler

interface SchedulerSettingsRepository {
    fun getPauseAfterSessionInMillis(): Long
    fun savePauseAfterSession(millis: Long)

    fun getWorkingDaysSettings(): List<WorkingDaySettings>
    fun saveWorkingDaysSettings(settings: List<WorkingDaySettings>)

    fun getSessionDurationsInMillis(): List<Long>
    fun saveSessionDurations(durationsInMillis: List<Long>)
}