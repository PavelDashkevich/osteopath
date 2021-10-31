package by.dashkevichpavel.osteopath.features.sessions

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.dashkevichpavel.osteopath.features.sessions.recent.FragmentSessionsRecent
import by.dashkevichpavel.osteopath.features.sessions.schedule.FragmentSessionsSchedule
import by.dashkevichpavel.osteopath.features.sessions.upcoming.FragmentSessionsUpcoming

class SessionsTabsAdapter(hostFragment: Fragment) : FragmentStateAdapter(hostFragment) {
    override fun getItemCount(): Int = NUMBER_OF_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            UPCOMING_TAB_POSITION -> FragmentSessionsUpcoming()
            SCHEDULE_TAB_POSITION -> FragmentSessionsSchedule()
            RECENT_TAB_POSITION -> FragmentSessionsRecent()
            else -> FragmentSessionsUpcoming()
        }
    }

    companion object {
        private const val NUMBER_OF_PAGES = 3
        const val UPCOMING_TAB_POSITION = 0
        const val SCHEDULE_TAB_POSITION = 1
        const val RECENT_TAB_POSITION = 2
    }
}