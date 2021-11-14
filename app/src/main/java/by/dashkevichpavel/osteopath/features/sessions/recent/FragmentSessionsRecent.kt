package by.dashkevichpavel.osteopath.features.sessions.recent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionsRecentBinding
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

class FragmentSessionsRecent :
    Fragment(R.layout.fragment_sessions_recent),
    TimeIntervalItemActionListener {
    private val viewModel: SessionsRecentViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionsRecentBinding: FragmentSessionsRecentBinding? = null
    private val binding get() = fragmentSessionsRecentBinding!!

    private val adapter = TimeIntervalItemAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupObservers()
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
        fragmentSessionsRecentBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSessionsRecentBinding = FragmentSessionsRecentBinding.bind(view)

        binding.rvSessionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSessionsList.adapter = adapter
        binding.rvSessionsList.addItemDecoration(SpaceItemDecoration())
    }

    private fun setupObservers() {
        viewModel.sessions.observe(viewLifecycleOwner, this::updateSessionsList)
    }

    private fun updateSessionsList(newSessions: List<TimeIntervalItemSession>) {
        adapter.setItems(newSessions)
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