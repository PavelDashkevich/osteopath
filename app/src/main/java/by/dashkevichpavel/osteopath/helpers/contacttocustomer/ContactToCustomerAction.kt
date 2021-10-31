package by.dashkevichpavel.osteopath.helpers.contacttocustomer

sealed class ContactToCustomerAction {
    sealed class Call : ContactToCustomerAction() {
        class Phone(var phoneNumber: String) : Call()
    }
    sealed class Message : ContactToCustomerAction() {
        class Instagram(var userId: String) : Message()
    }
}

interface ContactToCustomerActionHandler {
    fun contactToCustomer(action: ContactToCustomerAction)
}