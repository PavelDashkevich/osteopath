package by.dashkevichpavel.osteopath.features.settings.backup.create

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.helpers.backups.BackupDirCheckResult
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.helpers.formatDateAsString
import by.dashkevichpavel.osteopath.helpers.formatTimeAsString
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import by.dashkevichpavel.osteopath.repositories.sharedprefs.BackupSettingsSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class BackupCreateViewModel(
    applicationContext: Context,
    repository: LocalDbRepository
) : ViewModel() {
    private val backupHelper = BackupHelper(applicationContext, repository)

    val backupDir = MutableLiveData(backupHelper.backupSettingsSharedPrefs.backupDir)
    val autoBackupEnabled = MutableLiveData(backupHelper.backupSettingsSharedPrefs.autoBackupEnabled)
    val lastBackupDateTime = MutableLiveData(backupHelper.backupSettingsSharedPrefs.lastBackupDateTime)
    val uiState = MutableLiveData(BackupCreateUIState.START)

    private var jobManualBackup: Job? = null

    fun getLastBackupDateFormatted(): String =
        Date(backupHelper.backupSettingsSharedPrefs.lastBackupDateTime).formatDateAsString()

    fun getLastBackupTimeFormatted(): String =
        Date(backupHelper.backupSettingsSharedPrefs.lastBackupDateTime).formatTimeAsString()

    fun getLastBackupIsAuto(): Boolean =
        backupHelper.backupSettingsSharedPrefs.isLastBackupTypeAuto

    fun getLastBackupIsFailed(): Boolean =
        backupHelper.backupSettingsSharedPrefs.isLastBackupFailed

    fun getLastBackupFailureMessage(): String =
        backupHelper.backupSettingsSharedPrefs.lastBackupFailureMessage

    fun checkSettings() {
        uiState.value = when (backupHelper.checkBackupDir()) {
            BackupDirCheckResult.NOT_SET -> BackupCreateUIState.NO_BACKUP_DIR
            BackupDirCheckResult.SET_NOT_EXISTS -> BackupCreateUIState.BACKUP_DIR_NOT_EXISTS
            BackupDirCheckResult.EXISTS_NOT_ACCESSIBLE -> BackupCreateUIState.BACKUP_DIR_NOT_ACCESSIBLE
            BackupDirCheckResult.EXISTS_ACCESSIBLE -> BackupCreateUIState.READY
        }
    }

    fun setEnableAutoBackup(isEnabled: Boolean) {
        backupHelper.backupSettingsSharedPrefs.autoBackupEnabled = isEnabled
    }

    fun pickDirectory(uriDir: Uri) {
        backupHelper.backupSettingsSharedPrefs.backupDir = uriDir.toString()
        backupDir.value = backupHelper.backupSettingsSharedPrefs.backupDir
        uiState.value = BackupCreateUIState.READY
    }

    fun navigateUp() {
        if (uiState.value != BackupCreateUIState.MANUAL_BACKUP_IN_PROGRESS) {
            backupHelper.backupSettingsSharedPrefs.saveSettings()
            uiState.value = BackupCreateUIState.FINISH
        }
    }

    fun createBackup() {
        if (jobManualBackup == null || jobManualBackup?.isCompleted == true) {
            jobManualBackup = viewModelScope.launch(Dispatchers.IO) {
                val newUIState = when (backupHelper.checkBackupDir()) {
                    BackupDirCheckResult.NOT_SET -> BackupCreateUIState.NO_BACKUP_DIR
                    BackupDirCheckResult.SET_NOT_EXISTS -> BackupCreateUIState.BACKUP_DIR_NOT_EXISTS
                    BackupDirCheckResult.EXISTS_NOT_ACCESSIBLE -> BackupCreateUIState.BACKUP_DIR_NOT_ACCESSIBLE
                    BackupDirCheckResult.EXISTS_ACCESSIBLE -> BackupCreateUIState.MANUAL_BACKUP_IN_PROGRESS
                }

                uiState.postValue(newUIState)

                if (newUIState != BackupCreateUIState.MANUAL_BACKUP_IN_PROGRESS) return@launch

                backupHelper.createBackup(false)

                lastBackupDateTime.postValue(
                    backupHelper.backupSettingsSharedPrefs.lastBackupDateTime
                )

                uiState.postValue(BackupCreateUIState.READY)
            }
        }
    }
}