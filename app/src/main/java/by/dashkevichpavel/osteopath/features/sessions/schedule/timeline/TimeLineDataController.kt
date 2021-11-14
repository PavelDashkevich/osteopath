package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import by.dashkevichpavel.osteopath.features.sessions.schedule.calendar.CalendarDayItem
import by.dashkevichpavel.osteopath.model.NoSessionsPeriod
import by.dashkevichpavel.osteopath.model.SessionAndCustomer
import by.dashkevichpavel.osteopath.model.WorkingDaySettings

class TimeLineDataController(
    private val workingDaysSettings: List<WorkingDaySettings>,
    private val pauseAfterSession: Long,
    private val sessionDurations: List<Long>
) {
    private var timeLine: MutableList<TimeInterval> = mutableListOf()

    fun updateTimeLine(
        calendarDayItems: List<CalendarDayItem>,
        sessions: List<SessionAndCustomer>,
        noSessionsPeriods: List<NoSessionsPeriod>
    ) {
        timeLine.clear()

        val days = calendarDayItems.filterIsInstance<CalendarDayItem.Day>()
        days.forEach { day: CalendarDayItem.Day ->
            val dayTimeLine: MutableList<TimeInterval> = mutableListOf()

            addToDayTimeLineSessions(dayTimeLine, day, sessions)

            if (day.data.isWorkingDay) {
                addTimeIntervalsToWorkingDayTimeLine(dayTimeLine, day, noSessionsPeriods,
                    workingDaysSettings)
            } else {
                addTimeIntervalsToDayOffTimeLine(dayTimeLine, day, noSessionsPeriods)
            }

            timeLine.addAll(dayTimeLine)
        }

        timeLine.sortBy { timeInterval: TimeInterval -> timeInterval.startTimeMillis }
    }

    fun getTimeLineForOutput(
        selectedDay: CalendarDayItem?,
        filterBySessionDuration: Long?
    ): List<TimeInterval> {
        var result = timeLine.filter { timeInterval: TimeInterval ->
            timeInterval !is TimeInterval.NoSessions.Auto.DayStart &&
                    timeInterval !is TimeInterval.NoSessions.Auto.DayEnd
        }

        selectedDay?.let {
            if (selectedDay is CalendarDayItem.Day) {
                result = result.filter { timeInterval: TimeInterval ->
                    val dayRange =
                        selectedDay.data.startOfDayInMillis..selectedDay.data.endOfDayInMillis
                    timeInterval.startTimeMillis in dayRange &&
                    timeInterval.endTimeMillis in dayRange
                }
            }
        }

        filterBySessionDuration?.let {
            result = result
                .filterIsInstance<TimeInterval.AvailableToSchedule>()
                .filter { timeInterval: TimeInterval ->
                    timeInterval.endTimeMillis - timeInterval.startTimeMillis >=
                            filterBySessionDuration
                }
            }

        return result
    }

    private fun addTimeIntervalsToDayOffTimeLine(
        dayTimeLine: MutableList<TimeInterval>,
        day: CalendarDayItem.Day,
        noSessionsPeriods: List<NoSessionsPeriod>
    ) {
        dayTimeLine.add(
            TimeInterval.NoSessions.Auto.DayOff(
                day.data.startOfDayInMillis,
                day.data.endOfDayInMillis
            )
        )

        addToDayTimeLineNoSessionsPeriods(dayTimeLine, day, noSessionsPeriods)
    }

    private fun addTimeIntervalsToWorkingDayTimeLine(
        dayTimeLine: MutableList<TimeInterval>,
        day: CalendarDayItem.Day,
        noSessionsPeriods: List<NoSessionsPeriod>,
        workingDaysSettings: List<WorkingDaySettings>
    ) {
        val workingDaySettings = workingDaysSettings.firstOrNull { daySettings ->
            daySettings.dayOfWeek == day.data.dayOfWeek
        } ?: return
        if (workingDaySettings.dayStartInMillis > 0L) {
            dayTimeLine.add(
                TimeInterval.NoSessions.Auto.DayStart(
                    day.data.startOfDayInMillis,
                    day.data.startOfDayInMillis + workingDaySettings.dayStartInMillis
                )
            )
        }

        if (workingDaySettings.dayEndInMillis > 0L) {
            dayTimeLine.add(
                TimeInterval.NoSessions.Auto.DayEnd(
                    day.data.startOfDayInMillis + workingDaySettings.dayEndInMillis,
                    day.data.endOfDayInMillis
                )
            )
        }

        dayTimeLine.add(
            TimeInterval.NoSessions.Auto.Rest(
                day.data.startOfDayInMillis + workingDaySettings.restStartInMillis,
                day.data.startOfDayInMillis + workingDaySettings.restEndInMillis
            )
        )

        addToDayTimeLineNoSessionsPeriods(dayTimeLine, day, noSessionsPeriods)

        dayTimeLine.sortBy { timeInterval: TimeInterval -> timeInterval.startTimeMillis }

        val minSessionDuration = sessionDurations.minOrNull() ?: 0L
        val availableToScheduleIntervals = mutableListOf<TimeInterval.AvailableToSchedule>()

        for (index in 0..(dayTimeLine.size - 2)) {
            val currentInterval = dayTimeLine[index]
            val nextInterval = dayTimeLine[index + 1]
            var interval: Long = nextInterval.startTimeMillis - currentInterval.endTimeMillis

            if (currentInterval is TimeInterval.SessionTime &&
                nextInterval is TimeInterval.SessionTime
            ) {
                interval -= pauseAfterSession
            }

            if (interval >= minSessionDuration) {
                availableToScheduleIntervals.add(
                    TimeInterval.AvailableToSchedule(
                        currentInterval.endTimeMillis,
                        currentInterval.endTimeMillis + interval
                    )
                )
            }
        }

        dayTimeLine.addAll(availableToScheduleIntervals)
    }

    private fun addToDayTimeLineNoSessionsPeriods(
        dayTimeLine: MutableList<TimeInterval>,
        day: CalendarDayItem.Day,
        noSessionsPeriods: List<NoSessionsPeriod>
    ) {
        val dayRange = day.data.startOfDayInMillis..day.data.endOfDayInMillis

        noSessionsPeriods
            .filter { noSessionsPeriod: NoSessionsPeriod ->
                noSessionsPeriod.dateTimeStart.time in dayRange
            }
            .forEach { noSessionsPeriod: NoSessionsPeriod ->
                dayTimeLine.add(TimeInterval.NoSessions.Manual(noSessionsPeriod))
            }
    }

    private fun addToDayTimeLineSessions(
        dayTimeLine: MutableList<TimeInterval>,
        day: CalendarDayItem.Day,
        sessions: List<SessionAndCustomer>
    ) {
        val dayRange = day.data.startOfDayInMillis..day.data.endOfDayInMillis

        sessions
            .filter { sessionAndCustomer: SessionAndCustomer ->
                sessionAndCustomer.session.dateTime.time in dayRange
            }
            .forEach { sessionAndCustomer: SessionAndCustomer ->
                dayTimeLine.add(TimeInterval.SessionTime(sessionAndCustomer))
            }
    }
}