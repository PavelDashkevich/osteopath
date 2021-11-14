package by.dashkevichpavel.osteopath.features.sessions.schedule.calendar

import android.graphics.Typeface
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemCalendarDayBinding

class CalendarDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ListitemCalendarDayBinding.bind(itemView)

    fun bind(
        calendarDayItem: CalendarDayItem,
        calendarDayItemActionListener: CalendarDayItemActionListener
    ) {
        when (calendarDayItem) {
            is CalendarDayItem.Empty -> {
                binding.tvDayOfMonth.isVisible = false
                binding.tvBadge.isVisible = false
                binding.vSelected.isVisible = false
            }
            is CalendarDayItem.Day -> {
                binding.tvDayOfMonth.isVisible = true
                binding.tvDayOfMonth.text = "${calendarDayItem.data.dayOfMonth}"
                binding.tvDayOfMonth.setTextColor(
                    itemView.context.getColor(
                        if (calendarDayItem.data.isWorkingDay)
                            R.color.material_on_surface_emphasis_medium
                        else
                            R.color.material_on_surface_disabled
                    )
                )
                binding.tvDayOfMonth.setTypeface(
                    null,
                    if (calendarDayItem.data.isToday)
                        Typeface.BOLD
                    else
                        Typeface.NORMAL
                )
                binding.tvDayOfMonth.setOnClickListener {
                    calendarDayItemActionListener.onCalendarDayClick(
                        CalendarDayItemAction.Select(calendarDayItem)
                    )
                }

                binding.tvBadge.isVisible = (calendarDayItem.data.badge != 0)
                binding.tvBadge.text = "${calendarDayItem.data.badge}"

                binding.vSelected.isVisible = calendarDayItem.data.isSelected
            }
        }
    }
}