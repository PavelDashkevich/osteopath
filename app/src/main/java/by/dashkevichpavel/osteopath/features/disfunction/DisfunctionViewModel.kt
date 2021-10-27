package by.dashkevichpavel.osteopath.features.disfunction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction
import by.dashkevichpavel.osteopath.helpers.itemdeletion.DeletableInterface
import by.dashkevichpavel.osteopath.helpers.itemdeletion.ItemDeletionEventsHandler
import by.dashkevichpavel.osteopath.helpers.savechanges.SavableInterface
import by.dashkevichpavel.osteopath.helpers.savechanges.SaveChangesViewModelHelper
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DisfunctionViewModel(
    private val repository: LocalDbRepository
) : ViewModel(), SavableInterface {
    val disfunction = MutableLiveData<Disfunction?>(null)
    private var initialDisfunction: Disfunction = Disfunction()
    private var jobSave: Job? = null
    val saveChangesHelper = SaveChangesViewModelHelper(this)
    val currentDisfunctionId = MutableLiveData(0L)
    val disfunctionDeletionHandler = ItemDeletionEventsHandler(this::onDisfunctionDeleteConfirmation)

    fun selectDisfunction(customerId: Long, disfunctionId: Long) {
        if (disfunctionId == 0L) {
            setDisfunction(Disfunction(customerId = customerId))
            return
        }

        if (disfunction.value == null) {
            loadDisfunctionData(disfunctionId)
        }
    }

    fun setDescription(description: String) {
        disfunction.value?.description = description
    }

    fun setStatus(disfunctionStatusIId: Int) {
        disfunction.value?.disfunctionStatusId = disfunctionStatusIId
    }

    fun getDisfunctionId(): Long = currentDisfunctionId.value ?: 0L

    private fun onDisfunctionDeleteConfirmation(itemId: Long, userAction: DialogUserAction) {
        if (userAction == DialogUserAction.POSITIVE) {
            viewModelScope.launch {
                repository.deleteDisfunctionById(itemId)
            }
        }
    }

    private fun loadDisfunctionData(disfunctionId: Long) {
        viewModelScope.launch {
            repository.getDisfunctionById(disfunctionId)?.let { newDisfunction ->
                setDisfunction(newDisfunction)
            }
        }
    }

    private fun setDisfunction(newDisfunction: Disfunction) {
        disfunction.value = newDisfunction
        initialDisfunction = newDisfunction.copy()
        updateDisfunctionId()
    }

    private fun updateDisfunctionId() {
        currentDisfunctionId.value = disfunction.value?.id ?: 0L
    }

    override fun isDataModified(): Boolean = initialDisfunction.isModified(disfunction.value)

    override fun saveData() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        disfunction.value?.let { disf ->
            if (jobSave == null || jobSave?.isCompleted != false) {
                jobSave = viewModelScope.launch {
                    saveChangesHelper.startSaving()
                    disf.id = repository.insertDisfunction(disf)
                    saveChangesHelper.finishSaving()
                    updateDisfunctionId()
                    saveChangesHelper.navigateUp()
                }
            }
        }

        if (disfunction.value == null) saveChangesHelper.navigateUp()
    }
}