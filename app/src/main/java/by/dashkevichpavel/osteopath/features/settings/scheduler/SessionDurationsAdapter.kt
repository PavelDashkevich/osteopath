package by.dashkevichpavel.osteopath.features.settings.scheduler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil

class SessionDurationsAdapter(
    private val sessionDurationItemActionListener: SessionDurationItemActionListener
) : RecyclerView.Adapter<SessionDurationViewHolder>() {
    private val items: MutableList<SessionDurationItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionDurationViewHolder =
        SessionDurationViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_session_duration, parent, false)
        )

    override fun onBindViewHolder(holder: SessionDurationViewHolder, position: Int) {
        holder.bind(items[position], sessionDurationItemActionListener)
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<SessionDurationItem>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(items, newItems))
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}