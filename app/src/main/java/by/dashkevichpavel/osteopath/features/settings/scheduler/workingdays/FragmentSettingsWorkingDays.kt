package by.dashkevichpavel.osteopath.features.settings.scheduler.workingdays

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.BackClickHandler
import by.dashkevichpavel.osteopath.BackClickListener
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSettingsWorkingDaysBinding
import by.dashkevichpavel.osteopath.features.pickers.FragmentTimePicker
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDayItemClickListener
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettings
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettingsTimeField
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FragmentSettingsWorkingDays :
    Fragment(R.layout.fragment_settings_working_days),
    WorkingDayItemClickListener,
    BackClickListener {
    private val viewModel: WorkingDaysSettingsViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSettingsWorkingDaysBinding: FragmentSettingsWorkingDaysBinding? = null
    private val binding get() = fragmentSettingsWorkingDaysBinding!!

    private var backClickHandler: BackClickHandler? = null

    private val workingDaySettingsAdapter = WorkingDaySettingsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.loadSettings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupFragmentResultListeners()
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.navigateUp()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
        fragmentSettingsWorkingDaysBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSettingsWorkingDaysBinding = FragmentSettingsWorkingDaysBinding.bind(view)
        setupToolbar(binding.lToolbar.tbActions)
        binding.lToolbar.tbActions.title = getString(R.string.screen_settings_workings_days_toolbar_title)
        binding.rvWorkingDays.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWorkingDays.addItemDecoration(SpaceItemDecoration())
        binding.rvWorkingDays.adapter = workingDaySettingsAdapter
    }

    private fun setupEventListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)
    }

    private fun setupFragmentResultListeners() {
        childFragmentManager.setFragmentResultListener(
            TAG_WORKING_DAY_TIME_FIELD,
            viewLifecycleOwner
        ) { _, bundle ->
            val timeInMillis = FragmentTimePicker.extractTimeInMillis(bundle)
            FragmentTimePicker.extractAdditionalBundle(bundle)?.let { args ->
                viewModel.setTimeForField(
                    args.getInt(BUNDLE_KEY_ADDITIONAL_ARG_DAY_OF_WEEK, 0),
                    timeInMillis,
                    WorkingDaySettingsTimeField.values()[
                            args.getInt(BUNDLE_KEY_ADDITIONAL_ARG_WORKING_DAY_TIME_FIELD, 0)
                    ]
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.workingDaysSettings.observe(
            viewLifecycleOwner,
            this::onChangeWorkingDaysSettings
        )
        viewModel.needToShowTimePeriodError.observe(
            viewLifecycleOwner,
            this::onChangeNeedToShowTimePeriodError
        )
        viewModel.needToNavigateUp.observe(viewLifecycleOwner, this::onChangeNeedToNavigateUp)
    }

    private fun onChangeWorkingDaysSettings(newWorkingDaysSettings: List<WorkingDaySettings>) {
        workingDaySettingsAdapter.setItems(newWorkingDaysSettings)
    }

    private fun onChangeNeedToShowTimePeriodError(showError: Boolean) {
        if (!showError) return

        Snackbar
            .make(
                requireView(),
                R.string.screen_settings_workings_days_time_period_error,
                Snackbar.LENGTH_SHORT
            )
            .show()

        viewModel.errorShown()
    }

    private fun onChangeNeedToNavigateUp(needToNavigateUp: Boolean) {
        if (needToNavigateUp) findNavController().navigateUp()
    }

    override fun onBackClick(): Boolean {
        viewModel.navigateUp()
        return true
    }

    override fun onWorkingDayTimeFieldClick(
        dayOfWeek: Int,
        timeField: WorkingDaySettingsTimeField,
        initialTimeInMillis: Long
    ) {
        FragmentTimePicker.show(
            childFragmentManager,
            TAG_WORKING_DAY_TIME_FIELD,
            initialTimeInMillis,
            FragmentTimePicker.TimePickerMode.DURATION,
            Bundle().apply {
                putInt(BUNDLE_KEY_ADDITIONAL_ARG_DAY_OF_WEEK, dayOfWeek)
                putInt(BUNDLE_KEY_ADDITIONAL_ARG_WORKING_DAY_TIME_FIELD, timeField.ordinal)
            }
        )
    }

    override fun onIsWorkingDayClick(dayOfWeek: Int, isChecked: Boolean) {
        viewModel.setIsWorkingDay(dayOfWeek, isChecked)
    }

    override fun onRestIncludedClick(dayOfWeek: Int, isChecked: Boolean) {
        viewModel.setRestIncluded(dayOfWeek, isChecked)
    }

    companion object {
        private const val TAG_WORKING_DAY_TIME_FIELD = "WORKING_DAY_TIME_FIELD"
        private const val BUNDLE_KEY_ADDITIONAL_ARG_DAY_OF_WEEK = "ADDITIONAL_ARG_DAY_OF_WEEK"
        private const val BUNDLE_KEY_ADDITIONAL_ARG_WORKING_DAY_TIME_FIELD = "ADDITIONAL_ARG_WORKING_DAY_TIME_FIELD"
    }
}