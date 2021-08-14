package by.dashkevichpavel.osteopath.features.customerlist

import android.animation.LayoutTransition
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerListBinding
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.features.customerprofile.FragmentCustomerProfile
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.lang.IllegalArgumentException

class FragmentCustomerList :
    Fragment(R.layout.fragment_customer_list),
    CustomerClickListener {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.customer_list_menu, menu)

        optionsMenu = menu
        setupSearch()

        isMenuCreated = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mi_filter) {
            try {
                findNavController().navigate(R.id.action_fragmentCustomerList_to_fragmentCustomerListFilter)
            } catch (e: IllegalArgumentException) {
                Log.d("OsteoApp", "onOptionsItemSelected(): exception: ${e.message}")
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
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
        adapter = CustomerItemAdapter(viewModel.getCustomerList(), this)
        binding.rvCustomerList.adapter = adapter
    }

    private fun updateLoadingProgress(isLoading: Boolean) {
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE

        if (isLoading) {
            hideEmptyListHints()
        }
    }

    private fun updateCustomersList(newCustomers: List<Customer>) {
        hideEmptyListHints()

        if (newCustomers.isEmpty()) {
            if (viewModel.isSearchOrFilterResult) {
                binding.tvEmptyFilterOrSearchResult.visibility = View.VISIBLE
            } else {
                binding.tvEmptyListHint.visibility = View.VISIBLE
            }
        }

        setupRecyclerView()
    }

    private fun hideEmptyListHints() {
        binding.tvEmptyFilterOrSearchResult.visibility = View.GONE
        binding.tvEmptyListHint.visibility = View.GONE
    }

    private fun setupEventListeners() {
        binding.fabAddCustomer.setOnClickListener {
            openCustomerProfileScreen(0L)
        }

        binding.fabAddTestData.setOnClickListener {
            viewModel.loadTestData()
        }
    }

    private fun setupObservers() {
        viewModel.isCustomersLoading.observe(viewLifecycleOwner, ::updateLoadingProgress)
        viewModel.filteredCustomerList.observe(viewLifecycleOwner, ::updateCustomersList)
    }

    private fun openCustomerProfileScreen(customerId: Long) {
        try {
            findNavController().navigate(
                R.id.action_fragmentCustomerList_to_fragmentCustomer,
                FragmentCustomerProfile.packBundle(customerId)
            )
        } catch (e: IllegalArgumentException) {
            Log.d("OsteoApp", "openCustomerProfileScreen(): exception: ${e.message}")
        }
    }

    override fun onCustomerClick(customerId: Long) {
        openCustomerProfileScreen(customerId)
    }
}

interface CustomerClickListener {
    fun onCustomerClick(customerId: Long)
}