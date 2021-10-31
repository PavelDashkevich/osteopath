package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemSessionBinding
import by.dashkevichpavel.osteopath.helpers.formatDateAsString
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.helpers.formatDateTimeAsString
import by.dashkevichpavel.osteopath.helpers.formatTimeAsString
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SessionViewHolderUtil
import by.dashkevichpavel.osteopath.helpers.toStringDelimitedByNewLines
import com.google.android.material.card.MaterialCardView

class SessionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListitemSessionBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
        session: Session,
        sessionItemClickListener: SessionItemClickListener,
        sessionContextMenuClickListener: SessionContextMenuClickListener
    ) {
        binding.tvDate.text = session.dateTime.formatDateAsString()
        binding.tvTime.text =
            "${session.dateTime.formatTimeAsString()}-${session.dateTimeEnd.formatTimeAsString()}"
        binding.tvDone.text = itemView.context.getString(
            if (session.isDone) {
                R.string.session_status_done
            } else {
                R.string.session_status_planned
            }
        )

        SessionViewHolderUtil.setContentAndVisibilityOfBlock(
            binding.tvHeaderDisfunctions,
            binding.tvDisfunctions,
            session.disfunctions.map { disf -> disf.description }.toStringDelimitedByNewLines()
        )
        SessionViewHolderUtil.setContentAndVisibilityOfBlock(binding.tvHeaderPlan,
            binding.tvPlan, session.plan)
        SessionViewHolderUtil.setContentAndVisibilityOfBlock(binding.tvHeaderBodyCondition,
            binding.tvBodyCondition, session.bodyCondition)

        binding.cvCard.setOnClickListener {
            sessionItemClickListener.onSessionItemClick(session.customerId, session.id)
        }

        binding.ibContextActions.setOnClickListener {
            sessionContextMenuClickListener.onSessionContextMenuClick(session, binding.ibContextActions)
        }
    }
}