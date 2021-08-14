package by.dashkevichpavel.osteopath.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepositorySingleton
import by.dashkevichpavel.osteopath.features.customerlist.CustomerListViewModel
import by.dashkevichpavel.osteopath.features.customerlistfilter.CustomerListFilterViewModel
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.disfunction.DisfunctionViewModel
import by.dashkevichpavel.osteopath.features.selectdisfunctions.SelectDisfunctionsViewModel
import by.dashkevichpavel.osteopath.features.session.SessionViewModel

class OsteoViewModelFactory(
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when (modelClass) {
            CustomerListViewModel::class.java ->
                CustomerListViewModel(
                    applicationContext,
                    OsteoDbRepositorySingleton.getInstance(applicationContext)
                )
            CustomerListFilterViewModel::class.java ->
                CustomerListFilterViewModel(applicationContext)
            CustomerProfileViewModel::class.java ->
                CustomerProfileViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            DisfunctionViewModel::class.java ->
                DisfunctionViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            SessionViewModel::class.java ->
                SessionViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            SelectDisfunctionsViewModel::class.java ->
                SelectDisfunctionsViewModel(OsteoDbRepositorySingleton.getInstance(applicationContext))
            else ->
                throw IllegalArgumentException("$modelClass is not registered ViewModel")
        } as T

}