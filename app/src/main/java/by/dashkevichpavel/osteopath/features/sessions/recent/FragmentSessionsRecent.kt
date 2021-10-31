package by.dashkevichpavel.osteopath.features.sessions.recent

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionsRecentBinding
import by.dashkevichpavel.osteopath.databinding.FragmentSessionsUpcomingBinding
import by.dashkevichpavel.osteopath.features.customerprofile.sessions.SessionItemClickListener
import by.dashkevichpavel.osteopath.features.session.FragmentSession
import by.dashkevichpavel.osteopath.features.sessions.SessionFullItemAdapter
import by.dashkevichpavel.osteopath.features.sessions.upcoming.SessionsUpcomingViewModel
import by.dashkevichpavel.osteopath.helpers.actionCallPhoneNumber
import by.dashkevichpavel.osteopath.helpers.actionOpenInstagram
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerAction
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerActionHandler
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.model.SessionAndCustomer
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentSessionsRecent :
    Fragment(R.layout.fragment_sessions_recent),
    ContactToCustomerActionHandler,
    SessionItemClickListener {
    private val viewModel: SessionsRecentViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionsRecentBinding: FragmentSessionsRecentBinding? = null
    private val binding get() = fragmentSessionsRecentBinding!!

    private val adapter = SessionFullItemAdapter(
        this,
        this
    )

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

    private fun updateSessionsList(newSessions: List<SessionAndCustomer>) {
        adapter.setItems(newSessions)
    }

    private fun openSessionScreen(customerId: Long, sessionId: Long) {
        safelyNavigateTo(
            R.id.action_fragmentSessions_to_fragmentSession,
            FragmentSession.packBundle(customerId, sessionId, true)
        )
    }

    override fun contactToCustomer(action: ContactToCustomerAction) {
        when (action) {
            is ContactToCustomerAction.Call.Phone -> actionCallPhoneNumber(action.phoneNumber)
            is ContactToCustomerAction.Message.Instagram -> actionOpenInstagram(action.userId)
        }
    }

    override fun onSessionItemClick(customerId: Long, sessionId: Long) {
        openSessionScreen(customerId, sessionId)
    }
}