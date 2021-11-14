package by.dashkevichpavel.osteopath.features.sessions.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionsUpcomingBinding
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemActionListener
import by.dashkevichpavel.osteopath.features.session.FragmentSession
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemAction
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeIntervalItemAdapter
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeIntervalItemSession
import by.dashkevichpavel.osteopath.helpers.actionCallPhoneNumber
import by.dashkevichpavel.osteopath.helpers.actionOpenInstagram
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerAction
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentSessionsUpcoming :
    Fragment(R.layout.fragment_sessions_upcoming),
    TimeIntervalItemActionListener {
    private val viewModel: SessionsUpcomingViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionsUpcomingBinding: FragmentSessionsUpcomingBinding? = null
    private val binding get() = fragmentSessionsUpcomingBinding!!

    private val adapter = TimeIntervalItemAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupObservers()
        setupEventListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startSessionsTableObserving()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopSessionsTableObserving()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSessionsUpcomingBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSessionsUpcomingBinding = FragmentSessionsUpcomingBinding.bind(view)

        binding.rvSessionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSessionsList.adapter = adapter
        binding.rvSessionsList.addItemDecoration(SpaceItemDecoration())

        binding.tvEmptyListHint.text = getString(
            R.string.empty_screen_hint,
            getString(R.string.empty_screen_hint_part_sessions)
        )
    }

    private fun setupObservers() {
        viewModel.sessions.observe(viewLifecycleOwner, this::updateSessionsList)
    }

    private fun setupEventListeners() {
        binding.fabAddSession.setOnClickListener { openSessionScreen(0L, 0L) }
    }

    private fun updateSessionsList(newSessions: List<TimeIntervalItemSession>) {
        adapter.setItems(newSessions)
        setEmptyScreenHintVisibility(newSessions.isEmpty())
    }

    private fun setEmptyScreenHintVisibility(show: Boolean) {
        binding.tvEmptyListHint.isVisible = show
        binding.cvEmptyListHint.isVisible = show
    }

    private fun openSessionScreen(customerId: Long, sessionId: Long) {
        safelyNavigateTo(
            R.id.action_fragmentSessions_to_fragmentSession,
            FragmentSession.packBundle(customerId, sessionId, true)
        )
    }

    override fun onTimeIntervalItemClick(timeIntervalItemAction: TimeIntervalItemAction) {
        when (timeIntervalItemAction) {
            is TimeIntervalItemAction.SessionAction.ContactToCustomer ->
                when (timeIntervalItemAction.contactToCustomerAction) {
                    is ContactToCustomerAction.Call.Phone ->
                        actionCallPhoneNumber(timeIntervalItemAction.contactToCustomerAction.phoneNumber)
                    is ContactToCustomerAction.Message.Instagram ->
                        actionOpenInstagram(timeIntervalItemAction.contactToCustomerAction.userId)
                }
            is TimeIntervalItemAction.SessionAction.Open ->
                openSessionScreen(
                    timeIntervalItemAction.customerId,
                    timeIntervalItemAction.sessionId
                )
            else -> { }
        }
    }
}