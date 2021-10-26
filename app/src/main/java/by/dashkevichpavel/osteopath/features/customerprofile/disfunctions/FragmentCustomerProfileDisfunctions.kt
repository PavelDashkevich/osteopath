package by.dashkevichpavel.osteopath.features.customerprofile.disfunctions

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerProfileDisfunctionsBinding
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction
import by.dashkevichpavel.osteopath.features.dialogs.ItemDeleteConfirmationDialog
import by.dashkevichpavel.osteopath.features.disfunction.FragmentDisfunction
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentCustomerProfileDisfunctions :
    Fragment(R.layout.fragment_customer_profile_disfunctions),
    DisfunctionCategoryCollapseExpandClickListener,
    DisfunctionClickListener,
    DisfunctionContextMenuClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerProfileDisfunctionsBinding: FragmentCustomerProfileDisfunctionsBinding? = null
    private val binding get() = fragmentCustomerProfileDisfunctionsBinding!!

    private var adapter = DisfunctionItemAdapter(
        mutableListOf(),
        this,
        this,
        this
    )

    private val menuItemIdsToDisfunctionStatusIds: Map<Int, Int> =
        mapOf(
            R.id.mi_work to DisfunctionStatus.WORK.id,
            R.id.mi_work_done to DisfunctionStatus.WORK_DONE.id,
            R.id.mi_work_fail to DisfunctionStatus.WORK_FAIL.id,
            R.id.mi_wrong_diagnosed to DisfunctionStatus.WRONG_DIAGNOSED.id
        )

    private val disfunctionStatusIdsToMenuItemIds: Map<Int, Int> =
        menuItemIdsToDisfunctionStatusIds.entries.associateBy( { it.value } ) { it.key }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupEventListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCustomerProfileDisfunctionsBinding = null
    }

    private fun setupViews(view: View) {
        fragmentCustomerProfileDisfunctionsBinding = FragmentCustomerProfileDisfunctionsBinding.bind(view)
        binding.rvDisfunctionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDisfunctionsList.addItemDecoration(SpaceItemDecoration())
        binding.rvDisfunctionsList.adapter = adapter
        binding.tvEmptyListHint.text = getString(
            R.string.empty_screen_hint,
            getString(R.string.empty_screen_hint_part_disfunctions)
        )
    }

    private fun setupEventListeners() {
        childFragmentManager.setFragmentResultListener(
            ItemDeleteConfirmationDialog.KEY_RESULT,
            viewLifecycleOwner,
            this::onDisfunctionDeleteConfirm
        )

        binding.fabAddDisfunction.setOnClickListener {
            openDisfunctionScreen(viewModel.customer.value?.id ?: 0L, 0L)
        }
    }

    private fun setupObservers() {
        viewModel.disfunctions.observe(viewLifecycleOwner, this::updateDisfunctionsList)
        viewModel.startListeningForDisfunctionsChanges()
    }

    private fun updateDisfunctionsList(newDisfunctionsList: MutableList<Disfunction>) {
        adapter.setItems(newDisfunctionsList)
        setEmptyScreenHintVisibility(newDisfunctionsList.isEmpty())
    }

    private fun setEmptyScreenHintVisibility(show: Boolean) {
        binding.tvEmptyListHint.isVisible = show
        binding.cvEmptyListHint.isVisible = show
    }

    private fun openDisfunctionScreen(customerId: Long, disfunctionId: Long) {
        safelyNavigateTo(
            R.id.action_fragmentCustomerProfile_to_fragmentDisfunction,
            FragmentDisfunction.packBundle(customerId, disfunctionId)
        )
    }

    private fun showCustomerContextMenu(disfunction: Disfunction, anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.inflate(R.menu.disfunction_listitem_context_menu)

        val changeStatusItem = popupMenu.menu.findItem(R.id.mi_change_status)
        if (changeStatusItem.hasSubMenu()) {
            disfunctionStatusIdsToMenuItemIds[disfunction.disfunctionStatusId]?.let { menuItemId ->
                changeStatusItem.subMenu.removeItem(menuItemId)
            }
        }
        popupMenu.setForceShowIcon(true)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            onDisfunctionContextMenuItemClick(disfunction, menuItem)
        }
        popupMenu.show()
    }

    private fun onDisfunctionContextMenuItemClick(
        disfunction: Disfunction,
        menuItem: MenuItem
    ): Boolean {
        when (menuItem.itemId) {
            R.id.mi_work, R.id.mi_work_done, R.id.mi_work_fail, R.id.mi_wrong_diagnosed ->
                menuItemIdsToDisfunctionStatusIds[menuItem.itemId]?.let { statusId ->
                    viewModel.changeDisfunctionStatus(disfunction.id, statusId)
                }
            R.id.mi_delete -> {
                ItemDeleteConfirmationDialog.show(
                    childFragmentManager,
                    KEY_DISFUNCTION_DELETE_CONFIRMATION,
                    disfunction.id,
                    getString(R.string.disfunction_delete_dialog_message)
                )
            }
            else -> return false
        }

        return true
    }

    private fun onDisfunctionDeleteConfirm(key: String, bundle: Bundle) {
        if (key != KEY_DISFUNCTION_DELETE_CONFIRMATION) return

        val result = ItemDeleteConfirmationDialog.extractResult(bundle)
        val userAction = result.second
        val disfunctionId = result.first

        if (userAction == DialogUserAction.POSITIVE) {
            viewModel.deleteDisfunction(disfunctionId)
        }
    }

    override fun onCategoryClick(disfunctionStatus: DisfunctionStatus) {
        adapter.setItems(viewModel.disfunctions.value ?: emptyList(), disfunctionStatus)
    }

    override fun onDisfunctionClick(customerId: Long, disfunctionId: Long) {
        openDisfunctionScreen(customerId, disfunctionId)
    }

    override fun onDisfunctionContextMenuClick(disfunction: Disfunction, anchorView: View) {
        showCustomerContextMenu(disfunction, anchorView)
    }

    companion object {
        private const val KEY_DISFUNCTION_DELETE_CONFIRMATION = "KEY_DISFUNCTION_DELETE_CONFIRMATION"
    }
}

interface DisfunctionCategoryCollapseExpandClickListener {
    fun onCategoryClick(disfunctionStatus: DisfunctionStatus)
}

interface DisfunctionClickListener {
    fun onDisfunctionClick(customerId: Long, disfunctionId: Long)
}

interface DisfunctionContextMenuClickListener {
    fun onDisfunctionContextMenuClick(disfunction: Disfunction, anchorView: View)
}