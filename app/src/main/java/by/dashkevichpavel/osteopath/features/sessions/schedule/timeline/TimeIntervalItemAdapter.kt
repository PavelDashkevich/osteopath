package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemActionListener
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil
import java.lang.IllegalArgumentException

class TimeIntervalItemAdapter(
    private val timeIntervalItemActionListener: TimeIntervalItemActionListener
) : RecyclerView.Adapter<TimeIntervalItemViewHolder>() {
    private val items: MutableList<TimeIntervalItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeIntervalItemViewHolder {
        if (viewType !in ViewTypes.values().indices) {
            throw IllegalArgumentException("Unknown type of view.")
        }

        val viewHolder: TimeIntervalItemViewHolder = when (ViewTypes.values()[viewType]) {
            ViewTypes.AVAILABLE_TO_SCHEDULE ->
                TimeIntervalItemAvailableToScheduleViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.listitem_time_interval_available_to_schedule,
                            parent, false),
                    timeIntervalItemActionListener
                )
            ViewTypes.NO_SESSIONS_PERIOD_AUTO ->
                TimeIntervalItemNoSessionsPeriodAutoViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.listitem_no_sessions_period_auto,
                            parent, false)
                )
            ViewTypes.NO_SESSIONS_PERIOD_MANUAL ->
                TimeIntervalItemNoSessionsPeriodViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.listitem_no_sessions_period_manual,
                            parent, false),
                    timeIntervalItemActionListener
                )
            ViewTypes.SESSION ->
                TimeIntervalItemSessionViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.listitem_session_full, parent, false),
                    timeIntervalItemActionListener
                )
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: TimeIntervalItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is TimeIntervalItemAvailableToSchedule -> ViewTypes.AVAILABLE_TO_SCHEDULE
            is TimeIntervalItemNoSessionsAuto -> ViewTypes.NO_SESSIONS_PERIOD_AUTO
            is TimeIntervalItemNoSessionsManual -> ViewTypes.NO_SESSIONS_PERIOD_MANUAL
            is TimeIntervalItemSession -> ViewTypes.SESSION
            else -> throw IllegalArgumentException("Type of time interval not found.")
        }.ordinal

    fun setItems(newItems: List<TimeIntervalItem>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(items, newItems))
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }

    private enum class ViewTypes {
        AVAILABLE_TO_SCHEDULE,
        NO_SESSIONS_PERIOD_AUTO,
        NO_SESSIONS_PERIOD_MANUAL,
        SESSION
    }
}