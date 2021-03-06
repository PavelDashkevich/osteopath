package by.dashkevichpavel.osteopath.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.dashkevichpavel.osteopath.repositories.data.OsteoDbRepositorySingleton
import by.dashkevichpavel.osteopath.features.customerlist.CustomerListViewModel
import by.dashkevichpavel.osteopath.features.customerlistfilter.CustomerListFilterViewModel
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.disfunction.DisfunctionViewModel
import by.dashkevichpavel.osteopath.features.nosessionperiod.NoSessionPeriodViewModel
import by.dashkevichpavel.osteopath.features.selectdisfunctions.SelectDisfunctionsViewModel
import by.dashkevichpavel.osteopath.features.session.SessionViewModel
import by.dashkevichpavel.osteopath.features.sessions.SessionsViewModel
import by.dashkevichpavel.osteopath.features.sessions.recent.SessionsRecentViewModel
import by.dashkevichpavel.osteopath.features.sessions.schedule.SessionsScheduleViewModel
import by.dashkevichpavel.osteopath.features.sessions.upcoming.SessionsUpcomingViewModel
import by.dashkevichpavel.osteopath.features.settings.backup.create.BackupCreateViewModel
import by.dashkevichpavel.osteopath.features.settings.backup.restore.BackupRestoreViewModel
import by.dashkevichpavel.osteopath.features.settings.scheduler.SchedulerSettingsViewModel
import by.dashkevichpavel.osteopath.features.settings.scheduler.workingdays.WorkingDaysSettingsViewModel
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.SchedulerSettingsRepositoryImpl

class OsteoViewModelFactory(
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when (modelClass) {
            CustomerListViewModel::class.java ->
                CustomerListViewModel(
                    applicationContext,
                    OsteoDbRepositorySingleton.getInstance(applicationContext)
                )
            CustomerListFilterViewModel::class.java ->
                CustomerListFilterViewModel(applicationContext)
            CustomerProfileViewModel::class.java ->
                CustomerProfileViewModel(
                    applicationContext,
                    OsteoDbRepositorySingleton.getInstance(applicationContext)
                )
            DisfunctionViewModel::class.java ->
                DisfunctionViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            SessionViewModel::class.java ->
                SessionViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            SelectDisfunctionsViewModel::class.java ->
                SelectDisfunctionsViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            SessionsViewModel::class.java ->
                SessionsViewModel()
            BackupCreateViewModel::class.java ->
                BackupCreateViewModel(applicationContext)
            BackupRestoreViewModel::class.java ->
                BackupRestoreViewModel(applicationContext)
            SchedulerSettingsViewModel::class.java ->
                SchedulerSettingsViewModel(SchedulerSettingsRepositoryImpl(applicationContext))
            WorkingDaysSettingsViewModel::class.java ->
                WorkingDaysSettingsViewModel(SchedulerSettingsRepositoryImpl(applicationContext))
            SessionsUpcomingViewModel::class.java ->
                SessionsUpcomingViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            SessionsRecentViewModel::class.java ->
                SessionsRecentViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            SessionsScheduleViewModel::class.java ->
                SessionsScheduleViewModel(
                    OsteoDbRepositorySingleton.getInstance(applicationContext),
                    SchedulerSettingsRepositoryImpl(applicationContext)
                )
            NoSessionPeriodViewModel::class.java ->
                NoSessionPeriodViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            else ->
                throw IllegalArgumentException("$modelClass is not registered ViewModel")
        } as T
}