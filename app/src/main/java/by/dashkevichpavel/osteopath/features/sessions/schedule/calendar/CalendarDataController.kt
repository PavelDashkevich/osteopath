package by.dashkevichpavel.osteopath.features.sessions.schedule.calendar

import androidx.lifecycle.MutableLiveData
import by.dashkevichpavel.osteopath.model.CalendarDay
import by.dashkevichpavel.osteopath.model.WorkingDaySettings
import java.util.*

class CalendarDataController(
    private val workingDaysSettings: List<WorkingDaySettings> = emptyList()
) {
    private var startOfMonth: Calendar = Calendar.getInstance()
    private var endOfMonth: Calendar = Calendar.getInstance()
    private val calendarDaysItems: MutableList<CalendarDayItem> = mutableListOf()

    val startOfMonthInMillisLiveData = MutableLiveData(startOfMonth.timeInMillis)
    val calendarDaysItemsLiveData = MutableLiveData(calendarDaysItems)

    init {
        setToDate(Calendar.getInstance().timeInMillis)
    }

    fun setToDate(dateInMillis: Long) {
        val c = Calendar.getInstance()
        c.timeInMillis = dateInMillis
        startOfMonth = getStartOfMonth(c)
        endOfMonth = getEndOfMonth(startOfMonth)
        fillCalendarDaysItems()
        updateStartOfMonthLiveData()
    }

    fun nextMonth() = addMonth(1)
    fun prevMonth() = addMonth(-1)

    fun setBadges(sessionTimes: List<Long>) {
        for (index in calendarDaysItems.indices) {
            val day = calendarDaysItems[index]
            if (day is CalendarDayItem.Day) {
                val badgeValue = sessionTimes.count { sessionTime: Long ->
                    sessionTime >= day.data.startOfDayInMillis &&
                            sessionTime <= day.data.endOfDayInMillis
                }

                if (badgeValue != 0) {
                    calendarDaysItems[index] =
                        CalendarDayItem.Day(day.data.copy(badge = badgeValue))
                }
            }
        }
        updateCalendarDaysItemsLiveData()
    }

    fun getCalendarDaysItems(): List<CalendarDayItem> = calendarDaysItems
    fun getStartOfMonthInMillis(): Long = startOfMonth.timeInMillis
    fun getEndOfMonthInMillis(): Long = endOfMonth.timeInMillis

    fun selectDay(calendarDayItem: CalendarDayItem) {
        if (calendarDayItem is CalendarDayItem.Day) {
            val dayToSelect = calendarDaysItems.indexOfFirst { day ->
                if (day is CalendarDayItem.Day) {
                    day.data.startOfDayInMillis == calendarDayItem.data.startOfDayInMillis &&
                            day.data.endOfDayInMillis == calendarDayItem.data.endOfDayInMillis
                } else {
                    false
                }
            }
            if (dayToSelect in calendarDaysItems.indices) {
                changeSelectedStateForDay(dayToSelect)
                calendarDaysItems.forEachIndexed { index, dayItem ->
                    if (index != dayToSelect && dayItem is CalendarDayItem.Day &&
                        dayItem.data.isSelected) {
                        changeSelectedStateForDay(index)
                    }
                }
            }
            updateCalendarDaysItemsLiveData()
        }
    }

    fun getSelectedDay(): CalendarDayItem? =
        calendarDaysItems.firstOrNull { day -> day is CalendarDayItem.Day && day.data.isSelected }

    private fun changeSelectedStateForDay(index: Int) {
        val day = calendarDaysItems[index]
        if (day is CalendarDayItem.Day) {
            calendarDaysItems[index] =
                CalendarDayItem.Day(
                    day.data.copy(isSelected = !day.data.isSelected)
                )
        }
    }

    private fun addMonth(numOfMonths: Int) {
        startOfMonth.add(Calendar.MONTH, numOfMonths)
        endOfMonth = getEndOfMonth(startOfMonth)
        fillCalendarDaysItems()
        updateStartOfMonthLiveData()
    }

    private fun fillCalendarDaysItems() {
        calendarDaysItems.clear()
        val dayOfWeekOfFirstMonthDay = getDayOfWeekIndex(startOfMonth)
        val dayOfWeekOfLastMonthDay = getDayOfWeekIndex(endOfMonth)

        for (i in FIRST_DAY_OF_WEEK_INDEX until dayOfWeekOfFirstMonthDay) {
            calendarDaysItems.add(CalendarDayItem.Empty())
        }

        val firstMonthDay = startOfMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastMonthDay = endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        val c = Calendar.getInstance()
        val today = c.timeInMillis
        c.timeInMillis = startOfMonth.timeInMillis

        for (i in firstMonthDay..lastMonthDay) {
            c.set(Calendar.DAY_OF_MONTH, i)
            val dayOfWeek = getDayOfWeekIndex(c)
            val startOfDay = c.timeInMillis
            val endOfDay = c.timeInMillis + DURATION_OF_24_HOURS_IN_MILLIS - 1
            calendarDaysItems.add(
                CalendarDayItem.Day(
                    CalendarDay(
                        i,
                        dayOfWeek,
                        startOfDay,
                        endOfDay,
                        isWorkingDay = workingDaysSettings.getOrNull(dayOfWeek)?.isWorkingDay
                            ?: true,
                        isToday = (today in startOfDay..endOfDay)
                    )
                )
            )
        }

        if (dayOfWeekOfLastMonthDay != LAST_DAY_OF_WEEK_INDEX) {
            for (i in (dayOfWeekOfLastMonthDay + 1)..LAST_DAY_OF_WEEK_INDEX) {
                calendarDaysItems.add(CalendarDayItem.Empty())
            }
        }

        updateCalendarDaysItemsLiveData()
    }

    private fun getDayOfWeekIndex(calendar: Calendar): Int =
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 6
        }

    private fun getStartOfMonth(calendar: Calendar): Calendar {
        val c = Calendar.getInstance()
        c.timeInMillis = calendar.timeInMillis
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c
    }

    private fun getEndOfMonth(calendar: Calendar): Calendar {
        val c = Calendar.getInstance()
        c.timeInMillis = calendar.timeInMillis
        c.add(Calendar.MONTH, 1)
        c.add(Calendar.MILLISECOND, -1)
        return c
    }

    private fun updateStartOfMonthLiveData() {
        startOfMonthInMillisLiveData.value = startOfMonth.timeInMillis
    }

    private fun updateCalendarDaysItemsLiveData() {
        calendarDaysItemsLiveData.value = calendarDaysItems
    }

    companion object {
        private const val DURATION_OF_24_HOURS_IN_MILLIS = 24L * 3_600_000L
        private const val FIRST_DAY_OF_WEEK_INDEX = 0
        private const val LAST_DAY_OF_WEEK_INDEX = 6
    }
}