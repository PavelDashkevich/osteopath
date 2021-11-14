package by.dashkevichpavel.osteopath.features.nosessionperiod

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.dashkevichpavel.osteopath.BackClickHandler
import by.dashkevichpavel.osteopath.BackClickListener
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentNoSessionPeriodBinding
import by.dashkevichpavel.osteopath.features.pickers.FragmentDatePicker
import by.dashkevichpavel.osteopath.features.pickers.FragmentTimePicker
import by.dashkevichpavel.osteopath.features.session.FragmentSession
import by.dashkevichpavel.osteopath.helpers.formatDateAsEditable
import by.dashkevichpavel.osteopath.helpers.formatTimeAsEditable
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionFragmentHelper
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesFragmentHelper
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.model.NoSessionsPeriod
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.util.*

class FragmentNoSessionPeriod :
    Fragment(R.layout.fragment_no_session_period),
    BackClickListener {
    private val viewModel: NoSessionPeriodViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentNoSessionPeriodBinding: FragmentNoSessionPeriodBinding? = null
    private val binding get() = fragmentNoSessionPeriodBinding!!

    private var backClickHandler: BackClickHandler? = null
    private lateinit var saveChangesHelper: SaveChangesFragmentHelper
    private var itemDeletionFragmentHelper: ItemDeletionFragmentHelper? = null

    // fragment args
    var argNoSessionsPeriodId: Long? = null
    var argDefaultStartDateTime: Long? = null
    var argDefaultEndDateTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        argNoSessionsPeriodId = arguments?.getLong(ARG_KEY_NO_SESSIONS_PERIOD_ID)
        argDefaultStartDateTime = arguments?.getLong(ARG_KEY_DEFAULT_START_DATETIME)
        argDefaultEndDateTime = arguments?.getLong(ARG_KEY_DEFAULT_END_DATETIME)

        viewModel.selectNoSessionsPeriod(
            argNoSessionsPeriodId ?: 0L,
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
        onChangeNoSessionsPeriodId(viewModel.getNoSessionsPeriodId())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.saveChangesHelper.finishEditing()
            R.id.mi_cancel -> viewModel.saveChangesHelper.cancelEditing()
            R.id.mi_delete ->
                itemDeletionFragmentHelper?.showDialog(
                    viewModel.getNoSessionsPeriodId(),
                    getString(R.string.no_sessions_period_delete_dialog_message)
                )
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
        fragmentNoSessionPeriodBinding = null
    }

    private fun setupViews(view: View) {
        fragmentNoSessionPeriodBinding = FragmentNoSessionPeriodBinding.bind(view)
        setupToolbar(binding.lToolbar.tbActions)
    }

    private fun setupObservers() {
        viewModel.noSessionsPeriod.observe(viewLifecycleOwner, this::onChangeNoSessionsPeriod)
        viewModel.periodStart.observe(viewLifecycleOwner, this::onChangeDateTime)
        viewModel.periodEnd.observe(viewLifecycleOwner, this::onChangeDateTimeEnd)
        viewModel.currentNoSessionsPeriodId.observe(viewLifecycleOwner, this::onChangeNoSessionsPeriodId)
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
        itemDeletionFragmentHelper = ItemDeletionFragmentHelper(this,
            viewModel.noSessionsPeriodDeletionHandler, false)
    }

    private fun setupEventListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        binding.etDate.setOnClickListener {
            FragmentDatePicker.show(
                childFragmentManager,
                KEY_DATE_PICKER_NO_SESSIONS_PERIOD_DATE,
                viewModel.getNoSessionsPeriodDefaultStartDateTimeInMillis()
            )
        }

        binding.etTime.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_START,
                viewModel.getNoSessionsPeriodDefaultStartDateTimeInMillis()
            )
        }

        binding.etTimeEnd.setOnClickListener {
            FragmentTimePicker.show(
                childFragmentManager,
                KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_END,
                viewModel.getNoSessionsPeriodDefaultEndDateTimeInMillis()
            )
        }
    }

    private fun setupFragmentResultListeners() {
        childFragmentManager.setFragmentResultListener(
            KEY_DATE_PICKER_NO_SESSIONS_PERIOD_DATE,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setNoSessionsPeriodDate(FragmentDatePicker.extractTimeInMillis(bundle))
        }

        childFragmentManager.setFragmentResultListener(
            KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_START,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setNoSessionsPeriodStartTime(FragmentTimePicker.extractTimeInMillis(bundle))
        }

        childFragmentManager.setFragmentResultListener(
            KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_END,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setNoSessionsPeriodEndTime(FragmentTimePicker.extractTimeInMillis(bundle))
        }
    }

    private fun onChangeNoSessionsPeriod(noSessionsPeriod: NoSessionsPeriod) {
        requireActivity().title =
            if (noSessionsPeriod.id == 0L)
                getString(R.string.screen_no_sessions_period_edit_title)
            else
                ""
    }

    private fun onChangeNoSessionsPeriodId(noSessionsPeriodId: Long) {
        binding.lToolbar.tbActions.menu.findItem(R.id.mi_delete)?.isVisible =
            (noSessionsPeriodId != 0L)
    }

    private fun onChangeDateTime(date: Date) {
        binding.etDate.text = date.formatDateAsEditable()
        binding.etTime.text = date.formatTimeAsEditable()
    }

    private fun onChangeDateTimeEnd(date: Date) {
        binding.etTimeEnd.text = date.formatTimeAsEditable()
    }

    override fun onBackClick(): Boolean {
        viewModel.saveChangesHelper.finishEditing()
        return true
    }

    companion object {
        private const val ARG_KEY_NO_SESSIONS_PERIOD_ID = "ARG_KEY_NO_SESSIONS_PERIOD_ID"
        private const val ARG_KEY_DEFAULT_START_DATETIME = "ARG_KEY_DEFAULT_START_DATETIME"
        private const val ARG_KEY_DEFAULT_END_DATETIME = "ARG_KEY_DEFAULT_END_DATETIME"

        private const val KEY_DATE_PICKER_NO_SESSIONS_PERIOD_DATE = "KEY_DATE_PICKER_NO_SESSIONS_PERIOD_DATE"
        private const val KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_START = "KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_START"
        private const val KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_END = "KEY_TIME_PICKER_NO_SESSIONS_PERIOD_TIME_END"

        fun packBundle(noSessionsPeriodId: Long, defaultStartDateTime: Long = 0L,
                       defaultEndDateTime: Long = 0L): Bundle = Bundle().apply {
            putLong(ARG_KEY_NO_SESSIONS_PERIOD_ID, noSessionsPeriodId)
            putLong(ARG_KEY_DEFAULT_START_DATETIME, defaultStartDateTime)
            putLong(ARG_KEY_DEFAULT_END_DATETIME, defaultEndDateTime)
        }
    }
}