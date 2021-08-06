package by.dashkevichpavel.osteopath.helpers.savechanges

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class SaveChangesViewModelHelper(
    private val savableObject: SavableInterface
) : SaveChangesViewModelHelperInterface {
    private val savingState = MutableLiveData(SavingState.NOT_STARTED)
    private val needToNavigateUp = MutableLiveData(false)
    private val needToConfirmSaveChanges = MutableLiveData(false)

    override fun setupObservers(lifecycleOwner: LifecycleOwner, observers: SaveChangesObserversInterface) {
        savingState.observe(lifecycleOwner, observers::onChangeSavingState)
        needToNavigateUp.observe(lifecycleOwner, observers::onChangeNavigateUp)
        needToConfirmSaveChanges.observe(lifecycleOwner, observers::onChangeConfirmSaveChanges)
    }

    override fun startSaving() {
        savingState.value = SavingState.STARTED
    }

    override fun finishSaving() {
        savingState.value = SavingState.COMPLETED
    }

    override fun navigateUp() {
        needToNavigateUp.value = true
    }

    override fun finishEditing() {
        if (savableObject.isDataModified()) {
            savableObject.saveData()
        } else {
            navigateUp()
        }
    }

    override fun cancelEditing() {
        if (savableObject.isDataModified()) {
            needToConfirmSaveChanges.value = true
        } else {
            navigateUp()
        }
    }

    override fun acceptConfirmation() {
        needToConfirmSaveChanges.value = false
        savableObject.saveData()
    }

    override fun discardConfirmation() {
        needToConfirmSaveChanges.value = false
        navigateUp()
    }
}

interface SaveChangesObserversInterface {
    fun onChangeSavingState(savingState: SavingState)
    fun onChangeNavigateUp(needToNavigateUp: Boolean)
    fun onChangeConfirmSaveChanges(needToConfirmSaveChanges: Boolean)
}

interface SavableInterface {
    fun isDataModified(): Boolean
    fun saveData()
}