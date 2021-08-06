package by.dashkevichpavel.osteopath.features.selectdisfunctions

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.BackClickListener
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentSelectDisfunctions :
    Fragment(R.layout.fragment_select_disfunctions),
    DisfunctionCheckedChangeListener,
    BackClickListener {
    private val viewModel: SelectDisfunctionsViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private lateinit var rvDisfunctions: RecyclerView
    private lateinit var tbActions: Toolbar
    private val adapter = DisfunctionSelectableAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadDisfunctions(
            arguments?.getLong(ARG_KEY_CUSTOMER_ID, 0L) ?: 0L,
            (arguments?.getLongArray(ARG_KEY_EXCLUDE_IDS) ?: LongArray(0)).toList()
        )

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupToolbar(tbActions)
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.navigateBack()
            R.id.mi_cancel -> viewModel.cancelSelection()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun setupViews(view: View) {
        tbActions = view.findViewById(R.id.tb_actions)
        rvDisfunctions = view.findViewById(R.id.rv_disfunctions)
        rvDisfunctions.layoutManager = LinearLayoutManager(requireContext())
        rvDisfunctions.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.disfunctions.observe(viewLifecycleOwner, ::onChangeDisfunctions)
        viewModel.needToReturnSelectedIds.observe(viewLifecycleOwner, ::onChangeNeedToReturnSelectedIds)
    }

    private fun onChangeDisfunctions(disfunctions: List<Disfunction>) {
        adapter.setItems(disfunctions, viewModel.selectedIds)
    }

    private fun onChangeNeedToReturnSelectedIds(needToReturn: Boolean) {
        if (!needToReturn) return

        setFragmentResult(
            KEY_RESULT,
            bundleOf(BUNDLE_KEY_DISFUNCTIONS_IDS to viewModel.selectedIds.toLongArray())
        )

        findNavController().navigateUp()
    }

    override fun onCheckedChange(disfunctionId: Long, isChecked: Boolean) {
        viewModel.onDisfunctionSelect(disfunctionId, isChecked)
    }

    override fun onBackClick(): Boolean {
        viewModel.navigateBack()
        return true
    }

    companion object {
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
        const val ARG_KEY_EXCLUDE_IDS = "ARG_KEY_EXCLUDE_IDS"
        const val KEY_RESULT = "SELECT_DISFUNCTIONS_IDS"
        const val BUNDLE_KEY_DISFUNCTIONS_IDS = "BUNDLE_KEY_DISFUNCTIONS_IDS"

        fun extractBundle(bundle: Bundle): List<Long> {
            return (bundle.getLongArray(BUNDLE_KEY_DISFUNCTIONS_IDS) ?: LongArray(0)).toList()
        }

        fun createBundle(customerId: Long, selectedIds: List<Long>): Bundle {
            val bundle = Bundle()
            bundle.putLong(ARG_KEY_CUSTOMER_ID, customerId)
            bundle.putLongArray(ARG_KEY_EXCLUDE_IDS, selectedIds.toLongArray())

            return bundle
        }
    }
}

interface DisfunctionCheckedChangeListener {
    fun onCheckedChange(disfunctionId: Long, isChecked: Boolean)
}