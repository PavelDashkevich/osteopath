package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.model.formatDateTimeAsString

class SessionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvDateTime: TextView = itemView.findViewById(R.id.tv_date_time)
    private val tvDone: TextView = itemView.findViewById(R.id.tv_done)
    private val tvHeaderDisfunctions: TextView = itemView.findViewById(R.id.tv_header_disfunctions)
    private val tvDisfunctions: TextView = itemView.findViewById(R.id.tv_disfunctions)
    private val tvHeaderPlan: TextView = itemView.findViewById(R.id.tv_header_plan)
    private val tvPlan: TextView = itemView.findViewById(R.id.tv_plan)
    private val tvHeaderBodyCondition: TextView = itemView.findViewById(R.id.tv_header_body_condition)
    private val tvBodyCondition: TextView = itemView.findViewById(R.id.tv_body_condition)

    fun bind(session: Session) {
        tvDateTime.text = session.dateTime.formatDateTimeAsString()
        tvDone.text = itemView.context.getString(
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

        setContentAndVisibilityOfBlock(tvHeaderDisfunctions, tvDisfunctions, disfunctionsDescriptions)
        setContentAndVisibilityOfBlock(tvHeaderPlan, tvPlan, session.plan)
        setContentAndVisibilityOfBlock(tvHeaderBodyCondition, tvBodyCondition, session.bodyCondition)
    }

    private fun setContentAndVisibilityOfBlock(headerView: TextView, textView: TextView, text: String) {
        val visibilityOfBlock = if (text.isBlank()) { View.GONE } else { View.VISIBLE }

        textView.text = text

        headerView.visibility = visibilityOfBlock
        textView.visibility = visibilityOfBlock
    }
}