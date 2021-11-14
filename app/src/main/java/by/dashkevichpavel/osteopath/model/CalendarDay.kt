package by.dashkevichpavel.osteopath.model

data class CalendarDay(
    var dayOfMonth: Int,
    var dayOfWeek: Int,
    var startOfDayInMillis: Long,
    var endOfDayInMillis: Long,
    var badge: Int = 0,
    var isWorkingDay: Boolean = true,
    var isToday: Boolean = false,
    var isSelected: Boolean = false
    )