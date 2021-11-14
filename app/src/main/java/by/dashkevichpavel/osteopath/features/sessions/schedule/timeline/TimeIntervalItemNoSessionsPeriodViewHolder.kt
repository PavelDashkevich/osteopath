package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import android.view.View
import by.dashkevichpavel.osteopath.databinding.ListitemNoSessionsPeriodManualBinding
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemAction
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemActionListener
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil

class TimeIntervalItemNoSessionsPeriodViewHolder(
    itemView: View,
    private val timeIntervalItemActionListener: TimeIntervalItemActionListener
) :
    TimeIntervalItemViewHolder(itemView) {
    private val binding = ListitemNoSessionsPeriodManualBinding.bind(itemView)

    override fun bind(timeIntervalItem: TimeIntervalItem) {
        if (timeIntervalItem !is TimeIntervalItemNoSessionsManual) return

        val timeInterval = (timeIntervalItem.timeInterval as TimeInterval.NoSessions.Manual)

        binding.tvDayOfMonth.text =
            DateTimeUtil.formatAsDayOfMonthString(timeInterval.startTimeMillis)
        binding.tvMonthShort.text =
            DateTimeUtil.formatAsMonthShortString(timeInterval.startTimeMillis)
        binding.tvTimeStart.text = DateTimeUtil.formatTimeAsString(timeInterval.startTimeMillis)
        binding.tvTimeEnd.text = DateTimeUtil.formatTimeAsString(timeInterval.endTimeMillis)

        binding.cvCard.setOnClickListener {
            timeIntervalItemActionListener.onTimeIntervalItemClick(
                TimeIntervalItemAction.NoSessionsPeriodAction.Open(
                    timeInterval.noSessionsPeriod.id
                )
            )
        }
    }
}