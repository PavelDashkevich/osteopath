package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.disfunction.FragmentDisfunction
import by.dashkevichpavel.osteopath.features.session.FragmentSession
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.IllegalArgumentException

class FragmentCustomerProfileSessions :
    Fragment(R.layout.fragment_customer_profile_sessions),
    SessionItemClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private lateinit var rvSessions: RecyclerView
    private var adapter = SessionItemAdapter(this)
    private lateinit var fabAddSession: FloatingActionButton

    /*override fun onAttach(context: Context) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupListeners()
        setupObservers()
    }

    /*override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStart()
    }

    override fun onResume() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroy()
    }

    override fun onDestroyOptionsMenu() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyOptionsMenu()
    }

    override fun onDetach() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDetach()
    }*/

    private fun setupViews(view: View) {
        fabAddSession = view.findViewById(R.id.fab_session_add)
        rvSessions = view.findViewById(R.id.rv_sessions_list)
        rvSessions.layoutManager = LinearLayoutManager(requireContext())
        rvSessions.adapter = adapter
    }

    private fun setupListeners() {
        fabAddSession.setOnClickListener {
            openSessionScreen(viewModel.customer.value?.id ?: 0L, 0L)
        }
    }

    private fun setupObservers() {
        viewModel.sessions.observe(viewLifecycleOwner, this::updateSessionsList)
        viewModel.startListeningForSessionsChanges()
    }

    private fun updateSessionsList(newSessionsList: MutableList<Session>) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        adapter.setItems(newSessionsList)
    }

    private fun openSessionScreen(customerId: Long, sessionId: Long) {
        val bundle = Bundle()
        bundle.putLong(FragmentSession.ARG_KEY_CUSTOMER_ID, customerId)
        bundle.putLong(FragmentSession.ARG_KEY_SESSION_ID, sessionId)

        try {
            findNavController().navigate(R.id.action_fragmentCustomerProfile_to_fragmentSession, bundle)
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