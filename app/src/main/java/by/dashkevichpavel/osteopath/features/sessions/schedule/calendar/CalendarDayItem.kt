package by.dashkevichpavel.osteopath.features.sessions.schedule.calendar

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable
import by.dashkevichpavel.osteopath.model.CalendarDay

open class CalendarDayItem : DiffUtilComparable {
    class Empty : CalendarDayItem() {
        override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean = (item is Empty)
        override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean = (item is Empty)
    }

    class Day(var data: CalendarDay) : CalendarDayItem() {
        override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
            if (item !is Day) return false

            return data.startOfDayInMillis == item.data.startOfDayInMillis
        }

        override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
            if (item !is Day) return false

            return data.dayOfMonth == item.data.dayOfMonth &&
                    data.dayOfWeek == item.data.dayOfWeek &&
                    data.badge == item.data.badge &&
                    data.isToday == item.data.isToday &&
                    data.isWorkingDay == item.data.isWorkingDay &&
                    data.isSelected == item.data.isSelected
        }
    }

    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean = false
    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean = false
}