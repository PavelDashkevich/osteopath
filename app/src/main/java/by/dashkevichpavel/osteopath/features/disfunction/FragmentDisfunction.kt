package by.dashkevichpavel.osteopath.features.disfunction

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.toEditable
import by.dashkevichpavel.osteopath.features.BackClickHandler
import by.dashkevichpavel.osteopath.features.BackClickListener
import by.dashkevichpavel.osteopath.model.DisfunctionStatus
import by.dashkevichpavel.osteopath.model.setupToolbar
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class FragmentDisfunction : Fragment(R.layout.fragment_disfunction), BackClickListener {
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

    private var sbSaving: Snackbar? = null

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        inflater.inflate(R.menu.standard_edit_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        when (item.itemId) {
            android.R.id.home -> {
                if (viewModel.isDisfunctionModified()) {
                    viewModel.saveDisfunction()
                } else {
                    findNavController().navigateUp()
                }

                return true
            }
            R.id.mi_cancel -> {
                cancelChanges()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
        viewModel.disfunction.observe(viewLifecycleOwner, this::updateDisfunctionFields)
        viewModel.isSaving.observe(viewLifecycleOwner, this::checkIsSaving)
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

    private fun updateDisfunctionFields(newDisfunction: Disfunction?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        var toolbarTitle = "Новая дисфункция"

        newDisfunction?.let { disfunction ->
            etDescription.text = disfunction.description.toEditable()
            rgStatus.check(mapStatusIdToResId[disfunction.disfunctionStatusId] ?: -1)

            if (disfunction.id != 0L) {
                toolbarTitle = ""
            }

            tbActions.title = toolbarTitle
        }
    }

    private fun checkIsSaving(isSaving: Boolean?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: isSaving = $isSaving")

        if (isSaving == null) {
            return
        }

        if (isSaving) {
            sbSaving = Snackbar.make(requireView(), R.string.snackbar_saving_changes, Snackbar.LENGTH_INDEFINITE)
            sbSaving?.show()
            Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: show snackbar")
        } else {
            Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: navigateUp")
            if (sbSaving?.isShownOrQueued == true) {
                sbSaving?.dismiss()
            }
            findNavController().navigateUp()
        }
    }

    private fun cancelChanges() {
        if (viewModel.isDisfunctionModified()) {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.alert_dialog_cancel_changes)
                .setPositiveButton(R.string.alert_dialog_button_yes) { _, _ -> viewModel.saveDisfunction() }
                .setNegativeButton(R.string.alert_dialog_button_no) { _, _ -> findNavController().navigateUp() }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onBackClick(): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object {}.javaClass.enclosingMethod.name}")
        if (viewModel.isDisfunctionModified()) {
            viewModel.saveDisfunction()
            return true
        }

        return false
    }

    companion object {
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
        const val ARG_KEY_DISFUNCTION_ID = "ARG_KEY_DISFUNCTION_ID"
    }
}