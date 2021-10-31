package by.dashkevichpavel.osteopath.features.sessions

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.databinding.ListitemSessionFullBinding
import by.dashkevichpavel.osteopath.features.customerprofile.sessions.SessionItemClickListener
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerAction
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerActionHandler
import by.dashkevichpavel.osteopath.helpers.formatDateAsDayOfMonthString
import by.dashkevichpavel.osteopath.helpers.formatDateAsMonthShortString
import by.dashkevichpavel.osteopath.helpers.formatTimeAsString
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SessionViewHolderUtil
import by.dashkevichpavel.osteopath.helpers.toStringDelimitedByNewLines
import by.dashkevichpavel.osteopath.model.SessionAndCustomer

class SessionFullItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListitemSessionFullBinding.bind(itemView)

    fun bind(
        sessionAndCustomer: SessionAndCustomer,
        sessionItemClickListener: SessionItemClickListener,
        contactToCustomerActionHandler: ContactToCustomerActionHandler
    ) {
        binding.tvDayOfMonth.text =
            sessionAndCustomer.session.dateTime.formatDateAsDayOfMonthString()
        binding.tvMonthShort.text =
            sessionAndCustomer.session.dateTime.formatDateAsMonthShortString()
        binding.tvTimeStart.text = sessionAndCustomer.session.dateTime.formatTimeAsString()
        binding.tvTimeEnd.text = sessionAndCustomer.session.dateTimeEnd.formatTimeAsString()
        binding.tvCustomerName.text = sessionAndCustomer.customer.name
        SessionViewHolderUtil.setContentAndVisibilityOfBlock(
            binding.tvHeaderDisfunctions,
            binding.tvDisfunctions,
            sessionAndCustomer.session.disfunctions.map {
                    disf -> disf.description
            }.toStringDelimitedByNewLines()
        )
        SessionViewHolderUtil.setContentAndVisibilityOfBlock(binding.tvHeaderPlan,
            binding.tvPlan, sessionAndCustomer.session.plan)
        SessionViewHolderUtil.setContentAndVisibilityOfBlock(binding.tvHeaderBodyCondition,
            binding.tvBodyCondition, sessionAndCustomer.session.bodyCondition)

        binding.ibCall.isVisible = sessionAndCustomer.customer.phone.isNotBlank()
        binding.ibMessage.isVisible = sessionAndCustomer.customer.instagram.isNotBlank()

        if (sessionAndCustomer.customer.phone.isNotBlank()) {
            binding.ibCall.setOnClickListener {
                contactToCustomerActionHandler.contactToCustomer(
                    ContactToCustomerAction.Call.Phone(sessionAndCustomer.customer.phone)
                )
            }
        }

        if (sessionAndCustomer.customer.instagram.isNotBlank()) {
            binding.ibMessage.setOnClickListener {
                contactToCustomerActionHandler.contactToCustomer(
                    ContactToCustomerAction.Message.Instagram(sessionAndCustomer.customer.instagram)
                )
            }
        }

        binding.cvCard.setOnClickListener {
            sessionItemClickListener.onSessionItemClick(
                customerId = sessionAndCustomer.customer.id,
                sessionId = sessionAndCustomer.session.id
            )
        }
    }
}