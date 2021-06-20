package by.dashkevichpavel.osteopath

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.viewmodel.CustomerListViewModel
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.IllegalArgumentException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentCustomerList.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentCustomerList : Fragment(R.layout.fragment_customer_list) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewElements(view)
        setupToolbar()

        viewModel.isCustomersLoading.observe(viewLifecycleOwner, this::updateLoadingProgress)
        viewModel.customerList.observe(viewLifecycleOwner, this::updateCustomersList)
        viewModel.loadCustomers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OsteoApp", "onCreateOptionsMenu()")

        inflater.inflate(R.menu.customer_list_menu, menu)

        optionsMenu = menu
        setupSearch()

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.d("OsteoApp", "onPrepareOptionsMenu()")

        super.onPrepareOptionsMenu(menu)
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

    private fun setupSearch() {
        Log.d("OsteoApp", "setupSearch()")
        val searchItem = optionsMenu.findItem(R.id.mi_search)

        svSearch = searchItem.actionView as SearchView
        svSearch.queryHint = "Искать по имени"

        svSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toast.makeText(context, "Query: $query", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //Toast.makeText(context, "Query: $newText", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        )

        val searchBar = svSearch.findViewById<LinearLayout>(R.id.search_bar)
        searchBar.layoutTransition = LayoutTransition()
    }

    private fun setupRecyclerView() {
        Log.d("OsteoApp", "setupRecyclerView()")
        rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        adapter = CustomerAdapter(viewModel.customerList.value as MutableList<CustomerEntity>)
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

        if (viewModel.isCustomersLoading.value == true) {
            return
        }

        tvEmptyListHint.visibility =
            if (newCustomers.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        setupRecyclerView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentCustomerList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}