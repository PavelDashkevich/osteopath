package by.dashkevichpavel.osteopath.features.sessions.schedule

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSessionsScheduleBinding
import by.dashkevichpavel.osteopath.features.nosessionperiod.FragmentNoSessionPeriod
import by.dashkevichpavel.osteopath.features.session.FragmentSession
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemAction
import by.dashkevichpavel.osteopath.features.sessions.TimeIntervalItemActionListener
import by.dashkevichpavel.osteopath.features.sessions.schedule.calendar.CalendarDayItem
import by.dashkevichpavel.osteopath.features.sessions.schedule.calendar.CalendarDayItemAction
import by.dashkevichpavel.osteopath.features.sessions.schedule.calendar.CalendarDayItemActionListener
import by.dashkevichpavel.osteopath.features.sessions.schedule.calendar.CalendarDaysAdapter
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeIntervalItem
import by.dashkevichpavel.osteopath.features.sessions.schedule.timeline.TimeIntervalItemAdapter
import by.dashkevichpavel.osteopath.helpers.actionCallPhoneNumber
import by.dashkevichpavel.osteopath.helpers.actionOpenInstagram
import by.dashkevichpavel.osteopath.helpers.contacttocustomer.ContactToCustomerAction
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentSessionsSchedule :
    Fragment(R.layout.fragment_sessions_schedule),
    CalendarDayItemActionListener,
    TimeIntervalItemActionListener {
    private val viewModel: SessionsScheduleViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentSessionsScheduleBinding: FragmentSessionsScheduleBinding? = null
    private val binding get() = fragmentSessionsScheduleBinding!!

    private val calendarDaysAdapter = CalendarDaysAdapter(this)
    private val timeIntervalItemAdapter = TimeIntervalItemAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupObservers()
        setupEventListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startLoaders()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopLoaders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentSessionsScheduleBinding = null
    }

    private fun setupViews(view: View) {
        fragmentSessionsScheduleBinding = FragmentSessionsScheduleBinding.bind(view)

        binding.tvDayOfWeek0.text = DateTimeUtil.getShortNameOfDayOfWeek(0)
        binding.tvDayOfWeek1.text = DateTimeUtil.getShortNameOfDayOfWeek(1)
        binding.tvDayOfWeek2.text = DateTimeUtil.getShortNameOfDayOfWeek(2)
        binding.tvDayOfWeek3.text = DateTimeUtil.getShortNameOfDayOfWeek(3)
        binding.tvDayOfWeek4.text = DateTimeUtil.getShortNameOfDayOfWeek(4)
        binding.tvDayOfWeek5.text = DateTimeUtil.getShortNameOfDayOfWeek(5)
        binding.tvDayOfWeek6.text = DateTimeUtil.getShortNameOfDayOfWeek(6)

        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = calendarDaysAdapter

        binding.rvTimeBlocks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTimeBlocks.adapter = timeIntervalItemAdapter
        binding.rvTimeBlocks.addItemDecoration(SpaceItemDecoration())

        binding.spSessionDurations.adapter = ArrayAdapter(
            this.requireContext(),
            R.layout.spinner_item_session_duration,
            R.id.tvSessionDuration,
            viewModel.sessionDurations.map { duration ->
                DateTimeUtil.formatTimeAsDurationString(this.requireContext(), duration)
            }
        )
        if (binding.spSessionDurations.selectedItemPosition == AdapterView.INVALID_POSITION &&
            viewModel.sessionDurations.isNotEmpty()) {
            binding.spSessionDurations.setSelection(0)
        }

        enableSessionDurations()
    }

    private fun enableSessionDurations() {
        binding.spSessionDurations.isEnabled = binding.cbShowFreeTimeBlocks.isChecked
    }

    private fun setupObservers() {
        viewModel.calendarDayItems.observe(viewLifecycleOwner, calendarDaysAdapter::setItems)
        viewModel.timeLine.observe(viewLifecycleOwner, timeIntervalItemAdapter::setItems)
        viewModel.currentMonth.observe(viewLifecycleOwner, this::onChangeCurrentMonth)
    }

    private fun setupEventListeners() {
        binding.ibCalendarMonthFirst.setOnClickListener { viewModel.goToMonthOfEarliestSession() }
        binding.ibCalendarMonthBack.setOnClickListener { viewModel.goToPrevMonth() }
        binding.ibCalendarMonthNext.setOnClickListener { viewModel.goToNextMonth() }
        binding.ibCalendarMonthLast.setOnClickListener { viewModel.goToMonthOfLatestSession() }

        binding.fabAdd.setOnClickListener {
            showSchedulerAddMenu(binding.fabAdd)
        }

        binding.spSessionDurations.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    if (binding.cbShowFreeTimeBlocks.isChecked) {
                        viewModel.setFilterBySessionDuration(pos)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) { }
            }

        binding.cbShowFreeTimeBlocks.setOnCheckedChangeListener { _, isChecked ->
            enableSessionDurations()

            if (!isChecked) {
                viewModel.setFilterBySessionDuration(null)
            } else {
                viewModel.setFilterBySessionDuration(
                    if (binding.spSessionDurations.selectedItemPosition == AdapterView.INVALID_POSITION)
                        null
                    else
                        binding.spSessionDurations.selectedItemPosition
                )
            }
        }
    }

    private fun showSchedulerAddMenu(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.inflate(R.menu.scheduler_add)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.mi_add_session ->
                    openScreenSession(0L, 0L, true)
                R.id.mi_add_no_sessions_period ->
                    openScreenNoSessionsPeriod(0L)
                else -> return@setOnMenuItemClickListener false
            }
            true
        }
        popupMenu.show()
    }

    private fun openScreenSession(customerId: Long, sessionId: Long, showCustomer: Boolean,
                                  defaultStartDateTime: Long = 0L, defaultEndDateTime: Long = 0L) {
        safelyNavigateTo(
            R.id.action_fragmentSessions_to_fragmentSession,
            FragmentSession.packBundle(customerId, sessionId, showCustomer,
                defaultStartDateTime, defaultEndDateTime)
        )
    }

    private fun openScreenNoSessionsPeriod(noSessionsPeriodId: Long, defaultStartDateTime: Long = 0L,
                                           defaultEndDateTime: Long = 0L) {
        safelyNavigateTo(
            R.id.action_fragmentSessions_to_fragmentNoSessionsPeriod,
            FragmentNoSessionPeriod.packBundle(noSessionsPeriodId, defaultStartDateTime,
                defaultEndDateTime)
        )
    }

    private fun onChangeCurrentMonth(dateTimeInMillis: Long) {
        binding.tvMonthYear.text = DateTimeUtil.formatAsMonthAndYearString(dateTimeInMillis)
    }

    override fun onCalendarDayClick(action: CalendarDayItemAction) {
        when (action) {
            is CalendarDayItemAction.Select -> viewModel.selectDay(action.calendarDayItem)
        }
    }

    override fun onTimeIntervalItemClick(timeIntervalItemAction: TimeIntervalItemAction) {
        when (timeIntervalItemAction) {
            is TimeIntervalItemAction.SessionAction.Open ->
                openScreenSession(timeIntervalItemAction.customerId,
                    timeIntervalItemAction.sessionId, false)
            is TimeIntervalItemAction.SessionAction.ContactToCustomer ->
                when (timeIntervalItemAction.contactToCustomerAction) {
                    is ContactToCustomerAction.Call.Phone ->
                        actionCallPhoneNumber(timeIntervalItemAction.contactToCustomerAction.phoneNumber)
                    is ContactToCustomerAction.Message.Instagram ->
                        actionOpenInstagram(timeIntervalItemAction.contactToCustomerAction.userId)
                }
            is TimeIntervalItemAction.NoSessionsPeriodAction.Open ->
                openScreenNoSessionsPeriod(timeIntervalItemAction.noSessionsPeriodId)
            is TimeIntervalItemAction.AvailableToScheduleAction.AddSession ->
                openScreenSession(0L, 0L, true,
                    timeIntervalItemAction.dateTimeStart, timeIntervalItemAction.dateTimeEnd)
            is TimeIntervalItemAction.AvailableToScheduleAction.AddNoSessionsPeriod ->
                openScreenNoSessionsPeriod(0L,
                    timeIntervalItemAction.dateTimeStart, timeIntervalItemAction.dateTimeEnd)
            else -> { }
        }
    }
}