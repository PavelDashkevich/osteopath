package by.dashkevichpavel.osteopath.features.nosessionperiod

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionEventsHandler
import by.dashkevichpavel.osteopath.helpers.savechanges.SavableInterface
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesViewModelHelper
import by.dashkevichpavel.osteopath.helpers.setDatePartFromTimeInMillis
import by.dashkevichpavel.osteopath.helpers.setTimePartFromTimeInMillis
import by.dashkevichpavel.osteopath.model.NoSessionsPeriod
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.repositories.data.LocalDbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class NoSessionPeriodViewModel(
    private val repository: LocalDbRepository
) : ViewModel(), SavableInterface {
    private var dataInitialized = false
    val noSessionsPeriod: MutableLiveData<NoSessionsPeriod> = MutableLiveData(NoSessionsPeriod())
    private var initialNoSessionsPeriod: NoSessionsPeriod = NoSessionsPeriod()
    val periodStart: MutableLiveData<Date> = MutableLiveData(noSessionsPeriod.value?.dateTimeStart)
    val periodEnd: MutableLiveData<Date> = MutableLiveData(noSessionsPeriod.value?.dateTimeEnd)
    private var jobSave: Job? = null
    val currentNoSessionsPeriodId = MutableLiveData(0L)
    val saveChangesHelper = SaveChangesViewModelHelper(this)
    val noSessionsPeriodDeletionHandler =
        ItemDeletionEventsHandler(this::onNoSessionsPeriodDeleteConfirmation)

    fun selectNoSessionsPeriod(noSessionsPeriodId: Long, defaultStartDateTime: Long,
                               defaultEndDateTime: Long) {
        if (dataInitialized) return

        if (noSessionsPeriodId == 0L) {
            setNoSessionsPeriod(
                NoSessionsPeriod(
                    dateTimeStart =
                        if (defaultStartDateTime == 0L) Date() else Date(defaultStartDateTime),
                    dateTimeEnd =
                        if (defaultEndDateTime == 0L) Date() else Date(defaultEndDateTime)
                )
            )
        }

        viewModelScope.launch {
            if (noSessionsPeriodId != 0L) {
                setNoSessionsPeriod(
                    repository.getNoSessionsPeriodById(noSessionsPeriodId)
                        ?: NoSessionsPeriod()
                )
            }
        }

        dataInitialized = true
    }

    fun setNoSessionsPeriodDate(timeInMillis: Long) {
        noSessionsPeriod.value?.let { periodObject ->
            periodObject.dateTimeStart.setDatePartFromTimeInMillis(timeInMillis)
            periodObject.dateTimeEnd.setDatePartFromTimeInMillis(timeInMillis)
            periodStart.value = periodObject.dateTimeStart
            periodEnd.value = periodObject.dateTimeEnd
        }
    }

    fun setNoSessionsPeriodStartTime(timeInMillis: Long) {
        noSessionsPeriod.value?.let { periodObject ->
            periodObject.dateTimeStart.setTimePartFromTimeInMillis(timeInMillis)
            periodStart.value = periodObject.dateTimeStart
        }
    }

    fun setNoSessionsPeriodEndTime(timeInMillis: Long) {
        noSessionsPeriod.value?.let { periodObject ->
            periodObject.dateTimeEnd.time = periodObject.dateTimeStart.time
            periodObject.dateTimeEnd.setTimePartFromTimeInMillis(timeInMillis)
            periodEnd.value = periodObject.dateTimeEnd
        }
    }

    fun getNoSessionsPeriodDefaultStartDateTimeInMillis(): Long =
        noSessionsPeriod.value?.dateTimeStart?.time ?: System.currentTimeMillis()

    fun getNoSessionsPeriodDefaultEndDateTimeInMillis(): Long =
        noSessionsPeriod.value?.dateTimeEnd?.time ?: System.currentTimeMillis()

    fun getNoSessionsPeriodId(): Long = currentNoSessionsPeriodId.value ?: 0L

    private fun setNoSessionsPeriod(newPeriod: NoSessionsPeriod) {
        noSessionsPeriod.value = newPeriod
        initialNoSessionsPeriod = newPeriod.copy(
            dateTimeStart = Date(newPeriod.dateTimeStart.time),
            dateTimeEnd = Date(newPeriod.dateTimeEnd.time)
        )
        periodStart.value = newPeriod.dateTimeStart
        periodEnd.value = newPeriod.dateTimeEnd
        updateNoSessionsPeriodId()
    }

    private fun updateNoSessionsPeriodId() {
        currentNoSessionsPeriodId.value = noSessionsPeriod.value?.id ?: 0L
    }

    private fun onNoSessionsPeriodDeleteConfirmation(
        noSessionsPeriodId: Long,
        userAction: DialogUserAction
    ) {
        if (userAction == DialogUserAction.POSITIVE) {
            viewModelScope.launch {
                repository.deleteNoSessionsPeriodById(noSessionsPeriodId)
            }
        }
    }

    override fun isDataModified(): Boolean =
        initialNoSessionsPeriod.isModified(noSessionsPeriod.value)

    override fun saveData() {
        noSessionsPeriod.value?.let { periodObject ->
            if (jobSave == null || jobSave?.isCompleted != false) {
                jobSave = viewModelScope.launch {
                    saveChangesHelper.startSaving()
                    periodObject.id = repository.insertNoSessionPeriod(periodObject)
                    saveChangesHelper.finishSaving()
                    updateNoSessionsPeriodId()
                    saveChangesHelper.navigateUp()
                }
            }
        }

        if (noSessionsPeriod.value == null) saveChangesHelper.navigateUp()
    }
}