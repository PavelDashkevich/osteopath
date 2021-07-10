package by.dashkevichpavel.osteopath.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DisfunctionViewModel(
    private val repository: OsteoDbRepository
) : ViewModel() {
    val disfunction = MutableLiveData<Disfunction?>(null)
    val isSaving = MutableLiveData<Boolean?>(null)

    private var jobSave: Job? = null

    fun selectDisfunction(customerId: Long, disfunctionId: Long) {
        if (disfunctionId == 0L) {
            disfunction.value = Disfunction(customerId = customerId)
            return
        }

        if (disfunction.value == null) {
            loadDisfunctionData(disfunctionId)
        }
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
            val loadedDisfunction = repository.getDisfunctionById(disfunctionId)

            loadedDisfunction?.let {
                disfunction.value = loadedDisfunction
            }
        }
    }
}