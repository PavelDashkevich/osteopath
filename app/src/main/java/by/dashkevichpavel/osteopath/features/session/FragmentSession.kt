package by.dashkevichpavel.osteopath.features.session

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionBinding
import by.dashkevichpavel.osteopath.BackClickHandler
import by.dashkevichpavel.osteopath.BackClickListener
import by.dashkevichpavel.osteopath.features.customerlist.FragmentCustomerList
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.features.pickers.FragmentDatePicker
import by.dashkevichpavel.osteopath.features.pickers.FragmentTimePicker
import by.dashkevichpavel.osteopath.features.selectdisfunctions.FragmentSelectDisfunctions
import by.dashkevichpavel.osteopath.helpers.*
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionFragmentHelper
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesFragmentHelper
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.util.*

class FragmentSession :
    Fragment(R.layout.fragment_session),
    DisfunctionDeleteClickListener,
    BackClickListener {
    private val viewModel: SessionViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionBinding: FragmentSessionBinding? = null
    private val binding get() = fragmentSessionBinding!!

    private var backClickHandler: BackClickHandler? = null
    private lateinit var adapter: DisfunctionInSessionAdapter
    private lateinit var saveChangesHelper: SaveChangesFragmentHelper
    private var contextMenu: Menu? = null
    private var itemDeletionFragmentHelper: ItemDeletionFragmentHelper? = null

    // fragment args
    var argSessionId: Long? = null
    var argCustomerId: Long? = null
    var argShowCustomer: Boolean = false
    var argDefaultStartDateTime: Long? = null
    var argDefaultEndDateTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        argSessionId = arguments?.getLong(ARG_KEY_SESSION_ID)
        argCustomerId = arguments?.getLong(ARG_KEY_CUSTOMER_ID)
        argShowCustomer = arguments?.getBoolean(ARG_KEY_SHOW_CUSTOMER) ?: false
        argDefaultStartDateTime = arguments?.getLong(ARG_KEY_DEFAULT_START_DATETIME)
        argDefaultEndDateTime = arguments?.getLong(ARG_KEY_DEFAULT_END_DATETIME)

        viewModel.selectSession(
            argSessionId ?: 0L,
            argCustomerId ?: 0L,
            argDefaultStartDateTime ?: 0L,
            argDefaultEndDateTime ?: 0L
        )

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupObservers()
        setupHelpers()
        setupEventListeners()
        setupFragmentResultListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
        contextMenu = menu
        onChangeSessionId(viewModel.getSessionId())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.saveChangesHelper.finishEditing()
            R.id.mi_cancel -> viewModel.saveChangesHelper.cancelEditing()
            R.id.mi_delete ->
                itemDeletionFragmentHelper?.showDialog(
                    viewModel.getSessionId(),
                    getString(R.string.session_delete_dialog_message)
                )
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
        fragmentSessionBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSessionBinding = FragmentSessionBinding.bind(view)
        setupToolbar(binding.lToolbar.tbActions)
        setupRecyclerView()

        binding.tilCustomer.isVisible = argShowCustomer
        binding.etCustomer.isVisible = argShowCustomer
        binding.ibPickCustomer.isVisible = argShowCustomer
    }

    private fun setupRecyclerView() {
        adapter = DisfunctionInSessionAdapter(this)
        binding.rvDisfunctions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDisfunctions.addItemDecoration(SpaceItemDecoration())
        binding.rvDisfunctions.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.session.observe(viewLifecycleOwner, ::onChangeSession)
        viewModel.disfunctions.observe(viewLifecycleOwner, ::onChangeDisfunctions)
        viewModel.sessionDateTime.observe(viewLifecycleOwner, ::onChangeDateTime)
        viewModel.sessionDateTimeEnd.observe(viewLifecycleOwner, ::onChangeDateTimeEnd)
        viewModel.addDisfunctionActionEnabled.observe(viewLifecycleOwner,
            ::onChangeAddDisfunctionActionAccessibility)
        viewModel.currentSessionId.observe(viewLifecycleOwner, ::onChangeSessionId)
        viewModel.customerName.observe(viewLifecycleOwner, ::onChangeCustomerName)
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
        itemDeletionFragmentHelper = ItemDeletionFragmentHelper(this,
            viewModel.sessionDeletionHandler, false)
    }

    private fun setupEventListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        binding.ibPickCustomer.setOnClickListener {
            safelyNavigateTo(
                R.id.action_fragmentSession_to_fragmentCustomerList,
                FragmentCustomerList.createArgsBundle(true)
            )
            FragmentCustomerList
        }

        binding.etDate.setOnClickListener {
            FragmentDatePicker.show(
                childFragmentManager,
                KEY_DATE_PICKER_SESSION_DATE,
                viewModel.getSessionDefaultDateTimeInMillis()
            )
        }

        binding.etTime.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                KEY_TIME_PICKER_SESSION_TIME_START,
                viewModel.getSessionDefaultDateTimeInMillis()
            )
        }

        binding.etTimeEnd.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                KEY_TIME_PICKER_SESSION_TIME_END,
                viewModel.getSessionDefaultDateTimeEndInMillis()
            )
        }

        binding.ibAddDisfunction.setOnClickListener {
            safelyNavigateTo(
                R.id.action_fragmentSession_to_fragmentSelectDisfunctions,
                FragmentSelectDisfunctions.createBundle(
                    viewModel.getCustomerId(),
                    viewModel.getSelectedDisfunctionsIds()
                )
            )
        }

        binding.etPlan.doOnTextChanged { text, _, _, _ ->
            viewModel.setPlan(text.toString())
        }

        binding.etBodyConditions.doOnTextChanged { text, _, _, _ ->
            viewModel.setBodyCondition(text.toString())
        }

        binding.smDone.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsDone(isChecked)
        }
    }

    private fun setupFragmentResultListeners() {
        childFragmentManager.setFragmentResultListener(
            FragmentDatePicker.KEY_RESULT,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setSessionDate(FragmentDatePicker.extractTimeInMillis(bundle))
        }

        childFragmentManager.setFragmentResultListener(
            KEY_TIME_PICKER_SESSION_TIME_START,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setSessionTime(FragmentTimePicker.extractTimeInMillis(bundle))
        }

        childFragmentManager.setFragmentResultListener(
            KEY_TIME_PICKER_SESSION_TIME_END,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setSessionTimeEnd(FragmentTimePicker.extractTimeInMillis(bundle))
        }

        setFragmentResultListener(FragmentSelectDisfunctions.KEY_RESULT) { _, bundle ->
            viewModel.setSelectedDisfunctionsIds(
                FragmentSelectDisfunctions.extractBundle(bundle)
            )
        }

        setFragmentResultListener(FragmentCustomerList.KEY_RESULT) { _, bundle ->
            viewModel.setCustomer(FragmentCustomerList.extractResultBundle(bundle))
        }
    }

    private fun onChangeSession(session: Session) {
        requireActivity().title = if (session.id == 0L) getString(R.string.header_new_session) else ""
        binding.etPlan.text = session.plan.toEditable()
        binding.etBodyConditions.text = session.bodyCondition.toEditable()
        binding.smDone.isChecked = session.isDone
    }

    private fun onChangeDisfunctions(disfunctions: MutableList<Disfunction>) {
        adapter.setItems(disfunctions)
    }

    private fun onChangeDateTime(date: Date) {
        binding.etDate.text = date.formatDateAsEditable()
        binding.etTime.text = date.formatTimeAsEditable()
    }

    private fun onChangeDateTimeEnd(date: Date) {
        binding.etTimeEnd.text = date.formatTimeAsEditable()
    }

    private fun onChangeAddDisfunctionActionAccessibility(isEnabled: Boolean) {
        binding.ibAddDisfunction.isEnabled = !isEnabled
    }

    private fun onChangeSessionId(sessionId: Long) {
        binding.lToolbar.tbActions.menu.findItem(R.id.mi_delete)?.isVisible = (sessionId != 0L)
        binding.ibPickCustomer.isEnabled = (sessionId == 0L)
    }

    private fun onChangeCustomerName(customerName: String) {
        binding.etCustomer.text= customerName.toEditable()
    }

    override fun onDisfunctionDeleteClick(disfunctionId: Long) {
        viewModel.deleteDisfunction(disfunctionId)
    }

    override fun onBackClick(): Boolean {
        viewModel.saveChangesHelper.finishEditing()
        return true
    }

    companion object {
        private const val ARG_KEY_SESSION_ID = "ARG_KEY_SESSION_ID"
        private const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
        private const val ARG_KEY_SHOW_CUSTOMER = "ARG_KEY_SHOW_CUSTOMER"
        private const val ARG_KEY_DEFAULT_START_DATETIME = "ARG_KEY_DEFAULT_START_DATETIME"
        private const val ARG_KEY_DEFAULT_END_DATETIME = "ARG_KEY_DEFAULT_END_DATETIME"

        private const val KEY_DATE_PICKER_SESSION_DATE = "KEY_DATE_PICKER_SESSION_DATE"
        private const val KEY_TIME_PICKER_SESSION_TIME_START = "KEY_TIME_PICKER_SESSION_TIME_START"
        private const val KEY_TIME_PICKER_SESSION_TIME_END = "KEY_TIME_PICKER_SESSION_TIME_END"

        fun packBundle(customerId: Long, sessionId: Long, showCustomer: Boolean,
                       defaultStartDateTime: Long = 0L, defaultEndDateTime: Long = 0L): Bundle =
            Bundle().apply {
                putLong(ARG_KEY_CUSTOMER_ID, customerId)
                putLong(ARG_KEY_SESSION_ID, sessionId)
                putBoolean(ARG_KEY_SHOW_CUSTOMER, showCustomer)
                putLong(ARG_KEY_DEFAULT_START_DATETIME, defaultStartDateTime)
                putLong(ARG_KEY_DEFAULT_END_DATETIME, defaultEndDateTime)
            }
    }
}

interface DisfunctionDeleteClickListener {
    fun onDisfunctionDeleteClick(disfunctionId: Long)
}