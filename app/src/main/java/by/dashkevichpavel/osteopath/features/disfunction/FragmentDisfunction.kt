package by.dashkevichpavel.osteopath.features.disfunction

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentDisfunctionBinding
import by.dashkevichpavel.osteopath.BackClickHandler
import by.dashkevichpavel.osteopath.BackClickListener
import by.dashkevichpavel.osteopath.features.customerprofile.FragmentCustomerProfile
import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction
import by.dashkevichpavel.osteopath.features.dialogs.ItemDeleteConfirmationDialog
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionFragmentHelper
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesFragmentHelper
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.helpers.toEditable
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentDisfunction :
    Fragment(R.layout.fragment_disfunction),
    BackClickListener {
    private val viewModel: DisfunctionViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )
    private var fragmentDisfunctionBinding: FragmentDisfunctionBinding? = null
    private val binding get() = fragmentDisfunctionBinding!!
    private var backClickHandler: BackClickHandler? = null
    private val mapStatusIdToResId: Map<Int, Int> =
        mapOf(
            DisfunctionStatus.WORK.id to R.id.rbWork,
            DisfunctionStatus.WORK_DONE.id to R.id.rbWorkDone,
            DisfunctionStatus.WORK_FAIL.id to R.id.rbNoHelp,
            DisfunctionStatus.WRONG_DIAGNOSED.id to R.id.rbWrongDiagnosed
        )
    private val mapResIdToStatusId = mapStatusIdToResId.entries.associateBy({ it.value }) { it.key }
    private lateinit var saveChangesHelper: SaveChangesFragmentHelper
    private var contextMenu: Menu? = null
    private var itemDeletionFragmentHelper: ItemDeletionFragmentHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectDisfunction(
            arguments?.getLong(ARG_KEY_CUSTOMER_ID) ?: 0L,
            arguments?.getLong(ARG_KEY_DISFUNCTION_ID) ?: 0L
        )

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupObservers()
        setupHelpers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
        contextMenu = menu
        onChangeDisfunctionId(viewModel.getDisfunctionId())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        when (item.itemId) {
            android.R.id.home -> viewModel.saveChangesHelper.finishEditing()
            R.id.mi_cancel -> viewModel.saveChangesHelper.cancelEditing()
            R.id.mi_delete ->
                itemDeletionFragmentHelper?.showDialog(
                    itemId = viewModel.getDisfunctionId(),
                    message = getString(R.string.disfunction_delete_dialog_message)
                )
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
        fragmentDisfunctionBinding = null
    }

    private fun setupViews(view: View) {
        fragmentDisfunctionBinding = FragmentDisfunctionBinding.bind(view)
        setupToolbar(binding.lToolbar.tbActions)
    }

    private fun setupObservers() {
        viewModel.disfunction.observe(viewLifecycleOwner, this::onChangeDisfunction)
        viewModel.currentDisfunctionId.observe(viewLifecycleOwner, this::onChangeDisfunctionId)
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
        itemDeletionFragmentHelper = ItemDeletionFragmentHelper(this,
            viewModel.disfunctionDeletionHandler, false)
    }

    private fun setupEventListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        binding.etDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.setDescription(text.toString())
        }

        binding.rgCategory.setOnCheckedChangeListener { _, checkedId ->
            viewModel.setStatus(mapResIdToStatusId[checkedId] ?: 0)
        }
    }

    private fun onChangeDisfunctionId(disfunctionId: Long) {
        contextMenu?.findItem(R.id.mi_delete)?.isVisible = (disfunctionId != 0L)
    }

    private fun onChangeDisfunction(newDisfunction: Disfunction?) {
        newDisfunction?.let { disfunction ->
            binding.etDescription.text = disfunction.description.toEditable()
            binding.rgCategory.check(mapStatusIdToResId[disfunction.disfunctionStatusId] ?: -1)
            requireActivity().title =
                if (disfunction.id != 0L) {
                    ""
                } else {
                    getString(R.string.header_new_disfunction)
                }
        }
    }

    override fun onBackClick(): Boolean {
        viewModel.saveChangesHelper.finishEditing()
        return true
    }

    companion object {
        private const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
        private const val ARG_KEY_DISFUNCTION_ID = "ARG_KEY_DISFUNCTION_ID"

        fun packBundle(customerId: Long, disfunctionId: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(ARG_KEY_CUSTOMER_ID, customerId)
            bundle.putLong(ARG_KEY_DISFUNCTION_ID, disfunctionId)

            return bundle
        }
    }
}