package by.dashkevichpavel.osteopath.features.disfunction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DisfunctionViewModel(
    private val repository: OsteoDbRepository
) : ViewModel() {
    val disfunction = MutableLiveData<Disfunction?>(null)
    val isSaving = MutableLiveData<Boolean?>(null)
    private var initialDisfunction: Disfunction? = null

    private var jobSave: Job? = null

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

    fun isDisfunctionModified(): Boolean {
        var res = false

        disfunction.value?.let { disf ->
            res = disf.description != initialDisfunction?.description &&
                    disf.disfunctionStatusId != initialDisfunction?.disfunctionStatusId
        }

        return res
    }

    fun saveDisfunction() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        disfunction.value?.let { disf ->
            if (jobSave == null || jobSave?.isCompleted != false) {
                jobSave = viewModelScope.launch {
                    isSaving.value = true
                    repository.insertDisfunction(disf)
                    isSaving.value = false
                }
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
    }
}