package by.dashkevichpavel.osteopath.features.customerprofile

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerProfileBinding
import by.dashkevichpavel.osteopath.BackClickHandler
import by.dashkevichpavel.osteopath.BackClickListener
import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction
import by.dashkevichpavel.osteopath.features.dialogs.ItemDeleteConfirmationDialog
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesFragmentHelper
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class FragmentCustomerProfile :
    Fragment(R.layout.fragment_customer_profile),
    BackClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerProfileBinding: FragmentCustomerProfileBinding? = null
    private val binding get() = fragmentCustomerProfileBinding!!

    private lateinit var saveChangesHelper: SaveChangesFragmentHelper
    private var backClickHandler: BackClickHandler? = null

    private var contextMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectCustomer(arguments?.getLong(ARG_KEY_CUSTOMER_ID) ?: 0L)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupObservers()
        setupEventListeners()
        setupHelpers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
        contextMenu = menu
        onChangeCustomerId(viewModel.getCustomerId())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.saveChangesHelper.finishEditing()
            R.id.mi_cancel -> viewModel.saveChangesHelper.cancelEditing()
            R.id.mi_delete ->
                ItemDeleteConfirmationDialog.show(
                    fragmentManager = childFragmentManager,
                    itemId = viewModel.getCustomerId(),
                    message = getString(
                        R.string.customer_delete_dialog_message,
                        viewModel.getCustomerName()
                    ),
                    neutralButtonTextResId = R.string.customer_delete_dialog_button_neutral
                )
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
        fragmentCustomerProfileBinding = null
        contextMenu = null
    }

    private fun setupViews(view: View) {
        fragmentCustomerProfileBinding = FragmentCustomerProfileBinding.bind(view)
        setupToolbar(binding.tbActions)
        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        binding.vpViewPager.adapter = CustomerProfileAdapter(this)
        binding.vpViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.swipe(position)
                }
            }
        )
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tlCustomerTabs, binding.vpViewPager) { tab, pos ->
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
        viewModel.currentCustomerId.observe(viewLifecycleOwner, this::onChangeCustomerId)
    }

    private fun setupEventListeners() {
        childFragmentManager.setFragmentResultListener(
            ItemDeleteConfirmationDialog.KEY_RESULT,
            viewLifecycleOwner,
            this::onCustomerDeleteConfirm
        )
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
    }

    private fun onChangeToolbarTitle(newName: String?) {
        binding.tbActions.title = newName ?: getString(R.string.customer_profile_new_customer)
    }

    private fun onChangeCustomerId(customerId: Long) {
        contextMenu?.findItem(R.id.mi_delete)?.isVisible = (customerId != 0L)
    }

    private fun onCustomerDeleteConfirm(key: String, bundle: Bundle) {
        if (key != ItemDeleteConfirmationDialog.KEY_RESULT) return

        val result = ItemDeleteConfirmationDialog.extractResult(bundle)
        val userAction = result.second
        val customerId = result.first

        when (userAction) {
            DialogUserAction.POSITIVE -> {
                viewModel.deleteCustomer(customerId)
                findNavController().navigateUp()
            }
            DialogUserAction.NEUTRAL ->
                viewModel.setCustomerIsArchived(true)
            else -> { /* do nothing */ }
        }
    }

    override fun onBackClick(): Boolean {
        viewModel.saveChangesHelper.finishEditing()
        return true
    }

    companion object {
        private const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"

        fun packBundle(customerId: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(ARG_KEY_CUSTOMER_ID, customerId)

            return bundle
        }
    }
}