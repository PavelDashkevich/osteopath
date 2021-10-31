package by.dashkevichpavel.osteopath.features.sessions

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionsBinding
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfilePagesAdapter
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class FragmentSessions : Fragment(R.layout.fragment_sessions) {
    private val viewModel: SessionsViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionsBinding: FragmentSessionsBinding? = null
    private val binding get() = fragmentSessionsBinding!!

    private val tabDrawables = mapOf(
        SessionsTabsAdapter.UPCOMING_TAB_POSITION to R.drawable.ic_baseline_pending_actions_24,
        SessionsTabsAdapter.SCHEDULE_TAB_POSITION to R.drawable.ic_baseline_edit_calendar_24,
        SessionsTabsAdapter.RECENT_TAB_POSITION to R.drawable.ic_baseline_history_24
    )
    private val tabTexts = mapOf(
        SessionsTabsAdapter.UPCOMING_TAB_POSITION to R.string.screen_sessions_tab_upcoming_text,
        SessionsTabsAdapter.SCHEDULE_TAB_POSITION to R.string.screen_sessions_tab_recent_schedule,
        SessionsTabsAdapter.RECENT_TAB_POSITION to R.string.screen_sessions_tab_recent_text
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> binding.dlDrawerLayout.open()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSessionsBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSessionsBinding = FragmentSessionsBinding.bind(view)
        setupToolbar(binding.tbActions)
        binding.lNavMenu.nvMain.setupWithNavController(findNavController())
        setupViewPager()
        setupTabLayout()
    }

    private fun setupObservers() {
        viewModel.test = 0
    }

    private fun setupViewPager() {
        binding.vpViewPager.adapter = SessionsTabsAdapter(this)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tlSessionTabs, binding.vpViewPager) { tab, pos ->
            tab.icon = AppCompatResources.getDrawable(
                requireContext(),
                tabDrawables[pos] ?: R.drawable.ic_baseline_pest_control_24
            )
            tab.text = getString(tabTexts[pos] ?: R.string.screen_sessions_tab_error_text)
        }
            .attach()
    }
}