package by.dashkevichpavel.osteopath.features.settings.scheduler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.databinding.ListitemSessionDurationBinding
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil

class SessionDurationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ListitemSessionDurationBinding.bind(itemView)

    fun bind(sessionDurationItem: SessionDurationItem, itemActionListener: SessionDurationItemActionListener) {
        binding.tvDuration.text =
            DateTimeUtil.formatTimeAsDurationString(itemView.context, sessionDurationItem.duration)
        binding.ibDelete.setOnClickListener {
            itemActionListener.onSessionDurationClick(
                SessionDurationItemAction.Delete(sessionDurationItem.duration)
            )
        }
    }
}

