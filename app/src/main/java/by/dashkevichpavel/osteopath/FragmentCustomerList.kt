package by.dashkevichpavel.osteopath

import android.animation.LayoutTransition
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.viewmodel.CustomerListViewModel
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.IllegalArgumentException

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentCustomerList.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentCustomerList : Fragment(R.layout.fragment_customer_list) {
    private val viewModel: CustomerListViewModel by viewModels {
        OsteoViewModelFactory(requireContext().applicationContext)
    }

    private lateinit var rvCustomers: RecyclerView
    private lateinit var fabAddCustomer: FloatingActionButton
    private lateinit var tvEmptyListHint: TextView
    private lateinit var pbLoadingProgress: ProgressBar
    private lateinit var tbActions: Toolbar
    private lateinit var svSearch: SearchView
    private lateinit var optionsMenu: Menu
    private lateinit var adapter: CustomerAdapter

    private lateinit var filterSharedPreferences: CustomerFilterSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewElements(view)
        setupToolbar()

        filterSharedPreferences = CustomerFilterSharedPreferences(requireContext())
        filterSharedPreferences.loadValues()
        viewModel.setFilter(filterSharedPreferences.filterValues)

        viewModel.isCustomersLoading.observe(viewLifecycleOwner, this::updateLoadingProgress)
        viewModel.filteredCustomerList.observe(viewLifecycleOwner, this::updateCustomersList)
        viewModel.startCustomersTableChangeListening()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OsteoApp", "onCreateOptionsMenu()")

        inflater.inflate(R.menu.customer_list_menu, menu)

        optionsMenu = menu
        setupSearch()

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveStateOfSearchView()
    }

    override fun onStop() {
        super.onStop()
        saveStateOfSearchView()
    }

    private fun setupViewElements(view: View) {
        fabAddCustomer = view.findViewById(R.id.fab_customer_add)
        tvEmptyListHint = view.findViewById(R.id.tv_empty_list_hint)
        rvCustomers = view.findViewById(R.id.rv_customer_list)
        pbLoadingProgress = view.findViewById(R.id.pb_loading)
        tbActions = view.findViewById(R.id.tb_actions)
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(tbActions)
    }

    private fun hideKeyboard(): Boolean {
        val imm: InputMethodManager = svSearch.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        return imm.hideSoftInputFromWindow(svSearch.windowToken, 0)
    }

    private fun setupSearch() {
        Log.d("OsteoApp", "setupSearch()")
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
        viewModel.searchViewStateIconified = svSearch.isIconified
        viewModel.searchViewStateFocused = svSearch.isFocused
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
        Log.d("OsteoApp", "setupRecyclerView()")
        rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        adapter = CustomerAdapter(viewModel.filteredCustomerList.value as MutableList<CustomerEntity>)
        rvCustomers.adapter = adapter
    }

    private fun updateLoadingProgress(isLoading: Boolean) {
        Log.d("OsteoApp", "updateLoadingProgress($isLoading)")
        pbLoadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE

        if (isLoading) {
            tvEmptyListHint.visibility = View.GONE
        }
    }

    private fun updateCustomersList(newCustomers: List<CustomerEntity>) {
        Log.d("OsteoApp", "updateCustomersList(), list size = ${newCustomers.size}")

        tvEmptyListHint.visibility =
            if (newCustomers.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        setupRecyclerView()
    }
}