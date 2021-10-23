package by.dashkevichpavel.osteopath.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import by.dashkevichpavel.osteopath.helpers.operationresult.OperationResult
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper

class AutoBackupWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        var result = Result.success()
        val backupHelper = BackupHelper(applicationContext)

        if (backupHelper.backupSettingsSharedPrefs.autoBackupEnabled) {
            if (backupHelper.createBackup() is OperationResult.Error) {
                result = Result.retry()
            }
        }

        return result
    }

    companion object {
        const val WORK_NAME = "AutoBackupWorker"
    }
}