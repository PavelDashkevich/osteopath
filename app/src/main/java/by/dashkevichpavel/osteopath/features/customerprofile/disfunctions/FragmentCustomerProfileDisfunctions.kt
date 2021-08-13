package by.dashkevichpavel.osteopath.features.customerprofile.disfunctions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.customerlist.SpaceItemDecoration
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus
import by.dashkevichpavel.osteopath.features.disfunction.FragmentDisfunction
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.IllegalArgumentException

class FragmentCustomerProfileDisfunctions :
    Fragment(R.layout.fragment_customer_profile_disfunctions),
    DisfunctionCategoryCollapseExpandClickListener,
    DisfunctionClickListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private lateinit var rvDisfunctions: RecyclerView
    private var adapter = DisfunctionItemAdapter(
        mutableListOf(),
        this,
        this
    )
    private lateinit var fabAddDisfunction: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupListeners()
        setupObservers()
    }

    private fun setupViews(view: View) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        fabAddDisfunction = view.findViewById(R.id.fab_disfunction_add)
        rvDisfunctions = view.findViewById(R.id.rv_disfunctions_list)
        rvDisfunctions.layoutManager = LinearLayoutManager(requireContext())
        rvDisfunctions.addItemDecoration(SpaceItemDecoration())
        rvDisfunctions.adapter = adapter
    }

    private fun setupListeners() {
        fabAddDisfunction.setOnClickListener {
            openDisfunctionScreen(viewModel.customer.value?.id ?: 0L, 0L)
        }
    }

    private fun setupObservers() {
        viewModel.disfunctions.observe(viewLifecycleOwner, this::updateDisfunctionsList)
        viewModel.startListeningForDisfunctionsChanges()
    }

    private fun updateDisfunctionsList(newDisfunctionsList: MutableList<Disfunction>) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        adapter.setItems(newDisfunctionsList)
    }

    private fun openDisfunctionScreen(customerId: Long, disfunctionId: Long) {
        val bundle = Bundle()
        bundle.putLong(FragmentDisfunction.ARG_KEY_CUSTOMER_ID, customerId)
        bundle.putLong(FragmentDisfunction.ARG_KEY_DISFUNCTION_ID, disfunctionId)

        try {
            findNavController().navigate(R.id.action_fragmentCustomerProfile_to_fragmentDisfunction, bundle)
        } catch (e: IllegalArgumentException) {
            Log.d("OsteoApp", "openDisfunctionScreen(): exception: ${e.message}")
        }
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