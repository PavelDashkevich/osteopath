package by.dashkevichpavel.osteopath.features.selectdisfunctions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDbRepository
import kotlinx.coroutines.launch

class SelectDisfunctionsViewModel(
    private val repository: LocalDbRepository
) : ViewModel() {
    val disfunctions: MutableLiveData<List<Disfunction>> = MutableLiveData(listOf())
    val needToReturnSelectedIds: MutableLiveData<Boolean> = MutableLiveData(false)
    internal val selectedIds: MutableList<Long> = mutableListOf()

    fun loadDisfunctions(customerId: Long, excludeDisfunctionsIds: List<Long>) {
        viewModelScope.launch {
            val newDisfunctions: MutableList<Disfunction> =
                repository.getDisfunctionsByCustomerId(customerId) as MutableList<Disfunction>

            excludeDisfunctionsIds.forEach { disfunctionId ->
                newDisfunctions.removeAll { disfunction ->
                    disfunction.id == disfunctionId
                }
            }

            disfunctions.value = newDisfunctions
        }
    }

    fun onDisfunctionSelect(disfunctionId: Long, isSelected: Boolean) {
        if (isSelected) {
            selectedIds.add(disfunctionId)
        } else {
            selectedIds.remove(disfunctionId)
        }
    }

    fun navigateBack() {
        needToReturnSelectedIds.value = true
    }

    fun cancelSelection() {
        selectedIds.clear()
        needToReturnSelectedIds.value = true
    }
}