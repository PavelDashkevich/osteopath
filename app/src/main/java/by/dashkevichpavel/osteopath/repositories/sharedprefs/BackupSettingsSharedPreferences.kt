package by.dashkevichpavel.osteopath.repositories.sharedprefs

import android.content.Context

class BackupSettingsSharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    var backupDir: String = ""
    var autoBackupEnabled: Boolean = false
    var lastBackupDateTime: Long = 0L
    var isLastBackupTypeAuto: Boolean = false
    var isLastBackupFailed: Boolean = false
    var lastBackupFailureMessage: String = ""

    init {
        loadSettings()
    }

    fun loadSettings() {
        backupDir = sharedPreferences.getString(KEY_BACKUP_DIR, "") ?: ""
        autoBackupEnabled = sharedPreferences.getBoolean(KEY_AUTO_BACKUP_ENABLED, false)
        lastBackupDateTime = sharedPreferences.getLong(KEY_LAST_BACKUP_DATE_TIME, 0L)
        isLastBackupTypeAuto = sharedPreferences.getBoolean(KEY_LAST_BACKUP_IS_AUTO, false)
        isLastBackupFailed = sharedPreferences.getBoolean(KEY_LAST_BACKUP_IS_FAILED, false)
        lastBackupFailureMessage =
            sharedPreferences.getString(KEY_LAST_BACKUP_FAILURE_MESSAGE, "") ?: ""
    }

    fun saveSettings() {
        sharedPreferences
            .edit()
            .putString(KEY_BACKUP_DIR, backupDir)
            .putBoolean(KEY_AUTO_BACKUP_ENABLED, autoBackupEnabled)
            .putLong(KEY_LAST_BACKUP_DATE_TIME, lastBackupDateTime)
            .putBoolean(KEY_LAST_BACKUP_IS_AUTO, isLastBackupTypeAuto)
            .putBoolean(KEY_LAST_BACKUP_IS_FAILED, isLastBackupFailed)
            .putString(KEY_LAST_BACKUP_FAILURE_MESSAGE, lastBackupFailureMessage)
            .apply()
    }

    companion object {
        const val SHARED_PREFS_NAME = "backup_settings"

        const val KEY_BACKUP_DIR = "KEY_BACKUP_DIR"
        const val KEY_AUTO_BACKUP_ENABLED = "KEY_AUTO_BACKUP_ENABLED"
        const val KEY_LAST_BACKUP_DATE_TIME = "KEY_LAST_BACKUP_DATE_TIME"
        const val KEY_LAST_BACKUP_IS_AUTO = "KEY_LAST_BACKUP_IS_AUTO"
        const val KEY_LAST_BACKUP_IS_FAILED = "KEY_LAST_BACKUP_IS_FAILED"
        const val KEY_LAST_BACKUP_FAILURE_MESSAGE = "KEY_LAST_BACKUP_FAILURE_MESSAGE"
    }
}