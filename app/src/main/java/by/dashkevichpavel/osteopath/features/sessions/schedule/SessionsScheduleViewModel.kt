package by.dashkevichpavel.osteopath.features.sessions.schedule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.features.sessions.schedule.calendar.CalendarDataController
import by.dashkevichpavel.osteopath.features.sessions.schedule.calendar.CalendarDayItem
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeInterval
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeIntervalItem
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeIntervalItemFactory
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeLineDataController
import by.dashkevichpavel.osteopath.helpers.jobs.FlowJobController
import by.dashkevichpavel.osteopath.model.NoSessionsPeriod
import by.dashkevichpavel.osteopath.model.SessionAndCustomer
import by.dashkevichpavel.osteopath.repositories.data.LocalDbRepository
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.SchedulerSettingsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class SessionsScheduleViewModel(
    private val repository: LocalDbRepository,
    private val schedulerSettingsRepository: SchedulerSettingsRepository
) : ViewModel() {
    private val workingDaySettings = schedulerSettingsRepository.getWorkingDaysSettings()
    val sessionDurations = schedulerSettingsRepository.getSessionDurationsInMillis()
    private val calendarDataController = CalendarDataController(workingDaySettings)
    private val timeLineDataController = TimeLineDataController(workingDaySettings,
        schedulerSettingsRepository.getPauseAfterSessionInMillis(), sessionDurations)
    val calendarDayItems = MutableLiveData(calendarDataController.getCalendarDaysItems())
    val currentMonth = MutableLiveData(calendarDataController.getStartOfMonthInMillis())
    private var filterBySessionDuration: Long? = null

    private val sessionsLoader = FlowJobController(viewModelScope, this::loadSessions)
    private val noSessionsPeriodsLoader = FlowJobController(viewModelScope, this::loadNoSessionsPeriods)

    private var sessions: List<SessionAndCustomer> = listOf()
    private var noSessionsPeriods: List<NoSessionsPeriod> = listOf()

    val timeLine = MutableLiveData<List<TimeIntervalItem>>()

    init {
        calendarDataController.startOfMonthInMillisLiveData.observeForever(::onChangeCurrentMonth)
        calendarDataController.calendarDaysItemsLiveData.observeForever(::onChangeCalendarDayItems)
    }

    fun startLoaders() {
        sessionsLoader.start()
        noSessionsPeriodsLoader.start()
    }

    fun stopLoaders() {
        sessionsLoader.stop()
        noSessionsPeriodsLoader.stop()
    }

    // region calendar controls
    fun goToMonthOfEarliestSession() {
        viewModelScope.launch {
            val startTimeOfEarliestSession = repository.getEarliestSessionTime() ?: return@launch
            calendarDataController.setToDate(startTimeOfEarliestSession)
        }
    }

    fun goToPrevMonth() = calendarDataController.prevMonth()
    fun goToNextMonth() = calendarDataController.nextMonth()

    fun goToMonthOfLatestSession() {
        viewModelScope.launch {
            val startTimeOfLatestSession = repository.getLatestSessionTime() ?: return@launch
            calendarDataController.setToDate(startTimeOfLatestSession)
        }
    }

    fun selectDay(calendarDayItem: CalendarDayItem) {
        calendarDataController.selectDay(calendarDayItem)
        updateTimeLine()
    }
    // end region

    fun setFilterBySessionDuration(durationPos: Int?) {
        filterBySessionDuration = if (durationPos == null) {
            null
        } else {
            sessionDurations[durationPos]
        }

        updateTimeLine()
    }

    private suspend fun loadSessions() {
        repository.getSessionsByPeriodAsFlow(
            calendarDataController.getStartOfMonthInMillis(),
            calendarDataController.getEndOfMonthInMillis()
        ).collect { sessionsAndCustomers ->
            sessions = sessionsAndCustomers

            val sessionTimes: List<Long> = sessionsAndCustomers.map { sessionAndCustomer ->
                sessionAndCustomer.session.dateTime.time
            }
            calendarDataController.setBadges(sessionTimes)
            updateTimeLineData()
        }
    }

    private suspend fun loadNoSessionsPeriods() {
        repository.getNoSessionPeriodsByPeriodAsFlow(
            calendarDataController.getStartOfMonthInMillis(),
            calendarDataController.getEndOfMonthInMillis()
        ).collect { noSessionsPeriods ->
            this.noSessionsPeriods = noSessionsPeriods
            updateTimeLineData()
        }
    }

    private fun updateTimeLineData() {
        timeLineDataController.updateTimeLine(
            calendarDataController.getCalendarDaysItems(),
            sessions,
            noSessionsPeriods
        )

        updateTimeLine()
    }

    private fun updateTimeLine() {
        timeLine.value = timeLineDataController.getTimeLineForOutput(
            calendarDataController.getSelectedDay(),
            filterBySessionDuration
        ).map { timeInterval: TimeInterval ->
            TimeIntervalItemFactory.createByTimeIntervalType(timeInterval)
        }
    }

    private fun onChangeCalendarDayItems(items: List<CalendarDayItem>) {
        calendarDayItems.value = items
    }

    private fun onChangeCurrentMonth(startOfMonthInMillis: Long) {
        currentMonth.value = startOfMonthInMillis
        sessionsLoader.stop()
        sessionsLoader.start()
    }

    override fun onCleared() {
        calendarDataController.startOfMonthInMillisLiveData
            .removeObserver(this::onChangeCurrentMonth)
        calendarDataController.calendarDaysItemsLiveData
            .removeObserver(this::onChangeCalendarDayItems)
        super.onCleared()
    }
}