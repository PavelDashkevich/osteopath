package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import android.view.View
import by.dashkevichpavel.osteopath.databinding.ListitemTimeIntervalAvailableToScheduleBinding
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemAction
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemActionListener
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil

class TimeIntervalItemAvailableToScheduleViewHolder(
    itemView: View,
    private val timeIntervalItemActionListener: TimeIntervalItemActionListener
    ) : TimeIntervalItemViewHolder(itemView) {
    private val binding = ListitemTimeIntervalAvailableToScheduleBinding.bind(itemView)

    override fun bind(timeIntervalItem: TimeIntervalItem) {
        if (timeIntervalItem !is TimeIntervalItemAvailableToSchedule) return

        binding.tvDayOfMonth.text =
            DateTimeUtil.formatAsDayOfMonthString(timeIntervalItem.timeInterval.startTimeMillis)
        binding.tvMonthShort.text =
            DateTimeUtil.formatAsMonthShortString(timeIntervalItem.timeInterval.startTimeMillis)
        binding.tvTimeStart.text =
            DateTimeUtil.formatTimeAsString(timeIntervalItem.timeInterval.startTimeMillis)
        binding.tvTimeEnd.text =
            DateTimeUtil.formatTimeAsString(timeIntervalItem.timeInterval.endTimeMillis)

        binding.btnAddSession.setOnClickListener {
            timeIntervalItemActionListener.onTimeIntervalItemClick(
                TimeIntervalItemAction.AvailableToScheduleAction.AddSession(
                    timeIntervalItem.timeInterval.startTimeMillis,
                    timeIntervalItem.timeInterval.endTimeMillis
                )
            )
        }
        binding.btnAddNoSessionsPeriod.setOnClickListener {
            timeIntervalItemActionListener.onTimeIntervalItemClick(
                TimeIntervalItemAction.AvailableToScheduleAction.AddNoSessionsPeriod(
                    timeIntervalItem.timeInterval.startTimeMillis,
                    timeIntervalItem.timeInterval.endTimeMillis
                )
            )
        }
    }
}