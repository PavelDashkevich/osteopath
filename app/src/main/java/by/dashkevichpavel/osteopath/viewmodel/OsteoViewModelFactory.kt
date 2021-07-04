package by.dashkevichpavel.osteopath.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepositorySingleton

class OsteoViewModelFactory(
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when (modelClass) {
            CustomerListViewModel::class.java ->
                CustomerListViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            CustomerProfileViewModel::class.java ->
                CustomerProfileViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            else ->
                throw IllegalArgumentException("$modelClass is not registered ViewModel")
        } as T

}