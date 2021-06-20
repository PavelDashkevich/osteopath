package by.dashkevichpavel.osteopath

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.lang.IllegalArgumentException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentCustomerListFilter.newInstance] factory method to
 * create an instance of this fragment.
 */
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.customer_list_filter, menu)

        optionsMenu = menu

        super.onCreateOptionsMenu(menu, inflater)
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

    private fun changeCheckBoxesState(toChecked: Boolean) {
        allCheckBoxes.forEach { checkBox -> checkBox.isChecked = toChecked }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentCustomerListFilter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentCustomerListFilter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}