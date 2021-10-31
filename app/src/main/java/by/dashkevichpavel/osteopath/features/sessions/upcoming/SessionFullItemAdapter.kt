package by.dashkevichpavel.osteopath.features.sessions.upcoming

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.customerprofile.sessions.SessionItemClickListener
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerActionHandler
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil
import by.dashkevichpavel.osteopath.model.SessionAndCustomer

class SessionFullItemAdapter(
    private val sessionItemClickListener: SessionItemClickListener,
    private val contactToCustomerActionHandler: ContactToCustomerActionHandler
) : RecyclerView.Adapter<SessionFullItemViewHolder>() {
    private val sessionsAndCustomers: MutableList<SessionAndCustomer> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionFullItemViewHolder =
        SessionFullItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_session_full, parent, false)
        )

    override fun onBindViewHolder(holder: SessionFullItemViewHolder, position: Int) {
        holder.bind(
            sessionsAndCustomers[position],
            sessionItemClickListener,
            contactToCustomerActionHandler
        )
    }

    override fun getItemCount(): Int = sessionsAndCustomers.size

    fun setItems(newItems: List<SessionAndCustomer>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(sessionsAndCustomers, newItems))
        sessionsAndCustomers.clear()
        sessionsAndCustomers.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}