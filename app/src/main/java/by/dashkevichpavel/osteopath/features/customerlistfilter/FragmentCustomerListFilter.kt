package by.dashkevichpavel.osteopath.features.customerlistfilter

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerListFilterBinding
import by.dashkevichpavel.osteopath.features.customerlist.CustomerListViewModel
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentCustomerListFilter : Fragment(R.layout.fragment_customer_list_filter) {
    private val viewModel: CustomerListFilterViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerListFilterBinding: FragmentCustomerListFilterBinding? = null
    private val binding get() = fragmentCustomerListFilterBinding!!

    private var allCheckBoxes = mutableListOf<AppCompatCheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.customer_list_filter, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.mi_select_all -> changeCheckBoxesState(true)
            R.id.mi_deselect_all -> changeCheckBoxesState(false)
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onStop() {
        viewModel.saveFilterValues()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCustomerListFilterBinding = null
    }

    private fun setupViews(view: View) {
        fragmentCustomerListFilterBinding = FragmentCustomerListFilterBinding.bind(view)
        setupToolbar(binding.tbActions)
        setupCheckboxes()
    }

    private fun setupCheckboxes() {
        val filterValues = viewModel.loadFilterValues()

        binding.cbByAgeChildren.isChecked = filterValues.byAgeChildren
        binding.cbByAgeAdults.isChecked = filterValues.byAgeAdults
        binding.cbByCategoryWork.isChecked = filterValues.byCategoryWork
        binding.cbByCategoryWorkDone.isChecked = filterValues.byCategoryWorkDone
        binding.cbByCategoryNoHelp.isChecked = filterValues.byCategoryNoHelp

        allCheckBoxes.add(binding.cbByAgeChildren)
        allCheckBoxes.add(binding.cbByAgeAdults)
        allCheckBoxes.add(binding.cbByCategoryWork)
        allCheckBoxes.add(binding.cbByCategoryWorkDone)
        allCheckBoxes.add(binding.cbByCategoryNoHelp)
    }

    private fun setupEventListeners() {
        binding.cbByAgeChildren.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeFilterValue(byAgeChildren = isChecked)
        }
        binding.cbByAgeAdults.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeFilterValue(byAgeAdults = isChecked)
        }
        binding.cbByCategoryWork.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeFilterValue(byCategoryWork = isChecked)
        }
        binding.cbByCategoryWorkDone.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeFilterValue(byCategoryWorkDone = isChecked)
        }
        binding.cbByCategoryNoHelp.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeFilterValue(byCategoryNoHelp = isChecked)
        }
    }

    private fun changeCheckBoxesState(toChecked: Boolean) {
        allCheckBoxes.forEach { checkBox ->
            checkBox.isChecked = toChecked
        }
    }
}