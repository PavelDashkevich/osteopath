package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable

data class WorkingDaySettings(
    var dayOfWeek: Int = 0,
    var isWorkingDay: Boolean = true,
    var dayStartInMillis: Long = 0L,
    var dayEndInMillis: Long = 0L,
    var restIncluded: Boolean = false,
    var restStartInMillis: Long = 0L,
    var restEndInMillis: Long = 0L
) : DiffUtilComparable {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is WorkingDaySettings) return false

        return this.dayOfWeek == item.dayOfWeek
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item !is WorkingDaySettings) return false

        return isWorkingDay != item.isWorkingDay ||
                dayStartInMillis != item.dayStartInMillis ||
                dayEndInMillis != item.dayEndInMillis ||
                restIncluded != item.restIncluded ||
                restStartInMillis != item.restStartInMillis ||
                restEndInMillis != item.restEndInMillis
    }

}