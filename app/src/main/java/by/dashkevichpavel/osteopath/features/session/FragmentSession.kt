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
import by.dashkevichpavel.osteopath.databinding.FragmentSessionBinding
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

    private var fragmentSessionBinding: FragmentSessionBinding? = null
    private val binding get() = fragmentSessionBinding!!

    private var backClickHandler: BackClickHandler? = null
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
        setupObservers()
        setupHelpers()
        setupEventListeners()
        setupFragmentResultListeners()
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
        fragmentSessionBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSessionBinding = FragmentSessionBinding.bind(view)
        setupToolbar(binding.lToolbar.tbActions)
        setupRecyclerView()
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
        viewModel.addDisfunctionActionEnabled.observe(viewLifecycleOwner,
            ::onChangeAddDisfunctionActionAccessibility)
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
    }

    private fun setupEventListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

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
                KEY_TIME_PICKER_SESSION_TIME,
                viewModel.getSessionDefaultDateTimeInMillis()
            )
        }

        binding.ibAddDisfunction.setOnClickListener {
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
        binding.lToolbar.tbActions.title = if (session.id == 0L) getString(R.string.header_new_session) else ""
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

    private fun onChangeAddDisfunctionActionAccessibility(isEnabled: Boolean) {
        binding.ibAddDisfunction.isEnabled = !isEnabled
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

        private const val KEY_DATE_PICKER_SESSION_DATE = "KEY_DATE_PICKER_SESSION_DATE"
        private const val KEY_TIME_PICKER_SESSION_TIME = "KEY_TIME_PICKER_SESSION_TIME"

        fun packBundle(customerId: Long, sessionId: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(ARG_KEY_CUSTOMER_ID, customerId)
            bundle.putLong(ARG_KEY_SESSION_ID, sessionId)

            return bundle
        }
    }
}

interface DisfunctionDeleteClickListener {
    fun onDisfunctionDeleteClick(disfunctionId: Long)
}