package by.dashkevichpavel.osteopath.features.session

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.customerprofile.FragmentCustomerProfile
import by.dashkevichpavel.osteopath.features.datetimepicker.FragmentDatePicker
import by.dashkevichpavel.osteopath.features.datetimepicker.FragmentTimePicker
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class FragmentSession :
    Fragment(R.layout.fragment_session), DisfunctionDeleteClickListener {
    private val viewModel: SessionViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private lateinit var tilDate: TextInputLayout
    private lateinit var etDate: TextInputEditText
    private lateinit var ibSetDate: AppCompatImageButton
    private lateinit var tilTime: TextInputLayout
    private lateinit var etTime: TextInputEditText
    private lateinit var ibSetTime: AppCompatImageButton
    private lateinit var tilPlan: TextInputLayout
    private lateinit var etPlan: TextInputEditText
    private lateinit var tilBodyConditions: TextInputLayout
    private lateinit var etBodyConditions: TextInputEditText
    private lateinit var ibAddDisfunction: AppCompatImageButton
    private lateinit var rvDisfunctions: RecyclerView
    private lateinit var adapter: DisfunctionInSessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val argSessionId = arguments?.getLong(ARG_KEY_SESSION_ID)
        val argCustomerId = arguments?.getLong(ARG_KEY_CUSTOMER_ID)

        viewModel.setSession(argSessionId ?: 0L, argCustomerId ?: 0L)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupViews(view: View) {
        tilDate = view.findViewById(R.id.til_date)
        etDate = view.findViewById(R.id.et_date)
        ibSetDate = view.findViewById(R.id.ib_set_date)
        tilTime = view.findViewById(R.id.til_time)
        etTime = view.findViewById(R.id.et_time)
        ibSetTime = view.findViewById(R.id.ib_set_time)
        tilPlan = view.findViewById(R.id.til_plan)
        etPlan = view.findViewById(R.id.et_plan)
        tilBodyConditions = view.findViewById(R.id.til_body_conditions)
        etBodyConditions = view.findViewById(R.id.et_body_conditions)
        ibAddDisfunction = view.findViewById(R.id.ib_add_disfunction)
        rvDisfunctions = view.findViewById(R.id.rv_disfunctions)
    }

    private fun setupRecyclerView() {
        adapter = DisfunctionInSessionAdapter(this)
        rvDisfunctions.layoutManager = LinearLayoutManager(requireContext())
        rvDisfunctions.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.session.observe(viewLifecycleOwner, ::updateFields)
        viewModel.disfunctions.observe(viewLifecycleOwner, ::updateDisfunctions)
        viewModel.sessionDateTime.observe(viewLifecycleOwner, ::updateDateTime)
    }

    private fun setupListeners() {
        childFragmentManager.setFragmentResultListener(
            FragmentDatePicker.KEY_RESULT,
            viewLifecycleOwner,
            ::onDatePickerDateSet
        )

        childFragmentManager.setFragmentResultListener(
            FragmentTimePicker.KEY_RESULT,
            viewLifecycleOwner,
            ::onTimePickerTimeSet
        )

        ibSetDate.setOnClickListener {
            FragmentDatePicker.show(
                childFragmentManager,
                "SET_SESSION_DATE",
                viewModel.session.value?.dateTime?.time ?: 0L
            )
        }

        ibSetTime.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                "SET_SESSION_TIME",
                viewModel.session.value?.dateTime?.time ?: 0L
            )
        }

        etPlan.doOnTextChanged { text, _, _, _ ->
            viewModel.session.value?.let { session -> session.plan = text.toString() }
        }

        etBodyConditions.doOnTextChanged { text, _, _, _ ->
            viewModel.session.value?.let { session -> session.bodyCondition = text.toString() }
        }
    }

    private fun updateFields(session: Session) {
        etPlan.text = session.plan.toEditable()
        etBodyConditions.text = session.bodyCondition.toEditable()
    }

    private fun updateDisfunctions(disfunctions: MutableList<Disfunction>) {
        adapter.setItems(disfunctions)
    }

    private fun updateDateTime(date: Date) {
        etDate.text = date.formatDateAsEditable()
        etTime.text = date.formatTimeAsEditable()
    }

    override fun onDisfunctionDeleteClick(disfunctionId: Long) {
        viewModel.deleteDisfunction(disfunctionId)
    }

    private fun onDatePickerDateSet(key: String, bundle: Bundle) {
        viewModel.setSessionDate(FragmentDatePicker.extractTimeInMillis(bundle))
    }

    private fun onTimePickerTimeSet(key: String, bundle: Bundle) {
        viewModel.setSessionTime(FragmentTimePicker.extractTimeInMillis(bundle))
    }

    companion object {
        const val ARG_KEY_SESSION_ID = "ARG_KEY_SESSION_ID"
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
    }
}

interface DisfunctionDeleteClickListener {
    fun onDisfunctionDeleteClick(disfunctionId: Long)
}