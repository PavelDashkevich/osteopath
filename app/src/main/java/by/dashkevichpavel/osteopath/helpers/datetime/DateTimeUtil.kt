package by.dashkevichpavel.osteopath.helpers.datetime

import android.content.Context
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.helpers.setTimeComponents
import java.util.*

class DateTimeUtil {
    companion object {
        fun formatTimeAsDurationString(context: Context, timeInMillis: Long): String {
            val hourOfDayAndMinutes = getHourOfDayAndMinutesFromDuration(timeInMillis)
            var result =
                if (hourOfDayAndMinutes.first != 0)
                    "${hourOfDayAndMinutes.first} ${context.getString(R.string.common_duration_extended_hour)} "
                else
                    ""
            result +=
                if (hourOfDayAndMinutes.second != 0)
                    "${hourOfDayAndMinutes.second} ${context.getString(R.string.common_duration_extended_minutes)}"
                else
                    ""

            return result
        }

        fun getDurationInMillis(hourOfDay: Int, minutes: Int = 0): Long =
            (hourOfDay * 60 + minutes).toLong() * 60_000L

        fun durationToTime(durationInMillis: Long): Date {
            val c = Calendar.getInstance()
            val hourOfDayAndMinutes = getHourOfDayAndMinutesFromDuration(durationInMillis)
            c.setTimeComponents(hourOfDayAndMinutes.first, hourOfDayAndMinutes.second)
            return Date(c.timeInMillis)
        }

        fun getHourOfDayAndMinutesFromDuration(durationInMillis: Long): Pair<Int, Int> {
            var time = (durationInMillis / 60_000L)
            val minutes = (time % 60).toInt()
            time /= 60L
            val hourOfDay = (time % 60).toInt()
            return hourOfDay to minutes
        }
    }
}