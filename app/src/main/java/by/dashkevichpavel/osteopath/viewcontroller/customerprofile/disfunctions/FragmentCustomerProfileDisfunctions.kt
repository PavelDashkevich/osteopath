package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus
import by.dashkevichpavel.osteopath.viewcontroller.customerprofile.FragmentCustomerProfile
import by.dashkevichpavel.osteopath.viewcontroller.disfunction.FragmentDisfunction
import by.dashkevichpavel.osteopath.viewmodel.CustomerProfileViewModel
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

    override fun onAttach(context: Context) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)
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
        setupListeners()

        viewModel.disfunctions.observe(viewLifecycleOwner, this::updateDisfunctionsList)
        viewModel.startListeningForDisfunctionsChanges()
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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onSaveInstanceState(outState)
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
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        fabAddDisfunction = view.findViewById(R.id.fab_disfunction_add)
        rvDisfunctions = view.findViewById(R.id.rv_disfunctions_list)
        rvDisfunctions.layoutManager = LinearLayoutManager(requireContext())
        rvDisfunctions.adapter = adapter
    }

    private fun setupListeners() {
        fabAddDisfunction.setOnClickListener {
            openDisfunctionScreen(viewModel.customer.value?.id ?: 0L, 0L)
        }
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