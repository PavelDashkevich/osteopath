package by.dashkevichpavel.osteopath.repositories.sharedprefs

import android.content.Context

class PermissionRationaleSharedPreferences(
    private val context: Context,
    private val key: String
) {
    fun save(value: Boolean) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun read(): Boolean {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }

    companion object {
        const val SHARED_PREFS_NAME = "permissions_rationales"
    }
}

interface PermissionRationale {
    fun getValue(): Boolean
    fun setValue(value: Boolean)
}

class ReadContactsRationaleSharedPreferences(
    context: Context
) : PermissionRationale {
    private val sharedPreferences = PermissionRationaleSharedPreferences(
        context,
        KEY_READ_CONTACTS_PERMISSION
    )

    override fun getValue(): Boolean = sharedPreferences.read()
    override fun setValue(value: Boolean) = sharedPreferences.save(value)

    companion object {
        const val KEY_READ_CONTACTS_PERMISSION = "KEY_READ_CONTACTS_PERMISSION"
    }
}