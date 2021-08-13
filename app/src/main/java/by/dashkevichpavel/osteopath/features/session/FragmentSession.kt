package by.dashkevichpavel.osteopath.features.session

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.BackClickHandler
import by.dashkevichpavel.osteopath.features.BackClickListener
import by.dashkevichpavel.osteopath.features.customerlist.SpaceItemDecoration
import by.dashkevichpavel.osteopath.features.pickers.FragmentDatePicker
import by.dashkevichpavel.osteopath.features.pickers.FragmentTimePicker
import by.dashkevichpavel.osteopath.features.selectdisfunctions.FragmentSelectDisfunctions
import by.dashkevichpavel.osteopath.helpers.formatDateAsEditable
import by.dashkevichpavel.osteopath.helpers.formatTimeAsEditable
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesFragmentHelper
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.helpers.toEditable
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.IllegalArgumentException
import java.util.*

class FragmentSession :
    Fragment(R.layout.fragment_session),
    DisfunctionDeleteClickListener,
    BackClickListener {
    private val viewModel: SessionViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var backClickHandler: BackClickHandler? = null

    private lateinit var tbActions: Toolbar
    private lateinit var tilDate: TextInputLayout
    private lateinit var etDate: TextInputEditText
    private lateinit var tilTime: TextInputLayout
    private lateinit var etTime: TextInputEditText
    private lateinit var tilPlan: TextInputLayout
    private lateinit var etPlan: TextInputEditText
    private lateinit var tilBodyConditions: TextInputLayout
    private lateinit var etBodyConditions: TextInputEditText
    private lateinit var ibAddDisfunction: AppCompatImageButton
    private lateinit var smDone: SwitchMaterial
    private lateinit var rvDisfunctions: RecyclerView
    private lateinit var adapter: DisfunctionInSessionAdapter

    private lateinit var saveChangesHelper: SaveChangesFragmentHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val argSessionId = arguments?.getLong(ARG_KEY_SESSION_ID)
        val argCustomerId = arguments?.getLong(ARG_KEY_CUSTOMER_ID)

        viewModel.selectSession(argSessionId ?: 0L, argCustomerId ?: 0L)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupToolbar(tbActions)
        setupRecyclerView()
        setupObservers()
        setupHelpers()
        setupListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.saveChangesHelper.finishEditing()
            R.id.mi_cancel -> viewModel.saveChangesHelper.cancelEditing()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
    }

    private fun setupViews(view: View) {
        tbActions = view.findViewById(R.id.tb_actions)
        tilDate = view.findViewById(R.id.til_date)
        etDate = view.findViewById(R.id.et_date)
        tilTime = view.findViewById(R.id.til_time)
        etTime = view.findViewById(R.id.et_time)
        tilPlan = view.findViewById(R.id.til_plan)
        etPlan = view.findViewById(R.id.et_plan)
        tilBodyConditions = view.findViewById(R.id.til_body_conditions)
        etBodyConditions = view.findViewById(R.id.et_body_conditions)
        ibAddDisfunction = view.findViewById(R.id.ib_add_disfunction)
        smDone = view.findViewById(R.id.sm_done)
        rvDisfunctions = view.findViewById(R.id.rv_disfunctions)
    }

    private fun setupRecyclerView() {
        adapter = DisfunctionInSessionAdapter(this)
        rvDisfunctions.layoutManager = LinearLayoutManager(requireContext())
        rvDisfunctions.addItemDecoration(SpaceItemDecoration())
        rvDisfunctions.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.session.observe(viewLifecycleOwner, ::onChangeSession)
        viewModel.disfunctions.observe(viewLifecycleOwner, ::onChangeDisfunctions)
        viewModel.sessionDateTime.observe(viewLifecycleOwner, ::onChangeDateTime)
        viewModel.addDisfunctionActionEnabled.observe(viewLifecycleOwner,
            ::onChangeAddDisfunctionActionAccessibility)
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
    }

    private fun setupListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        setupFragmentResultListeners()

        etDate.setOnClickListener {
            FragmentDatePicker.show(
                childFragmentManager,
                KEY_DATE_PICKER_SESSION_DATE,
                viewModel.getSessionDefaultDateTimeInMillis()
            )
        }

        etTime.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                KEY_TIME_PICKER_SESSION_TIME,
                viewModel.getSessionDefaultDateTimeInMillis()
            )
        }

        ibAddDisfunction.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_fragmentSession_to_fragmentSelectDisfunctions,
                    FragmentSelectDisfunctions.createBundle(
                        viewModel.getCustomerId(),
                        viewModel.getSelectedDisfunctionsIds()
                    )
                )
            } catch (e: IllegalArgumentException) {
                Log.d("OsteoApp", "setupListeners(): ibAddDisfunction.onClickListener: exception: ${e.message}")
            }
        }

        etPlan.doOnTextChanged { text, _, _, _ ->
            viewModel.setPlan(text.toString())
        }

        etBodyConditions.doOnTextChanged { text, _, _, _ ->
            viewModel.setBodyCondition(text.toString())
        }

        smDone.setOnCheckedChangeListener { _, isChecked ->
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
            FragmentTimePicker.KEY_RESULT,
        viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setSessionTime(FragmentTimePicker.extractTimeInMillis(bundle))
        }

        setFragmentResultListener(FragmentSelectDisfunctions.KEY_RESULT) { _, bundle ->
            viewModel.setSelectedDisfunctionsIds(
                FragmentSelectDisfunctions.extractBundle(bundle)
            )
        }
    }

    private fun onChangeSession(session: Session) {
        tbActions.title = if (session.id == 0L) getString(R.string.header_new_session) else ""
        etPlan.text = session.plan.toEditable()
        etBodyConditions.text = session.bodyCondition.toEditable()
        smDone.isChecked = session.isDone
    }

    private fun onChangeDisfunctions(disfunctions: MutableList<Disfunction>) {
        adapter.setItems(disfunctions)
    }

    private fun onChangeDateTime(date: Date) {
        etDate.text = date.formatDateAsEditable()
        etTime.text = date.formatTimeAsEditable()
    }

    private fun onChangeAddDisfunctionActionAccessibility(isEnabled: Boolean) {
        ibAddDisfunction.isEnabled = !isEnabled
    }

    override fun onDisfunctionDeleteClick(disfunctionId: Long) {
        viewModel.deleteDisfunction(disfunctionId)
    }

    override fun onBackClick(): Boolean {
        viewModel.saveChangesHelper.finishEditing()
        return true
    }

    companion object {
        const val ARG_KEY_SESSION_ID = "ARG_KEY_SESSION_ID"
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"

        private const val KEY_DATE_PICKER_SESSION_DATE = "KEY_DATE_PICKER_SESSION_DATE"
        private const val KEY_TIME_PICKER_SESSION_TIME = "KEY_TIME_PICKER_SESSION_TIME"
    }
}

interface DisfunctionDeleteClickListener {
    fun onDisfunctionDeleteClick(disfunctionId: Long)
}