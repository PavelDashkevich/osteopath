package by.dashkevichpavel.osteopath.features.disfunction

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.BackClickHandler
import by.dashkevichpavel.osteopath.features.BackClickListener
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesFragmentHelper
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class FragmentDisfunction :
    Fragment(R.layout.fragment_disfunction),
    BackClickListener {
    private val viewModel: DisfunctionViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var backClickHandler: BackClickHandler? = null
    private val mapStatusIdToResId: Map<Int, Int> =
        mapOf(
            DisfunctionStatus.WORK.id to R.id.rb_work,
            DisfunctionStatus.WORK_DONE.id to R.id.rb_work_done,
            DisfunctionStatus.WORK_FAIL.id to R.id.rb_no_help,
            DisfunctionStatus.WRONG_DIAGNOSED.id to R.id.rb_wrong_diagnosed
        )
    private val mapResIdToStatusId = mapStatusIdToResId.entries.associateBy({ it.value }) { it.key }

    private lateinit var etDescription: TextInputEditText
    private lateinit var rgStatus: RadioGroup
    private lateinit var tbActions: Toolbar

    private lateinit var saveChangesHelper: SaveChangesFragmentHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)

        viewModel.selectDisfunction(
            arguments?.getLong(ARG_KEY_CUSTOMER_ID) ?: 0L,
            arguments?.getLong(ARG_KEY_DISFUNCTION_ID) ?: 0L
        )

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupToolbar(tbActions)
        setupListeners()
        setupObservers()
        setupHelpers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        when (item.itemId) {
            android.R.id.home -> viewModel.saveChangesHelper.finishEditing()
            R.id.mi_cancel -> viewModel.saveChangesHelper.cancelEditing()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
    }

    private fun setupViews(view: View) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        etDescription = view.findViewById(R.id.et_description)
        rgStatus = view.findViewById(R.id.rg_category)
        tbActions = view.findViewById(R.id.tb_actions)
    }

    private fun setupObservers() {
        viewModel.disfunction.observe(viewLifecycleOwner, this::onChangeDisfunction)
    }

    private fun setupHelpers() {
        saveChangesHelper = SaveChangesFragmentHelper(this, viewModel.saveChangesHelper)
    }

    private fun setupListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        etDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.setDescription(text.toString())
        }

        rgStatus.setOnCheckedChangeListener { _, checkedId ->
            viewModel.setStatus(mapResIdToStatusId[checkedId] ?: 0)
        }
    }

    private fun onChangeDisfunction(newDisfunction: Disfunction?) {
        newDisfunction?.let { disfunction ->
            etDescription.text = disfunction.description.toEditable()
            rgStatus.check(mapStatusIdToResId[disfunction.disfunctionStatusId] ?: -1)
            tbActions.title =
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
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
        const val ARG_KEY_DISFUNCTION_ID = "ARG_KEY_DISFUNCTION_ID"
    }
}