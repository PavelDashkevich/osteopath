package by.dashkevichpavel.osteopath.features.customerprofile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.BackClickHandler
import by.dashkevichpavel.osteopath.features.BackClickListener
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesFragmentHelper
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FragmentCustomerProfile :
    Fragment(R.layout.fragment_customer_profile),
    BackClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )
    private lateinit var tbActions: Toolbar
    private lateinit var tlTabs: TabLayout
    private lateinit var vpPager: ViewPager2
    private lateinit var adapter: CustomerProfileAdapter

    private lateinit var saveChangesHelper: SaveChangesFragmentHelper
    private var backClickHandler: BackClickHandler? = null

    override fun onAttach(context: Context) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)

        viewModel.selectCustomer(arguments?.getLong(ARG_KEY_CUSTOMER_ID) ?: 0L)

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
        setupToolbar(tbActions)
        setupViewPager()
        setupTabLayout()
        setupObservers()
        setupHelpers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.saveChangesHelper.finishEditing()
            R.id.mi_cancel -> viewModel.saveChangesHelper.cancelEditing()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
    }

    private fun setupViews(view: View) {
        tbActions = view.findViewById(R.id.tb_actions)
        tlTabs = view.findViewById(R.id.tl_customer_tabs)
        vpPager = view.findViewById(R.id.vp2_view_pager)
    }

    private fun setupViewPager() {
        adapter = CustomerProfileAdapter(this)
        vpPager.adapter = adapter
        vpPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.swipe(position)
            }
        })
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

    private fun setupObservers() {
        viewModel.toolbarTitle.observe(viewLifecycleOwner, this::onChangeToolbarTitle)
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
    }

    private fun onChangeToolbarTitle(newName: String?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        tbActions.title = newName ?: getString(R.string.customer_profile_new_customer)
    }

    override fun onBackClick(): Boolean {
        viewModel.saveChangesHelper.finishEditing()
        return true
    }

    companion object {
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
    }
}