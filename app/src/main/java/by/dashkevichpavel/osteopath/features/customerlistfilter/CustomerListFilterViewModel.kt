package by.dashkevichpavel.osteopath.features.customerlistfilter

import android.content.Context
import androidx.lifecycle.ViewModel
import by.dashkevichpavel.osteopath.model.FilterValues
import by.dashkevichpavel.osteopath.repositories.sharedprefs.CustomerFilterSharedPreferences

class CustomerListFilterViewModel(context: Context) : ViewModel() {
    private val customerFilterSharedPreferences = CustomerFilterSharedPreferences(context)

    private var filterValues = FilterValues()

    fun loadFilterValues(): FilterValues {
        filterValues = customerFilterSharedPreferences.loadValues()
        return filterValues
    }

    fun saveFilterValues() = customerFilterSharedPreferences.saveValues(filterValues)

    fun changeFilterValue(
        byAgeChildren: Boolean? = null,
        byAgeAdults: Boolean? = null,
        byCategoryWork: Boolean? = null,
        byCategoryWorkDone: Boolean? = null,
        byCategoryNoHelp: Boolean? = null) {
        filterValues.byAgeChildren = byAgeChildren ?: filterValues.byAgeChildren
        filterValues.byAgeAdults = byAgeAdults ?: filterValues.byAgeAdults
        filterValues.byCategoryWork = byCategoryWork ?: filterValues.byCategoryWork
        filterValues.byCategoryWorkDone = byCategoryWorkDone ?: filterValues.byCategoryWorkDone
        filterValues.byCategoryNoHelp = byCategoryNoHelp ?: filterValues.byCategoryNoHelp
    }
}