package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import android.view.View
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemNoSessionsPeriodAutoBinding
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil

class TimeIntervalItemNoSessionsPeriodAutoViewHolder(itemView: View) :
    TimeIntervalItemViewHolder(itemView) {
    private val binding = ListitemNoSessionsPeriodAutoBinding.bind(itemView)

    override fun bind(timeIntervalItem: TimeIntervalItem) {
        if (timeIntervalItem !is TimeIntervalItemNoSessionsAuto) return

        binding.tvHeader.text = itemView.context.getString(
            when (timeIntervalItem.timeInterval) {
                is TimeInterval.NoSessions.Auto.DayEnd ->
                    R.string.list_item_time_interval_no_sessions_auto_day_end
                is TimeInterval.NoSessions.Auto.DayOff ->
                    R.string.list_item_time_interval_no_sessions_auto_day_off
                is TimeInterval.NoSessions.Auto.DayStart ->
                    R.string.list_item_time_interval_no_sessions_auto_day_start
                is TimeInterval.NoSessions.Auto.Rest ->
                    R.string.list_item_time_interval_no_sessions_auto_day_rest
                else ->
                    throw IllegalArgumentException("Unknown type of time interval.")
            }
        )

        binding.tvDayOfMonth.text =
            DateTimeUtil.formatAsDayOfMonthString(timeIntervalItem.timeInterval.startTimeMillis)
        binding.tvMonthShort.text =
            DateTimeUtil.formatAsMonthShortString(timeIntervalItem.timeInterval.startTimeMillis)
        binding.tvTimeStart.text =
            DateTimeUtil.formatTimeAsString(timeIntervalItem.timeInterval.startTimeMillis)
        binding.tvTimeEnd.text =
            DateTimeUtil.formatTimeAsString(timeIntervalItem.timeInterval.endTimeMillis)
    }
}