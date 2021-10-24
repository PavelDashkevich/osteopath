package by.dashkevichpavel.osteopath.helpers.savechanges

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R

class SaveChangesFragmentHelper(
    private val fragment: Fragment,
    private val saveChangesViewModelHelper: SaveChangesViewModelHelperInterface
) : SaveChangesObserversInterface {
    private lateinit var savingStateIndicator: SavingStateIndicator

    init {
        saveChangesViewModelHelper.setupObservers(fragment.viewLifecycleOwner, this)
    }

    override fun onChangeSavingState(savingState: SavingState) {
        if (!::savingStateIndicator.isInitialized) {
            savingStateIndicator = SavingStateIndicator(fragment.requireView())
        }

        savingStateIndicator.onSavingStateChanges(savingState)
    }

    override fun onChangeNavigateUp(needToNavigateUp: Boolean) {
        if (needToNavigateUp) fragment.findNavController().navigateUp()
    }

    override fun onChangeConfirmSaveChanges(needToConfirmSaveChanges: Boolean) {
        if (!needToConfirmSaveChanges) return

        AlertDialog.Builder(fragment.requireContext())
            .setMessage(R.string.alert_dialog_cancel_changes)
            .setPositiveButton(R.string.alert_dialog_button_yes) { _, _ ->
                saveChangesViewModelHelper.acceptConfirmation()
            }
            .setNegativeButton(R.string.alert_dialog_button_no) { _, _ ->
                saveChangesViewModelHelper.discardConfirmation()
            }
            .show()
    }

}

interface SaveChangesViewModelHelperInterface {
    fun finishEditing()
    fun cancelEditing()
    fun acceptConfirmation()
    fun discardConfirmation()
    fun setupObservers(lifecycleOwner: LifecycleOwner, observers: SaveChangesObserversInterface)
    fun startSaving()
    fun finishSaving()
    fun navigateUp()
}

