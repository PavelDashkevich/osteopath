package by.dashkevichpavel.osteopath.features.settings.scheduler

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
import by.dashkevichpavel.osteopath.databinding.FragmentSettingsSchedulerBinding
import by.dashkevichpavel.osteopath.features.pickers.FragmentTimePicker
import by.dashkevichpavel.osteopath.helpers.*
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettings
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettingsTimeField
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDayItemClickListener
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.util.*

class FragmentSettingsScheduler :
    Fragment(R.layout.fragment_settings_scheduler),
    SessionDurationItemActionListener {
    private val viewModel: SchedulerSettingsViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSettingsSchedulerBinding: FragmentSettingsSchedulerBinding? = null
    private val binding get() = fragmentSettingsSchedulerBinding!!

    private val sessionDurationsAdapter = SessionDurationsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupFragmentResultListeners()
        setupObservers()
        viewModel.loadSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSettingsSchedulerBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSettingsSchedulerBinding = FragmentSettingsSchedulerBinding.bind(view)

        setupToolbar(binding.lToolbar.tbActions)
        requireActivity().title = getString(R.string.screen_settings_scheduler_toolbar_title)

        binding.rvSessionDurations.adapter = sessionDurationsAdapter
        binding.rvSessionDurations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSessionDurations.addItemDecoration(SpaceItemDecoration())
    }

    private fun setupEventListeners() {
        binding.btnSetupWorkingDays.setOnClickListener {
            safelyNavigateTo(R.id.action_fragmentSettingsScheduler_to_fragmentSettingsWorkingDays)
        }

        binding.etPauseAfterSession.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                TAG_PAUSE_AFTER_SESSION_TIME_FIELD,
                viewModel.getPauseAfterSession(),
                mode = FragmentTimePicker.TimePickerMode.DURATION
            )
        }

        binding.btnAddSessionDuration.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                TAG_SESSION_DURATION,
                0L,
                mode = FragmentTimePicker.TimePickerMode.DURATION
            )
        }
    }

    private fun setupFragmentResultListeners() {
        childFragmentManager.setFragmentResultListener(
            TAG_PAUSE_AFTER_SESSION_TIME_FIELD,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setPauseAfterSession(FragmentTimePicker.extractTimeInMillis(bundle))
        }

        childFragmentManager.setFragmentResultListener(
            TAG_SESSION_DURATION,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.addSessionDuration(FragmentTimePicker.extractTimeInMillis(bundle))
        }
    }

    private fun setupObservers() {
        viewModel.workingDaysSettings.observe(viewLifecycleOwner, this::onChangeWorkingDaysSettings)
        viewModel.pauseAfterSession.observe(viewLifecycleOwner, this::onChangePauseAfterSession)
        viewModel.sessionDurations.observe(viewLifecycleOwner, this::onChangeSessionDurations)
    }

    private fun onChangeWorkingDaysSettings(newWorkingDaysSettings: List<WorkingDaySettings>) {
        val workingDays = newWorkingDaysSettings.filter { daySettings -> daySettings.isWorkingDay }

        if (workingDays.isEmpty()) {
            binding.tvWorkingHours.text =
                getString(R.string.screen_settings_scheduler_no_working_days)
        } else {
            var workingHours = ""
            workingDays.forEach { daySettings ->
                workingHours += (if (workingHours.isNotBlank()) "\n" else "") +
                    daySettings.dayOfWeek.toNameOfWeekDay().toCapitalized() + ": " +
                        DateTimeUtil.durationToTime(daySettings.dayStartInMillis).formatTimeAsString() + "-" +
                        DateTimeUtil.durationToTime(daySettings.dayEndInMillis).formatTimeAsString() +
                        if (daySettings.restIncluded)
                            " " + getString(R.string.screen_settings_scheduler_rest_included) + " " +
                                    DateTimeUtil.durationToTime(daySettings.restStartInMillis).formatTimeAsString() + "-" +
                                    DateTimeUtil.durationToTime(daySettings.restEndInMillis).formatTimeAsString()
                        else
                            ""
            }
            binding.tvWorkingHours.text = workingHours
        }
    }

    private fun onChangePauseAfterSession(newPause: Long) {
        binding.etPauseAfterSession.text =
            DateTimeUtil.formatTimeAsDurationString(requireContext(), newPause).toEditable()
    }

    private fun onChangeSessionDurations(newSessionDurations: List<SessionDurationItem>) {
        sessionDurationsAdapter.setItems(newSessionDurations)
    }

    override fun onSessionDurationClick(action: SessionDurationItemAction) {
        when (action) {
            is SessionDurationItemAction.Delete ->
                viewModel.deleteSessionDuration(action.durationInMillis)
        }
    }

    companion object {
        private const val TAG_PAUSE_AFTER_SESSION_TIME_FIELD = "PAUSE_AFTER_SESSION_TIME_FIELD"
        private const val TAG_SESSION_DURATION = "SESSION_DURATION"
    }
}