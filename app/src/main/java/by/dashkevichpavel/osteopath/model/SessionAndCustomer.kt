package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable

data class SessionAndCustomer(
    var session: Session,
    var customer: Customer
) : DiffUtilComparable {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is SessionAndCustomer) return false

        return session.isTheSameItemAs(item.session) && customer.isTheSameItemAs(item.customer)
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item !is SessionAndCustomer) return false

        return session.contentsTheSameAs(item.session) && customer.isTheSameItemAs(item.customer)
    }
}
