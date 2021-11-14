package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import by.dashkevichpavel.osteopath.model.NoSessionsPeriod
import by.dashkevichpavel.osteopath.model.SessionAndCustomer

sealed class TimeInterval(var startTimeMillis: Long, var endTimeMillis: Long) {
    class AvailableToSchedule(startTimeMillis: Long, endTimeMillis: Long) :
        TimeInterval(startTimeMillis, endTimeMillis)
    class SessionTime(var sessionAndCustomer: SessionAndCustomer) :
        TimeInterval(
            sessionAndCustomer.session.dateTime.time,
            sessionAndCustomer.session.dateTimeEnd.time
        )
    sealed class NoSessions(startTimeMillis: Long, endTimeMillis: Long) :
        TimeInterval(startTimeMillis, endTimeMillis) {
            sealed class Auto(startTimeMillis: Long, endTimeMillis: Long) :
                NoSessions(startTimeMillis, endTimeMillis) {
                    class DayStart(startTimeMillis: Long, endTimeMillis: Long) :
                        Auto(startTimeMillis, endTimeMillis)
                    class Rest(startTimeMillis: Long, endTimeMillis: Long) :
                        Auto(startTimeMillis, endTimeMillis)
                    class DayEnd(startTimeMillis: Long, endTimeMillis: Long) :
                        Auto(startTimeMillis, endTimeMillis)
                    class DayOff(startTimeMillis: Long, endTimeMillis: Long) :
                        Auto(startTimeMillis, endTimeMillis)
                }
            class Manual(var noSessionsPeriod: NoSessionsPeriod) :
                NoSessions(noSessionsPeriod.dateTimeStart.time, noSessionsPeriod.dateTimeEnd.time)
        }
}