package by.dashkevichpavel.osteopath.viewcontroller.customerlistfilter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R

class FragmentCustomerListFilter : Fragment(R.layout.fragment_customer_list_filter) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var tbActions: Toolbar
    private lateinit var cbByAgeChildren: AppCompatCheckBox
    private lateinit var cbByAgeAdults: AppCompatCheckBox
    private lateinit var cbByCategoryWork: AppCompatCheckBox
    private lateinit var cbByCategoryWorkDone: AppCompatCheckBox
    private lateinit var cbByCategoryNoHelp: AppCompatCheckBox
    private var allCheckBoxes = mutableListOf<AppCompatCheckBox>()
    private lateinit var optionsMenu: Menu

    private lateinit var filterSharedPreferences: CustomerFilterSharedPreferences

    override fun onAttach(context: Context) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)

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
        setupViewElements(view)
        setupToolbar()
        setupCheckboxes()
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
        inflater.inflate(R.menu.customer_list_filter, menu)

        optionsMenu = menu

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
            R.id.mi_select_all -> {
                changeCheckBoxesState(true)
                return false
            }
            R.id.mi_deselect_all -> {
                changeCheckBoxesState(false)
                return false
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        filterSharedPreferences.saveValues()
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyView()
    }

    override fun onDestroyOptionsMenu() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyOptionsMenu()
    }

    override fun onDestroy() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDetach()
    }

    private fun setupViewElements(view: View) {
        tbActions = view.findViewById(R.id.tb_actions)
        cbByAgeChildren = view.findViewById(R.id.cb_by_age_children)
        cbByAgeAdults = view.findViewById(R.id.cb_by_age_adults)
        cbByCategoryWork = view.findViewById(R.id.cb_by_category_work)
        cbByCategoryWorkDone = view.findViewById(R.id.cb_by_category_work_done)
        cbByCategoryNoHelp = view.findViewById(R.id.cb_by_category_no_help)

        allCheckBoxes.add(cbByAgeChildren)
        allCheckBoxes.add(cbByAgeAdults)
        allCheckBoxes.add(cbByCategoryWork)
        allCheckBoxes.add(cbByCategoryWorkDone)
        allCheckBoxes.add(cbByCategoryNoHelp)
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(tbActions)
    }

    private fun setupCheckboxes() {
        filterSharedPreferences = CustomerFilterSharedPreferences(requireActivity())
        filterSharedPreferences.loadValues()
        filterSharedPreferences.mapIdsToKeys(
            cbByAgeChildren.id,
            cbByAgeAdults.id,
            cbByCategoryWork.id,
            cbByCategoryWorkDone.id,
            cbByCategoryNoHelp.id
        )

        allCheckBoxes.forEach { checkBox ->
            checkBox.isChecked = filterSharedPreferences.getValueByViewId(checkBox.id)
            checkBox.setOnClickListener {
                filterSharedPreferences.saveValue(checkBox.id, checkBox.isChecked)
            }
        }
    }

    private fun changeCheckBoxesState(toChecked: Boolean) {
        allCheckBoxes.forEach { checkBox ->
            checkBox.isChecked = toChecked
            filterSharedPreferences.saveValue(checkBox.id, checkBox.isChecked)
        }
    }
}