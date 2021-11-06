package by.dashkevichpavel.osteopath.features.settings.scheduler

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable

data class SessionDurationItem(val duration: Long) : DiffUtilComparable {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is SessionDurationItem) return false

        return duration == item.duration
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean =
        isTheSameItemAs(item)
}