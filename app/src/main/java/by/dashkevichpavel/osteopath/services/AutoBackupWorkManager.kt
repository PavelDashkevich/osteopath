package by.dashkevichpavel.osteopath.services

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AutoBackupWorkManager(private val applicationContext: Context) {
    fun setupWork() {
        val workManager = WorkManager.getInstance(applicationContext)
        val workInfo = workManager.getWorkInfosForUniqueWorkLiveData(AutoBackupWorker.WORK_NAME)
        var workWithCurrentTagExists = false

        workInfo.value?.let { listOfInfo ->
            listOfInfo.forEach { info: WorkInfo? ->
                info?.let { nonNullInfo ->
                    workWithCurrentTagExists = workWithCurrentTagExists ||
                            nonNullInfo.tags.contains(TAG_VERSION_OF_PERIODIC_WORK_SETTINGS)
                }
            }
        }

        val workRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(
            repeatInterval = INTERVAL_HOURS.toLong(),
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = (INTERVAL_HOURS.toLong() * 60L) / 2L,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .addTag(TAG_VERSION_OF_PERIODIC_WORK_SETTINGS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            AutoBackupWorker.WORK_NAME,
            if (!workWithCurrentTagExists)
                ExistingPeriodicWorkPolicy.REPLACE
            else
                ExistingPeriodicWorkPolicy.KEEP
            ,
            workRequest
        )
    }

    companion object {
        // if you change work request parameters (like intervals etc) then change version of settings
        // in constant below and existing periodic work will be "updated"
        const val TAG_VERSION_OF_PERIODIC_WORK_SETTINGS = "VERSION_1"
        const val INTERVAL_HOURS = 6
    }
}