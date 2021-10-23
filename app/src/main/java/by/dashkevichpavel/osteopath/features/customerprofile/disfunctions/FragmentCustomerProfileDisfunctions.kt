package by.dashkevichpavel.osteopath.features.customerprofile.disfunctions

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerProfileDisfunctionsBinding
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.SpaceItemDecoration
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus
import by.dashkevichpavel.osteopath.features.disfunction.FragmentDisfunction
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.helpers.safelyNavigateTo
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.lang.IllegalArgumentException

class FragmentCustomerProfileDisfunctions :
    Fragment(R.layout.fragment_customer_profile_disfunctions),
    DisfunctionCategoryCollapseExpandClickListener,
    DisfunctionClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerProfileDisfunctionsBinding: FragmentCustomerProfileDisfunctionsBinding? = null
    private val binding get() = fragmentCustomerProfileDisfunctionsBinding!!

    private var adapter = DisfunctionItemAdapter(
        mutableListOf(),
        this,
        this
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupEventListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCustomerProfileDisfunctionsBinding = null
    }

    private fun setupViews(view: View) {
        fragmentCustomerProfileDisfunctionsBinding = FragmentCustomerProfileDisfunctionsBinding.bind(view)
        binding.rvDisfunctionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDisfunctionsList.addItemDecoration(SpaceItemDecoration())
        binding.rvDisfunctionsList.adapter = adapter
    }

    private fun setupEventListeners() {
        binding.fabAddDisfunction.setOnClickListener {
            openDisfunctionScreen(viewModel.customer.value?.id ?: 0L, 0L)
        }
    }

    private fun setupObservers() {
        viewModel.disfunctions.observe(viewLifecycleOwner, this::updateDisfunctionsList)
        viewModel.startListeningForDisfunctionsChanges()
    }

    private fun updateDisfunctionsList(newDisfunctionsList: MutableList<Disfunction>) {
        adapter.setItems(newDisfunctionsList)
    }

    private fun openDisfunctionScreen(customerId: Long, disfunctionId: Long) {
        safelyNavigateTo(
            R.id.action_fragmentCustomerProfile_to_fragmentDisfunction,
            FragmentDisfunction.packBundle(customerId, disfunctionId)
        )
    }

    override fun onCategoryClick(disfunctionStatus: DisfunctionStatus) {
        adapter.setItems(viewModel.disfunctions.value ?: emptyList(), disfunctionStatus)
    }

    override fun onDisfunctionClick(customerId: Long, disfunctionId: Long) {
        openDisfunctionScreen(customerId, disfunctionId)
    }
}

interface DisfunctionCategoryCollapseExpandClickListener {
    fun onCategoryClick(disfunctionStatus: DisfunctionStatus)
}

interface DisfunctionClickListener {
    fun onDisfunctionClick(customerId: Long, disfunctionId: Long)
}