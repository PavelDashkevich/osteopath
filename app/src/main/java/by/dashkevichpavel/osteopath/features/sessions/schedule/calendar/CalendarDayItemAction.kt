package by.dashkevichpavel.osteopath.features.sessions.schedule.calendar

interface CalendarDayItemActionListener {
    fun onCalendarDayClick(action: CalendarDayItemAction)
}

sealed class CalendarDayItemAction {
    class Select(val calendarDayItem: CalendarDayItem) : CalendarDayItemAction()
}