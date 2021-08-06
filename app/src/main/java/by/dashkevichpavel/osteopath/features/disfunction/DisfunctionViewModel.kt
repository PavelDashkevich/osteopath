package by.dashkevichpavel.osteopath.features.disfunction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    }

    override fun isDataModified(): Boolean = initialDisfunction.isModified(disfunction.value)

    override fun saveData() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        disfunction.value?.let { disf ->
            if (jobSave == null || jobSave?.isCompleted != false) {
                jobSave = viewModelScope.launch {
                    saveChangesHelper.startSaving()
                    repository.insertDisfunction(disf)
                    saveChangesHelper.finishSaving()
                    saveChangesHelper.navigateUp()
                }
            }
        }

        if (disfunction.value == null) saveChangesHelper.navigateUp()
    }
}