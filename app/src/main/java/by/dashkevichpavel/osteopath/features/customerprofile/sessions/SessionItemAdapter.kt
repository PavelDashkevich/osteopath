package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemActionListener
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil
import by.dashkevichpavel.osteopath.model.Session

class SessionItemAdapter(
    private val timeIntervalItemActionListener: TimeIntervalItemActionListener,
    private val sessionContextMenuClickListener: SessionContextMenuClickListener
) : RecyclerView.Adapter<SessionItemViewHolder>() {
    private val sessions: MutableList<Session> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionItemViewHolder =
        SessionItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_session, parent, false)
        )

    override fun onBindViewHolder(holder: SessionItemViewHolder, position: Int) {
        holder.bind(sessions[position], timeIntervalItemActionListener, sessionContextMenuClickListener)
    }

    override fun getItemCount(): Int = sessions.size

    fun setItems(newItems: List<Session>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(sessions, newItems))
        sessions.clear()
        sessions.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}