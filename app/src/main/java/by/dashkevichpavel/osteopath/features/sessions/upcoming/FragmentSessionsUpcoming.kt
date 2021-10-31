package by.dashkevichpavel.osteopath.features.sessions.upcoming

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionsUpcomingBinding
import by.dashkevichpavel.osteopath.features.customerprofile.sessions.SessionItemClickListener
import by.dashkevichpavel.osteopath.features.session.FragmentSession
import by.dashkevichpavel.osteopath.helpers.actionCallPhoneNumber
import by.dashkevichpavel.osteopath.helpers.actionOpenInstagram
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerAction
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerActionHandler
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.model.SessionAndCustomer
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentSessionsUpcoming :
    Fragment(R.layout.fragment_sessions_upcoming),
    ContactToCustomerActionHandler,
    SessionItemClickListener
{
    private val viewModel: SessionsUpcomingViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionsUpcomingBinding: FragmentSessionsUpcomingBinding? = null
    private val binding get() = fragmentSessionsUpcomingBinding!!

    private val adapter = SessionFullItemAdapter(
        this,
        this
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        setupViews(view)
        setupObservers()
        setupEventListeners()
    }

    override fun onResume() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onResume()
        viewModel.startSessionsTableObserving()
    }

    override fun onPause() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPause()
        viewModel.stopSessionsTableObserving()
    }

    override fun onDestroyView() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyView()
        fragmentSessionsUpcomingBinding = null
    }

    private fun setupViews(view: View) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
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
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        viewModel.sessions.observe(viewLifecycleOwner, this::updateSessionsList)
    }

    private fun setupEventListeners() {
        binding.fabAddSession.setOnClickListener { openSessionScreen(0L, 0L) }
    }

    private fun updateSessionsList(newSessions: List<SessionAndCustomer>) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
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

    override fun contactToCustomer(action: ContactToCustomerAction) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        when (action) {
            is ContactToCustomerAction.Call.Phone -> actionCallPhoneNumber(action.phoneNumber)
            is ContactToCustomerAction.Message.Instagram -> actionOpenInstagram(action.userId)
        }
    }

    override fun onSessionItemClick(customerId: Long, sessionId: Long) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        openSessionScreen(customerId, sessionId)
    }
}