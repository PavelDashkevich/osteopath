package by.dashkevichpavel.osteopath.features.customerlist

import android.animation.LayoutTransition
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerListBinding
import by.dashkevichpavel.osteopath.features.customerprofile.FragmentCustomerProfile
import by.dashkevichpavel.osteopath.features.dialogs.CustomerDeleteConfirmationDialog
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentCustomerList :
    Fragment(R.layout.fragment_customer_list),
    CustomerClickListener,
    CustomerContextMenuClickListener {
    private val viewModel: CustomerListViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerListBinding: FragmentCustomerListBinding? = null
    private val binding get() = fragmentCustomerListBinding!!

    private lateinit var svSearch: SearchView
    private lateinit var optionsMenu: Menu
    private lateinit var adapter: CustomerItemAdapter

    private var isMenuCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupObservers()
        viewModel.setFilter()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startCustomerListObserving(requireContext().applicationContext)
    }

    override fun onResume() {
        super.onResume()
        viewModel.startCustomerListObserving(requireContext().applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.customer_list_menu, menu)

        optionsMenu = menu
        setupSearch()

        isMenuCreated = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_filter ->
                safelyNavigateTo(R.id.action_fragmentCustomerList_to_fragmentCustomerListFilter)
            android.R.id.home -> binding.dlDrawerLayout.open()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopCustomerListObserving()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopCustomerListObserving()
        saveStateOfSearchView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCustomerListBinding = null
    }

    private fun hideKeyboard(): Boolean {
        val imm: InputMethodManager = svSearch.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        return imm.hideSoftInputFromWindow(svSearch.windowToken, 0)
    }

    private fun setupViews(view: View) {
        fragmentCustomerListBinding = FragmentCustomerListBinding.bind(view)
        setupToolbar(binding.tbActions)
        binding.lNavMenu.nvMain.setupWithNavController(findNavController())
        binding.tvEmptyListHint.text = getString(
            R.string.empty_screen_hint,
            getString(R.string.empty_screen_hint_part_customers)
        )
        registerForContextMenu(binding.rvCustomerList)
    }

    private fun setupSearch() {
        val searchItem = optionsMenu.findItem(R.id.mi_search)

        svSearch = searchItem.actionView as SearchView
        svSearch.queryHint = "Искать по имени"

        svSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.setQueryString(query ?: "")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            }
        )

        svSearch.setOnCloseListener {
            viewModel.setQueryString("")
            false
        }

        val searchBar = svSearch.findViewById<LinearLayout>(R.id.search_bar)
        searchBar.layoutTransition = LayoutTransition()

        restoreStateOfSearchView()
    }

    private fun saveStateOfSearchView() {
        if (!isMenuCreated) { return }

        viewModel.searchViewStateIconified = svSearch.isIconified
        viewModel.searchViewStateQueryString = svSearch.query
        viewModel.searchViewStateKeyboardShown = hideKeyboard()
    }

    private fun restoreStateOfSearchView() {
        svSearch.isIconified = viewModel.searchViewStateIconified
        if (!svSearch.isIconified) {
            svSearch.setQuery(viewModel.searchViewStateQueryString, false)

            if (!viewModel.searchViewStateKeyboardShown) {
                svSearch.clearFocus()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCustomerList.layoutManager = LinearLayoutManager(requireContext())
        adapter = CustomerItemAdapter(viewModel.getCustomerList(), this, this)
        binding.rvCustomerList.adapter = adapter
    }

    private fun updateLoadingProgress(isLoading: Boolean) {
        binding.pbLoading.isVisible = isLoading

        if (isLoading) {
            setEmptyListHintsVisibility(
                showEmptyListHint = false,
                showEmptyFilterOrSearchResultHint = false
            )
        }
    }

    private fun updateCustomersList(newCustomers: List<Customer>) {
        setEmptyListHintsVisibility(
            showEmptyListHint = false,
            showEmptyFilterOrSearchResultHint = false
        )

        if (newCustomers.isEmpty()) {
            if (viewModel.isSearchOrFilterResult) {
                setEmptyListHintsVisibility(
                    showEmptyListHint = false,
                    showEmptyFilterOrSearchResultHint = true
                )
            } else {
                setEmptyListHintsVisibility(
                    showEmptyListHint = true,
                    showEmptyFilterOrSearchResultHint = false
                )
            }
        }

        setupRecyclerView()
    }

    private fun setEmptyListHintsVisibility(
        showEmptyListHint: Boolean = true,
        showEmptyFilterOrSearchResultHint: Boolean = true
    ) {
        binding.tvEmptyFilterOrSearchResult.isVisible = showEmptyFilterOrSearchResultHint
        binding.tvEmptyListHint.isVisible = showEmptyListHint
        binding.cvEmptyListHint.isVisible = showEmptyListHint
    }

    private fun setupEventListeners() {
        childFragmentManager.setFragmentResultListener(
            CustomerDeleteConfirmationDialog.KEY_RESULT,
            viewLifecycleOwner,
            this::onCustomerDeleteConfirm
        )

        binding.fabAddCustomer.setOnClickListener {
            openCustomerProfileScreen(0L)
        }
    }

    private fun setupObservers() {
        viewModel.isCustomersLoading.observe(viewLifecycleOwner, ::updateLoadingProgress)
        viewModel.filteredCustomerList.observe(viewLifecycleOwner, ::updateCustomersList)
        viewModel.startCustomerListObserving(requireContext().applicationContext)
    }

    private fun openCustomerProfileScreen(customerId: Long) {
        safelyNavigateTo(
            R.id.action_fragmentCustomerList_to_fragmentCustomer,
            FragmentCustomerProfile.packBundle(customerId)
        )
    }

    private fun showCustomerContextMenu(customer: Customer, anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.inflate(R.menu.customer_listitem_context_menu)
        popupMenu.menu.removeItem(
            if (customer.isArchived)
                R.id.mi_archive
            else
                R.id.mi_unarchive
        )
        popupMenu.setForceShowIcon(true)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            onCustomerContextMenuItemClick(customer, menuItem)
        }
        popupMenu.show()
    }

    private fun onCustomerContextMenuItemClick(
        customer: Customer,
        menuItem: MenuItem
    ): Boolean {
        when(menuItem.itemId) {
            R.id.mi_archive -> viewModel.putCustomerToArchive(customer.id)
            R.id.mi_unarchive -> viewModel.removeCustomerFromArchive(customer.id)
            R.id.mi_delete -> {
                CustomerDeleteConfirmationDialog.show(
                    childFragmentManager,
                    KEY_CUSTOMER_DELETE_CONFIRMATION,
                    customer.name,
                    customer.id
                )
            }
            else -> return false
        }

        return true
    }

    private fun onCustomerDeleteConfirm(key: String, bundle: Bundle) {
        val result = CustomerDeleteConfirmationDialog.extractResult(bundle)
        val userAction = result.second
        val customerId = result.first

        when (userAction) {
            CustomerDeleteConfirmationDialog.UserAction.DELETE ->
                viewModel.deleteCustomer(customerId)
            CustomerDeleteConfirmationDialog.UserAction.ARCHIVE ->
                viewModel.putCustomerToArchive(customerId)
            else -> { /* do nothing */ }
        }
    }

    override fun onCustomerClick(customerId: Long) = openCustomerProfileScreen(customerId)
    override fun onCustomerContextMenuClick(customer: Customer, anchorView: View) =
        showCustomerContextMenu(customer, anchorView)

    companion object {
        private const val KEY_CUSTOMER_DELETE_CONFIRMATION = "KEY_CUSTOMER_DELETE_CONFIRMATION"
    }
}

interface CustomerClickListener {
    fun onCustomerClick(customerId: Long)
}

interface CustomerContextMenuClickListener {
    fun onCustomerContextMenuClick(customer: Customer, anchorView: View)
}