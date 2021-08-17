package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerProfileSessionsBinding
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.session.FragmentSession
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.lang.IllegalArgumentException

class FragmentCustomerProfileSessions :
    Fragment(R.layout.fragment_customer_profile_sessions),
    SessionItemClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerProfileSessionsBinding: FragmentCustomerProfileSessionsBinding? = null
    private val binding get() = fragmentCustomerProfileSessionsBinding!!

    private var adapter = SessionItemAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupEventListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCustomerProfileSessionsBinding = null
    }

    private fun setupViews(view: View) {
        fragmentCustomerProfileSessionsBinding = FragmentCustomerProfileSessionsBinding.bind(view)

        binding.rvSessionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSessionsList.addItemDecoration(SpaceItemDecoration())
        binding.rvSessionsList.adapter = adapter
    }

    private fun setupEventListeners() {
        binding.fabAddSession.setOnClickListener {
            openSessionScreen(viewModel.customer.value?.id ?: 0L, 0L)
        }
    }

    private fun setupObservers() {
        viewModel.sessions.observe(viewLifecycleOwner, this::updateSessionsList)
        viewModel.startListeningForSessionsChanges()
    }

    private fun updateSessionsList(newSessionsList: MutableList<Session>) {
        adapter.setItems(newSessionsList)
    }

    private fun openSessionScreen(customerId: Long, sessionId: Long) {
        try {
            findNavController().navigate(
                R.id.action_fragmentCustomerProfile_to_fragmentSession,
                FragmentSession.packBundle(customerId, sessionId)
            )
        } catch (e: IllegalArgumentException) {
            Log.d("OsteoApp", "openDisfunctionScreen(): exception: ${e.message}")
        }
    }

    override fun onSessionItemClick(customerId: Long, sessionId: Long) {
        openSessionScreen(customerId, sessionId)
    }
}

interface SessionItemClickListener {
    fun onSessionItemClick(customerId: Long, sessionId: Long)
}