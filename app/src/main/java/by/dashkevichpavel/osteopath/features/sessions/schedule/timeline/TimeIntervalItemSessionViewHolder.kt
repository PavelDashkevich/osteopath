package by.dashkevichpavel.osteopath.features.sessions.schedule.timeline

import android.view.View
import androidx.core.view.isVisible
import by.dashkevichpavel.osteopath.databinding.ListitemSessionFullBinding
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemAction
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemActionListener
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerAction
import by.dashkevichpavel.osteopath.helpers.formatDateAsDayOfMonthString
import by.dashkevichpavel.osteopath.helpers.formatDateAsMonthShortString
import by.dashkevichpavel.osteopath.helpers.formatTimeAsString
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SessionViewHolderUtil
import by.dashkevichpavel.osteopath.helpers.toStringDelimitedByNewLines

class TimeIntervalItemSessionViewHolder(
    itemView: View,
    private val timeIntervalItemActionListener: TimeIntervalItemActionListener
) :
    TimeIntervalItemViewHolder(itemView) {
    private val binding = ListitemSessionFullBinding.bind(itemView)

    override fun bind(timeIntervalItem: TimeIntervalItem) {
        if (timeIntervalItem !is TimeIntervalItemSession) return

        val sessionAndCustomer =
            (timeIntervalItem.timeInterval as TimeInterval.SessionTime).sessionAndCustomer

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
                timeIntervalItemActionListener.onTimeIntervalItemClick(
                    TimeIntervalItemAction.SessionAction.ContactToCustomer(
                        ContactToCustomerAction.Call.Phone(sessionAndCustomer.customer.phone)
                    )
                )
            }
        }

        if (sessionAndCustomer.customer.instagram.isNotBlank()) {
            binding.ibMessage.setOnClickListener {
                timeIntervalItemActionListener.onTimeIntervalItemClick(
                    TimeIntervalItemAction.SessionAction.ContactToCustomer(
                        ContactToCustomerAction.Message.Instagram(
                            sessionAndCustomer.customer.instagram
                        )
                    )
                )
            }
        }

        binding.cvCard.setOnClickListener {
            timeIntervalItemActionListener.onTimeIntervalItemClick(
                TimeIntervalItemAction.SessionAction.Open(
                    sessionAndCustomer.customer.id,
                    sessionAndCustomer.session.id
                )
            )
        }
    }
}