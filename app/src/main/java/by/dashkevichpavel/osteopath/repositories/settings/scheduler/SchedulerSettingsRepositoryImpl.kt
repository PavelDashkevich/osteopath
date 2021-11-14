package by.dashkevichpavel.osteopath.repositories.settings.scheduler

import android.content.Context
import android.content.SharedPreferences
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil
import by.dashkevichpavel.osteopath.model.WorkingDaySettings

class SchedulerSettingsRepositoryImpl(context: Context) : SchedulerSettingsRepository {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun getPauseAfterSessionInMillis(): Long =
        sharedPrefs.getLong(
            KEY_PAUSE_AFTER_SESSION_IN_MILLIS,
            DateTimeUtil.getDurationInMillis(0, DEFAULT_PAUSE_AFTER_SESSION)
        )

    override fun savePauseAfterSession(millis: Long) =
        sharedPrefs.edit().putLong(KEY_PAUSE_AFTER_SESSION_IN_MILLIS, millis).apply()

    override fun getWorkingDaysSettings(): List<WorkingDaySettings> {
        val listOfSettings: MutableList<WorkingDaySettings> = mutableListOf()

        for (dayOfWeek in 0..6) {
            listOfSettings.add(getWorkingDaysSettingsByDay(dayOfWeek))
        }

        return listOfSettings
    }

    override fun saveWorkingDaysSettings(settings: List<WorkingDaySettings>) {
        settings.forEach { daySettings -> saveWorkingDaysSettingsByDay(daySettings) }
    }

    override fun getSessionDurationsInMillis(): List<Long> {
        val durationsInMillis: MutableList<Long> = mutableListOf()
        val defaultSessionDuration = DateTimeUtil.getDurationInMillis(DEFAULT_SESSION_DURATION)
        val defaultSetOfDurations = setOf(defaultSessionDuration.toString())
        var savedSetOfDurations =
            sharedPrefs.getStringSet(KEY_SESSION_DURATIONS, defaultSetOfDurations)
            ?: defaultSetOfDurations

        if (savedSetOfDurations.isEmpty()) {
            savedSetOfDurations = defaultSetOfDurations
        }

        savedSetOfDurations.forEach { sessionDuration: String ->
                durationsInMillis.add(sessionDuration.toLongOrNull() ?: defaultSessionDuration)
            }

        return durationsInMillis.sorted()
    }

    override fun saveSessionDurations(durationsInMillis: List<Long>) {
        sharedPrefs
            .edit()
            .putStringSet(
                KEY_SESSION_DURATIONS,
                durationsInMillis
                    .distinct()
                    .sorted()
                    .map { durationInMillis: Long -> durationInMillis.toString() }
                    .toSet()
            )
            .apply()
    }

    private fun getWorkingDaysSettingsByDay(dayOfWeek: Int): WorkingDaySettings =
        WorkingDaySettings(
            dayOfWeek = dayOfWeek,
            isWorkingDay = sharedPrefs.getBoolean(
                getKeyName(KEY_DAY_SETTINGS_IS_WORKING, dayOfWeek),
                dayOfWeek in 0..4
            ),
            dayStartInMillis = sharedPrefs.getLong(
                getKeyName(KEY_DAY_SETTINGS_DAY_START_IN_MILLIS, dayOfWeek),
                DateTimeUtil.getDurationInMillis(DEFAULT_WORKING_DAY_START)
            ),
            dayEndInMillis = sharedPrefs.getLong(
                getKeyName(KEY_DAY_SETTINGS_DAY_END_IN_MILLIS, dayOfWeek),
                DateTimeUtil.getDurationInMillis(DEFAULT_WORKING_DAY_END)
            ),
            restIncluded = sharedPrefs.getBoolean(
                getKeyName(KEY_DAY_SETTINGS_REST_INCLUDED, dayOfWeek),
                true
            ),
            restStartInMillis = sharedPrefs.getLong(
                getKeyName(KEY_DAY_SETTINGS_REST_START_IN_MILLIS, dayOfWeek),
                DateTimeUtil.getDurationInMillis(DEFAULT_WORKING_DAY_REST_START)
            ),
            restEndInMillis = sharedPrefs.getLong(
                getKeyName(KEY_DAY_SETTINGS_REST_END_IN_MILLIS, dayOfWeek),
                DateTimeUtil.getDurationInMillis(DEFAULT_WORKING_DAY_REST_END)
            )
        )

    private fun saveWorkingDaysSettingsByDay(daySettings: WorkingDaySettings) {
        sharedPrefs
            .edit()
            .putBoolean(
                getKeyName(KEY_DAY_SETTINGS_IS_WORKING, daySettings.dayOfWeek),
                daySettings.isWorkingDay
            )
            .putLong(
                getKeyName(KEY_DAY_SETTINGS_DAY_START_IN_MILLIS, daySettings.dayOfWeek),
                daySettings.dayStartInMillis
            )
            .putLong(
                getKeyName(KEY_DAY_SETTINGS_DAY_END_IN_MILLIS, daySettings.dayOfWeek),
                daySettings.dayEndInMillis
            )
            .putBoolean(
                getKeyName(KEY_DAY_SETTINGS_REST_INCLUDED, daySettings.dayOfWeek),
                daySettings.restIncluded
            )
            .putLong(
                getKeyName(KEY_DAY_SETTINGS_REST_START_IN_MILLIS, daySettings.dayOfWeek),
                daySettings.restStartInMillis
            )
            .putLong(
                getKeyName(KEY_DAY_SETTINGS_REST_END_IN_MILLIS, daySettings.dayOfWeek),
                daySettings.restEndInMillis
            )
            .apply()
    }

    private fun getKeyName(keyPrefix: String, keyPostfix: Int): String =
        "${keyPrefix}_${keyPostfix}"

    companion object {
        private const val SHARED_PREFS_NAME = "scheduler_settings"

        private const val KEY_PAUSE_AFTER_SESSION_IN_MILLIS = "PAUSE_AFTER_SESSION_IN_MILLIS"
        private const val KEY_DAY_SETTINGS_IS_WORKING = "DAY_SETTINGS_IS_WORKING"
        private const val KEY_DAY_SETTINGS_DAY_START_IN_MILLIS = "DAY_SETTINGS_DAY_START_IN_MILLIS"
        private const val KEY_DAY_SETTINGS_DAY_END_IN_MILLIS = "DAY_SETTINGS_DAY_END_IN_MILLIS"
        private const val KEY_DAY_SETTINGS_REST_INCLUDED = "DAY_SETTINGS_REST_INCLUDED"
        private const val KEY_DAY_SETTINGS_REST_START_IN_MILLIS = "DAY_SETTINGS_REST_START_IN_MILLIS"
        private const val KEY_DAY_SETTINGS_REST_END_IN_MILLIS = "DAY_SETTINGS_REST_END_IN_MILLIS"
        private const val KEY_SESSION_DURATIONS = "SESSION_DURATIONS"

        private const val DEFAULT_PAUSE_AFTER_SESSION = 10 // min
        private const val DEFAULT_WORKING_DAY_START = 9 // hour
        private const val DEFAULT_WORKING_DAY_END = 18 // hour
        private const val DEFAULT_WORKING_DAY_REST_START = 13 // hour
        private const val DEFAULT_WORKING_DAY_REST_END = 14 // hour
        private const val DEFAULT_SESSION_DURATION = 1 // hour
    }
}