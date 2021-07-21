package by.dashkevichpavel.osteopath.features.customerlist

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.repositories.sharedprefs.CustomerFilterSharedPreferences
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.features.customerprofile.FragmentCustomerProfile
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.IllegalArgumentException

class FragmentCustomerList :
    Fragment(R.layout.fragment_customer_list),
    CustomerClickListener {
    private val viewModel: CustomerListViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private lateinit var rvCustomers: RecyclerView
    private lateinit var fabAddCustomer: FloatingActionButton
    private lateinit var fabAddTestData: FloatingActionButton
    private lateinit var tvEmptyListHint: TextView
    private lateinit var tvEmptyResultsHint: TextView
    private lateinit var pbLoadingProgress: ProgressBar
    private lateinit var tbActions: Toolbar
    private lateinit var svSearch: SearchView
    private lateinit var optionsMenu: Menu
    private lateinit var adapter: CustomerItemAdapter

    private var isMenuCreated: Boolean = false

    /*override fun onAttach(context: Context) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttach(context)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        viewModel.init(requireContext())

        setupViewElements(view)
        setupToolbar()
        setupClickListeners()
        setupObservers()
    }

    /*override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStart()
    }

    override fun onResume() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onResume()
    }*/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        inflater.inflate(R.menu.customer_list_menu, menu)

        optionsMenu = menu
        setupSearch()

        isMenuCreated = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    /*override fun onPrepareOptionsMenu(menu: Menu) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPause()
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

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
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        super.onSaveInstanceState(outState)
        saveStateOfSearchView()
    }

    override fun onStop() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        super.onStop()
        saveStateOfSearchView()
    }

    /*override fun onDestroyView() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroy()
    }

    override fun onDestroyOptionsMenu() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyOptionsMenu()
    }

    override fun onDetach() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDetach()
    }*/

    private fun setupViewElements(view: View) {
        fabAddCustomer = view.findViewById(R.id.fab_customer_add)
        fabAddTestData = view.findViewById(R.id.fab_add_test_data)
        tvEmptyListHint = view.findViewById(R.id.tv_empty_list_hint)
        tvEmptyResultsHint = view.findViewById(R.id.tv_empty_filter_or_search_result)
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
        rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        adapter = CustomerItemAdapter(viewModel.getCustomerList(), this)
        rvCustomers.adapter = adapter
    }

    private fun updateLoadingProgress(isLoading: Boolean) {
        pbLoadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE

        if (isLoading) {
            hideEmptyListHints()
        }
    }

    private fun updateCustomersList(newCustomers: List<Customer>) {
        hideEmptyListHints()

        if (newCustomers.isEmpty()) {
            if (viewModel.isSearchOrFilterResult) {
                tvEmptyResultsHint.visibility = View.VISIBLE
            } else {
                tvEmptyListHint.visibility = View.VISIBLE
            }
        }

        setupRecyclerView()
    }

    private fun hideEmptyListHints() {
        tvEmptyResultsHint.visibility = View.GONE
        tvEmptyListHint.visibility = View.GONE
    }

    private fun setupClickListeners() {
        fabAddCustomer.setOnClickListener {
            openCustomerProfileScreen(0L)
        }

        fabAddTestData.setOnClickListener {
            viewModel.loadTestData()
        }
    }

    private fun setupObservers() {
        viewModel.observeLoadingProgressChanges(viewLifecycleOwner, this::updateLoadingProgress)
        viewModel.observeCustomerListChanges(viewLifecycleOwner, this::updateCustomersList)
    }

    private fun openCustomerProfileScreen(customerId: Long) {
        val bundle = Bundle()
        bundle.putLong(FragmentCustomerProfile.ARG_KEY_CUSTOMER_ID, customerId)

        try {
            findNavController().navigate(R.id.action_fragmentCustomerList_to_fragmentCustomer, bundle)
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