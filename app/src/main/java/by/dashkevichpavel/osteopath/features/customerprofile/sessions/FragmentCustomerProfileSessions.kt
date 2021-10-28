package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
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
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionFragmentHelper
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.lang.IllegalArgumentException

class FragmentCustomerProfileSessions :
    Fragment(R.layout.fragment_customer_profile_sessions),
    SessionItemClickListener,
    SessionContextMenuClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerProfileSessionsBinding: FragmentCustomerProfileSessionsBinding? = null
    private val binding get() = fragmentCustomerProfileSessionsBinding!!

    private var adapter = SessionItemAdapter(this, this)
    private var itemDeletionFragmentHelper: ItemDeletionFragmentHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupObservers()
        setupHelpers()
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

        binding.tvEmptyListHint.text = getString(
            R.string.empty_screen_hint,
            getString(R.string.empty_screen_hint_part_sessions)
        )
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

    private fun setupHelpers() {
        itemDeletionFragmentHelper = ItemDeletionFragmentHelper(this,
            viewModel.sessionDeletionHandler, true)
    }

    private fun updateSessionsList(newSessionsList: MutableList<Session>) {
        adapter.setItems(newSessionsList)
        setEmptyScreenHintVisibility(newSessionsList.isEmpty())
    }

    private fun setEmptyScreenHintVisibility(show: Boolean) {
        binding.tvEmptyListHint.isVisible = show
        binding.cvEmptyListHint.isVisible = show
    }

    private fun openSessionScreen(customerId: Long, sessionId: Long) {
        safelyNavigateTo(
            R.id.action_fragmentCustomerProfile_to_fragmentSession,
            FragmentSession.packBundle(customerId, sessionId)
        )
    }

    private fun showSessionContextMenu(session: Session, anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.inflate(R.menu.context_menu_session_listitem)

        if (session.isDone) {
            popupMenu.menu.removeItem(R.id.mi_mark_as_done)
        }
        popupMenu.setForceShowIcon(true)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            onSessionContextMenuItemClick(session, menuItem)
        }
        popupMenu.show()
    }

    private fun onSessionContextMenuItemClick(session: Session, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.mi_mark_as_done -> viewModel.setSessionIsDone(session.id, true)
            R.id.mi_delete ->
                itemDeletionFragmentHelper?.showDialog(
                    itemId = session.id,
                    message = getString(R.string.session_delete_dialog_message)
                )
            else -> return false
        }

        return true
    }

    override fun onSessionItemClick(customerId: Long, sessionId: Long) {
        openSessionScreen(customerId, sessionId)
    }

    override fun onSessionContextMenuClick(session: Session, anchorView: View) {
        showSessionContextMenu(session, anchorView)
    }
}

interface SessionItemClickListener {
    fun onSessionItemClick(customerId: Long, sessionId: Long)
}

interface SessionContextMenuClickListener {
    fun onSessionContextMenuClick(session: Session, anchorView: View)
}