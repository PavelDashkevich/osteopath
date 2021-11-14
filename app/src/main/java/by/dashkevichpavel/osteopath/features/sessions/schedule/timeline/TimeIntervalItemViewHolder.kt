package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class TimeIntervalItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(timeIntervalItem: TimeIntervalItem)
}

