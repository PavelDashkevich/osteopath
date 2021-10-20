package by.dashkevichpavel.osteopath.repositories.sharedprefs

import android.content.Context
import android.util.Log
import by.dashkevichpavel.osteopath.model.FilterValues

class CustomerFilterSharedPreferences(
    private var context: Context
) {
    private var filterValues = FilterValues()
    private var filterValuesByKey: MutableMap<String, Boolean> = mutableMapOf()

    init {
        //Log.d("OsteoApp", "CustomerFilterSharedPreferences: init()")
        fillFilterValuesByKey()
    }

    private fun fillFilterValuesByKey() {
        filterValuesByKey[KEY_FILTER_BY_AGE_CHILDREN] = filterValues.byAgeChildren
        filterValuesByKey[KEY_FILTER_BY_AGE_ADULTS] = filterValues.byAgeAdults
        filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK] = filterValues.byCategoryWork
        filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK_DONE] = filterValues.byCategoryWorkDone
        filterValuesByKey[KEY_FILTER_BY_CATEGORY_NO_HELP] = filterValues.byCategoryNoHelp
        filterValuesByKey[KEY_FILTER_SHOW_ARCHIVED] = filterValues.showArchived
    }

    private fun fillFilterValues() {
        filterValues = FilterValues(
            byAgeChildren = filterValuesByKey[KEY_FILTER_BY_AGE_CHILDREN] ?: false,
            byAgeAdults = filterValuesByKey[KEY_FILTER_BY_AGE_ADULTS] ?: false,
            byCategoryWork = filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK] ?: false,
            byCategoryWorkDone = filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK_DONE] ?: false,
            byCategoryNoHelp = filterValuesByKey[KEY_FILTER_BY_CATEGORY_NO_HELP] ?: false,
            showArchived = filterValuesByKey[KEY_FILTER_SHOW_ARCHIVED] ?: false
        )
    }

    fun loadValues(): FilterValues {
        //Log.d("OsteoApp", "CustomerFilterSharedPreferences: loadValues()")
        for(key in filterValuesByKey.keys) {
            filterValuesByKey[key] = context
                .getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(key, false)
        }

        fillFilterValues()

        return filterValues
    }

    fun saveValues(newFilterValues: FilterValues) {
        //Log.d("OsteoApp", "CustomerFilterSharedPreferences: saveValues()")

        filterValues = newFilterValues
        fillFilterValuesByKey()

        for(key in filterValuesByKey.keys) {
            context
                .getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, filterValuesByKey[key] ?: false)
                .apply()
        }
    }

    companion object {
        const val SHARED_PREFS_NAME = "filter_settings"

        const val KEY_FILTER_BY_AGE_CHILDREN = "FILTER_BY_AGE_CHILDREN"
        const val KEY_FILTER_BY_AGE_ADULTS = "FILTER_BY_AGE_ADULTS"
        const val KEY_FILTER_BY_CATEGORY_WORK = "FILTER_BY_CATEGORY_WORK"
        const val KEY_FILTER_BY_CATEGORY_WORK_DONE = "FILTER_BY_CATEGORY_WORK_DONE"
        const val KEY_FILTER_BY_CATEGORY_NO_HELP = "FILTER_BY_CATEGORY_NO_HELP"
        const val KEY_FILTER_SHOW_ARCHIVED = "FILTER_SHOW_ARCHIVED"
    }
}