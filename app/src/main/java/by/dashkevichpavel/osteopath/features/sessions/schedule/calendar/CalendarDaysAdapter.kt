package by.dashkevichpavel.osteopath.features.sessions.schedule.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil

class CalendarDaysAdapter(
    private val calendarDayItemActionListener: CalendarDayItemActionListener
) : RecyclerView.Adapter<CalendarDayViewHolder>() {
    private val items: MutableList<CalendarDayItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder =
        CalendarDayViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_calendar_day, parent, false)
        )

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(items[position], calendarDayItemActionListener)
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<CalendarDayItem>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(items, newItems))
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}