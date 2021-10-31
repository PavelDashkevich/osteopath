package by.dashkevichpavel.osteopath.features.customerprofile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.dashkevichpavel.osteopath.features.customerprofile.attachments.FragmentCustomerProfileAttachments
import by.dashkevichpavel.osteopath.features.customerprofile.contacts.FragmentCustomerProfileContacts
import by.dashkevichpavel.osteopath.features.customerprofile.disfunctions.FragmentCustomerProfileDisfunctions
import by.dashkevichpavel.osteopath.features.customerprofile.sessions.FragmentCustomerProfileSessions

class CustomerProfilePagesAdapter(hostFragment: Fragment) : FragmentStateAdapter(hostFragment) {
    override fun getItemCount(): Int = NUMBER_OF_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentCustomerProfileContacts()
            1 -> FragmentCustomerProfileDisfunctions()
            2 -> FragmentCustomerProfileSessions()
            3 -> FragmentCustomerProfileAttachments()
            else -> FragmentCustomerProfileContacts()
        }
    }

    private companion object {
        private const val NUMBER_OF_PAGES = 4
    }
}
