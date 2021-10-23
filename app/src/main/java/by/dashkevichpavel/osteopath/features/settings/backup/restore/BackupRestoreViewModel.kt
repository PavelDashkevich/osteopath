package by.dashkevichpavel.osteopath.features.settings.backup.restore

import android.content.Context
import android.content.OperationApplicationException
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Operation
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.helpers.operationresult.OperationResult
import by.dashkevichpavel.osteopath.services.AutoBackupWorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BackupRestoreViewModel(applicationContext: Context) : ViewModel() {
    private val backupHelper = BackupHelper(applicationContext)
    private val autoBackupWorkManager = AutoBackupWorkManager(applicationContext)
    private var backupRestoreResultErrorMessage: String = ""
    val backupFile = MutableLiveData("")
    val uiState = MutableLiveData(BackupRestoreUIState.START)
    private var stopAutoBackupOperation: LiveData<Operation.State>? = null
    private var jobRestoringDBFromBackup: Job? = null

    fun pickBackupFile(backupFileUri: Uri) {
        backupFile.value = backupFileUri.toString()
        uiState.value =
            if (backupFile.value?.isEmpty() == true)
                BackupRestoreUIState.START
            else
                BackupRestoreUIState.BACKUP_FILE_SET
    }

    fun navigateUp() {
        if (uiState.value != BackupRestoreUIState.RESTORE_IN_PROGRESS) {
            uiState.value = BackupRestoreUIState.FINISH
        }
    }

    fun resetState() {
        uiState.value = BackupRestoreUIState.BACKUP_FILE_SET
    }

    fun startRestoringDBFromBackup() {
        uiState.value = BackupRestoreUIState.RESTORE_IN_PROGRESS
        stopAutoBackupOperation = autoBackupWorkManager.stopWork()
        stopAutoBackupOperation?.observeForever(this::onStopAutoBackupOperationStateChanged)
    }

    private fun continueRestoringDBFromBackup() {
        stopAutoBackupOperation?.removeObserver(this::onStopAutoBackupOperationStateChanged)
        stopAutoBackupOperation = null

        if (jobRestoringDBFromBackup == null || jobRestoringDBFromBackup?.isCompleted == true) {
            jobRestoringDBFromBackup = viewModelScope.launch(Dispatchers.IO) {
                val result: OperationResult =
                    backupHelper.restoreFromBackup(Uri.parse(backupFile.value))

                when (result) {
                    is OperationResult.Error -> {
                        backupRestoreResultErrorMessage = result.errorMessage
                        uiState.postValue(BackupRestoreUIState.RESTORE_COMPLETE_FAIL)
                    }
                    is OperationResult.Success ->
                        uiState.postValue(BackupRestoreUIState.RESTORE_COMPLETE_SUCCESS)
                }

                autoBackupWorkManager.setupWork()
            }
        }
    }

    fun getErrorMessage(): String = backupRestoreResultErrorMessage

    private fun onStopAutoBackupOperationStateChanged(operationState: Operation.State) {
        if (operationState !is Operation.State.IN_PROGRESS) {
            continueRestoringDBFromBackup()
        }
    }
}