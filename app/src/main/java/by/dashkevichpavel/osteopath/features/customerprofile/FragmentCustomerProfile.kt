package by.dashkevichpavel.osteopath.features.customerprofile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FragmentCustomerProfile : Fragment(R.layout.fragment_customer_profile) {
    private lateinit var tbActions: Toolbar
    private lateinit var tlTabs: TabLayout
    private lateinit var vpPager: ViewPager2
    private lateinit var adapter: CustomerProfileAdapter

    private val viewModel: CustomerProfileViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    override fun onAttach(context: Context) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)

        val argVal = arguments?.getLong(ARG_KEY_CUSTOMER_ID)

        viewModel.selectCustomer(argVal ?: 0L)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        setupViews(view)
        setupToolbar()
        setupViewPager()
        setupTabLayout()

        viewModel.customerName.observe(viewLifecycleOwner, this::updateCustomerName)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        updateCustomerName(viewModel.customerName.value)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onSaveInstanceState(outState)
        viewModel.toolbarStateTitle = tbActions.title.toString()
    }

    override fun onStop() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStop()
    }

    override fun onDestroyView() {
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
    }

    private fun setupViews(view: View) {
        tbActions = view.findViewById(R.id.tb_actions)
        tlTabs = view.findViewById(R.id.tl_customer_tabs)
        vpPager = view.findViewById(R.id.vp2_view_pager)
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(tbActions)
        //updateCustomerName(viewModel.customerName.value)
    }

    private fun setupViewPager() {
        adapter = CustomerProfileAdapter(this)
        vpPager.adapter = adapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tlTabs, vpPager) { tab, pos ->
            tab.icon = AppCompatResources.getDrawable(
                requireContext(),
                when (pos) {
                    0 -> R.drawable.ic_baseline_perm_contact_calendar_24
                    1 -> R.drawable.ic_baseline_healing_24
                    2 -> R.drawable.ic_baseline_calendar_today_24
                    3 -> R.drawable.ic_baseline_attach_file_24
                    else -> R.drawable.ic_baseline_pest_control_24
                }
            )
        }
            .attach()
    }

    private fun updateCustomerName(newName: String?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        tbActions.title = newName ?: getString(R.string.customer_profile_new_customer)
    }

    companion object {
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
    }
}