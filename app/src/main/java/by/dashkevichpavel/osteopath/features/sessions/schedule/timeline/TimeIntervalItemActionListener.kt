package by.dashkevichpavel.osteopath.features.sessions

import android.view.View
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerAction
import by.dashkevichpavel.osteopath.model.Session

interface TimeIntervalItemActionListener {
    fun onTimeIntervalItemClick(timeIntervalItemAction: TimeIntervalItemAction)
}

sealed class TimeIntervalItemAction {
    sealed class SessionAction : TimeIntervalItemAction() {
        class Open(val customerId: Long, val sessionId: Long) : SessionAction()
        class ContactToCustomer(val contactToCustomerAction: ContactToCustomerAction) :
            SessionAction()
        class OpenContextMenu(val session: Session, val anchorView: View) : SessionAction()
    }
    sealed class NoSessionsPeriodAction : TimeIntervalItemAction() {
        class Open(val noSessionsPeriodId: Long) : NoSessionsPeriodAction()
    }
    sealed class AvailableToScheduleAction : TimeIntervalItemAction() {
        class AddSession(val dateTimeStart: Long, val dateTimeEnd: Long) :
            AvailableToScheduleAction()
        class AddNoSessionsPeriod(val dateTimeStart: Long, val dateTimeEnd: Long) :
            AvailableToScheduleAction()
    }
}