package by.dashkevichpavel.osteopath.viewcontroller.disfunction

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.toEditable
import by.dashkevichpavel.osteopath.viewcontroller.BackClickHandler
import by.dashkevichpavel.osteopath.viewcontroller.BackClickListener
import by.dashkevichpavel.osteopath.viewmodel.DisfunctionViewModel
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
            0 to R.id.rb_work,
            1 to R.id.rb_work_done,
            2 to R.id.rb_no_help,
            3 to R.id.rb_wrong_diagnosed
        )
    private val mapResIdToStatusId = mapStatusIdToResId.entries.associateBy({ it.value }) { it.key }

    private lateinit var etDescription: TextInputEditText
    private lateinit var rgStatus: RadioGroup
    private lateinit var rbWork: RadioButton
    private lateinit var rbWorkDone: RadioButton
    private lateinit var rbNoHelp: RadioButton
    private lateinit var rbWrongDiagnosed: RadioButton
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

        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        setupViews(view)
        setupToolbar()

        viewModel.disfunction.observe(viewLifecycleOwner, this::updateDisfunctionViews)
        viewModel.isSaving.observe(viewLifecycleOwner, this::checkIsSaving)
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
                if (isDisfunctionModified()) {
                    saveChanges()
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
        rbWork = view.findViewById(R.id.rb_work)
        rbWorkDone = view.findViewById(R.id.rb_work_done)
        rbNoHelp = view.findViewById(R.id.rb_no_help)
        rbWrongDiagnosed = view.findViewById(R.id.rb_wrong_diagnosed)
        tbActions = view.findViewById(R.id.tb_actions)
    }

    private fun setupToolbar() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        (activity as AppCompatActivity).setSupportActionBar(tbActions)
    }

    private fun updateDisfunctionViews(newDisfunction: Disfunction?) {
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

    private fun isDisfunctionModified(): Boolean {
        var res = false

        viewModel.disfunction.value?.let { disfunction ->
            val selectedStatusId = mapResIdToStatusId[rgStatus.checkedRadioButtonId] ?: 0

            res = (etDescription.text.toString() != disfunction.description ||
                    selectedStatusId != disfunction.disfunctionStatusId)
        }

        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: res = $res")

        return res
    }

    private fun updateViewModelDisfunction() {
        viewModel.disfunction.value?.let { disfunction ->
            disfunction.description = etDescription.text.toString()
            disfunction.disfunctionStatusId = mapResIdToStatusId[rgStatus.checkedRadioButtonId] ?: 0
        }
    }

    private fun navigateBack(): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        if (isDisfunctionModified()) {
            saveChanges()
            return true
        }

        return false
    }

    private fun saveChanges() {
        updateViewModelDisfunction()
        viewModel.saveDisfunction()
    }

    private fun cancelChanges() {
        if (isDisfunctionModified()) {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.alert_dialog_cancel_changes)
                .setPositiveButton(R.string.alert_dialog_button_yes) { _, _ -> saveChanges() }
                .setNegativeButton(R.string.alert_dialog_button_no) { _, _ -> findNavController().navigateUp() }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onBackClick(): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object {}.javaClass.enclosingMethod.name}")
        if (isDisfunctionModified()) {
            saveChanges()
            return true
        }

        return false
    }

    companion object {
        const val ARG_KEY_CUSTOMER_ID = "ARG_KEY_CUSTOMER_ID"
        const val ARG_KEY_DISFUNCTION_ID = "ARG_KEY_DISFUNCTION_ID"
    }
}