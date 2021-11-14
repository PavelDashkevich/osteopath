package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable

open class TimeIntervalItem(var timeInterval: TimeInterval) : DiffUtilComparable {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is TimeIntervalItem) return false

        return this.timeInterval.startTimeMillis == item.timeInterval.startTimeMillis &&
                this.timeInterval.endTimeMillis == item.timeInterval.endTimeMillis
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean = isTheSameItemAs(item)
}

class TimeIntervalItemSession(timeInterval: TimeInterval.SessionTime) : TimeIntervalItem(timeInterval) {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is TimeIntervalItemSession ||
            timeInterval !is TimeInterval.SessionTime) return false

        return (timeInterval as TimeInterval.SessionTime).sessionAndCustomer.isTheSameItemAs(
                (item.timeInterval as TimeInterval.SessionTime).sessionAndCustomer
            )
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item !is TimeIntervalItemSession ||
            timeInterval !is TimeInterval.SessionTime) return false

        return (timeInterval as TimeInterval.SessionTime).sessionAndCustomer.contentsTheSameAs(
                (item.timeInterval as TimeInterval.SessionTime).sessionAndCustomer
            )
    }
}

class TimeIntervalItemNoSessionsAuto(timeInterval: TimeInterval.NoSessions.Auto) :
    TimeIntervalItem(timeInterval)

class TimeIntervalItemNoSessionsManual(timeInterval: TimeInterval.NoSessions.Manual) :
    TimeIntervalItem(timeInterval) {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is TimeIntervalItemNoSessionsManual) return false

        return (timeInterval as TimeInterval.NoSessions.Manual).noSessionsPeriod.isTheSameItemAs(
            (item.timeInterval as TimeInterval.NoSessions.Manual).noSessionsPeriod
        )
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item !is TimeIntervalItemNoSessionsManual) return false

        return (timeInterval as TimeInterval.NoSessions.Manual).noSessionsPeriod.contentsTheSameAs(
            (item.timeInterval as TimeInterval.NoSessions.Manual).noSessionsPeriod
        )
    }
}

class TimeIntervalItemAvailableToSchedule(timeInterval: TimeInterval.AvailableToSchedule) :
    TimeIntervalItem(timeInterval)

class TimeIntervalItemFactory {
    companion object {
        fun createByTimeIntervalType(timeInterval: TimeInterval): TimeIntervalItem =
            when (timeInterval) {
                is TimeInterval.AvailableToSchedule ->
                    TimeIntervalItemAvailableToSchedule(timeInterval)
                is TimeInterval.NoSessions.Auto -> TimeIntervalItemNoSessionsAuto(timeInterval)
                is TimeInterval.NoSessions.Manual -> TimeIntervalItemNoSessionsManual(timeInterval)
                is TimeInterval.SessionTime -> TimeIntervalItemSession(timeInterval)
            }
    }
}