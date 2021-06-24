package by.dashkevichpavel.osteopath

import android.content.Context
import android.util.Log
import by.dashkevichpavel.osteopath.model.FilterValues

class CustomerFilterSharedPreferences(
    private var context: Context
) {
    private var filterValuesByKey: MutableMap<String, Boolean> = mutableMapOf()
    private var filterKeysByViewId: MutableMap<Int, String> = mutableMapOf()
    var filterValues = FilterValues()

    init {
        Log.d("OsteoApp", "CustomerFilterSharedPreferences: init()")
        filterValuesByKey[KEY_FILTER_BY_AGE_CHILDREN] = false
        filterValuesByKey[KEY_FILTER_BY_AGE_ADULTS] = false
        filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK] = false
        filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK_DONE] = false
        filterValuesByKey[KEY_FILTER_BY_CATEGORY_NO_HELP] = false
    }

    fun mapIdsToKeys(
        idByAgeChildren: Int,
        idByAgeAdults: Int,
        idByCategoryWork: Int,
        idByCategoryWorkDone: Int,
        idByCategoryNoHelp: Int
    ) {
        Log.d("OsteoApp", "CustomerFilterSharedPreferences: mapIdsToKeys($idByAgeChildren, $idByAgeAdults, $idByCategoryWork, $idByCategoryWorkDone, $idByCategoryNoHelp)")
        filterKeysByViewId[idByAgeChildren] = KEY_FILTER_BY_AGE_CHILDREN
        filterKeysByViewId[idByAgeAdults] = KEY_FILTER_BY_AGE_ADULTS
        filterKeysByViewId[idByCategoryWork] = KEY_FILTER_BY_CATEGORY_WORK
        filterKeysByViewId[idByCategoryWorkDone] = KEY_FILTER_BY_CATEGORY_WORK_DONE
        filterKeysByViewId[idByCategoryNoHelp] = KEY_FILTER_BY_CATEGORY_NO_HELP
    }

    fun saveValue(viewId: Int, value: Boolean) {
        Log.d("OsteoApp", "CustomerFilterSharedPreferences: saveValue()")
        filterKeysByViewId[viewId]?.let { key ->
            filterValuesByKey[key] = value
        }
    }

    fun getValueByViewId(viewId: Int): Boolean {
        Log.d("OsteoApp", "CustomerFilterSharedPreferences: getValueByViewId()")
        filterKeysByViewId[viewId]?.let { key ->
            return filterValuesByKey[key] ?: false
        }

        return false
    }

    fun loadValues() {
        Log.d("OsteoApp", "CustomerFilterSharedPreferences: loadValues()")
        for(key in filterValuesByKey.keys) {
            filterValuesByKey[key] = context
                .getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(key, false)
        }

        filterValues = FilterValues(
            byAgeChildren = filterValuesByKey[KEY_FILTER_BY_AGE_CHILDREN] ?: false,
            byAgeAdults = filterValuesByKey[KEY_FILTER_BY_AGE_ADULTS] ?: false,
            byCategoryWork = filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK] ?: false,
            byCategoryWorkDone = filterValuesByKey[KEY_FILTER_BY_CATEGORY_WORK_DONE] ?: false,
            byCategoryNoHelp = filterValuesByKey[KEY_FILTER_BY_CATEGORY_NO_HELP] ?: false
        )
    }

    fun saveValues() {
        Log.d("OsteoApp", "CustomerFilterSharedPreferences: saveValues()")
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
    }
}