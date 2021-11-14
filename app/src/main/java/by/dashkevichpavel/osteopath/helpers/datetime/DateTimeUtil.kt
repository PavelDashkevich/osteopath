package by.dashkevichpavel.osteopath.helpers.datetime

import android.content.Context
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.helpers.*
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

        fun getFullNameOfDayOfWeek(dayOfWeek: Int): String =
            getDateByDayOfWeek(dayOfWeek).formatAsDayOfWeekFullName().toCapitalized()

        fun getShortNameOfDayOfWeek(dayOfWeek: Int): String =
            getDateByDayOfWeek(dayOfWeek).formatAsDayOfWeekShortName().toCapitalized()

        private fun getDateByDayOfWeek(dayOfWeek: Int): Date {
            val index = dayOfWeek % 7
            val c = Calendar.getInstance()
            c.firstDayOfWeek = Calendar.MONDAY
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + index)

            return Date(c.timeInMillis)
        }

        fun formatAsMonthAndYearString(dateTimeInMillis: Long): String =
            Date(dateTimeInMillis).formatDateAsMonthAndYearString().toCapitalized()

        fun formatAsMonthShortString(dateTimeInMillis: Long): String =
             android.text.format.DateFormat.format(
                 "MMMM",
                 Date(dateTimeInMillis)
             ).toString().substring(0..2).uppercase()

        fun formatAsDayOfMonthString(dateTimeInMillis: Long): String =
            android.text.format.DateFormat.format("dd", Date(dateTimeInMillis)).toString()

        fun formatTimeAsString(dateTimeInMillis: Long): String =
            android.text.format.DateFormat.format(
                "HH:mm",
                Date(dateTimeInMillis)
            ).toString()
    }
}