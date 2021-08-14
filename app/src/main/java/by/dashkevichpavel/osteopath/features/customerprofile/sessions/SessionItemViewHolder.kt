package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemSessionBinding
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.helpers.formatDateTimeAsString
import com.google.android.material.card.MaterialCardView

class SessionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListitemSessionBinding.bind(itemView)

    fun bind(session: Session, sessionItemClickListener: SessionItemClickListener) {
        binding.tvDateTime.text = session.dateTime.formatDateTimeAsString()
        binding.tvDone.text = itemView.context.getString(
            if (session.isDone) {
                R.string.session_status_done
            } else {
                R.string.session_status_planned
            }
        )

        var disfunctionsDescriptions = ""
        session.disfunctions.forEach { disfunction ->
            if (disfunction.description.isNotBlank()) {
                disfunctionsDescriptions = disfunctionsDescriptions +
                        if (disfunctionsDescriptions.isNotBlank()) { "\n\n" } else { "" } +
                        disfunction.description
            }
        }

        setContentAndVisibilityOfBlock(binding.tvHeaderDisfunctions, binding.tvDisfunctions,
            disfunctionsDescriptions)
        setContentAndVisibilityOfBlock(binding.tvHeaderPlan, binding.tvPlan, session.plan)
        setContentAndVisibilityOfBlock(binding.tvHeaderBodyCondition, binding.tvBodyCondition,
            session.bodyCondition)

        binding.cvCard.setOnClickListener {
            sessionItemClickListener.onSessionItemClick(session.customerId, session.id)
        }
    }

    private fun setContentAndVisibilityOfBlock(headerView: TextView, textView: TextView, text: String) {
        val visibilityOfBlock = if (text.isBlank()) { View.GONE } else { View.VISIBLE }

        textView.text = text

        headerView.visibility = visibilityOfBlock
        textView.visibility = visibilityOfBlock
    }
}