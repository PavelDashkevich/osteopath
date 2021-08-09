package by.dashkevichpavel.osteopath.helpers.permissions

import android.Manifest
import androidx.fragment.app.Fragment
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.repositories.sharedprefs.ReadContactsRationaleSharedPreferences

class ReadContactsPermissionsHelper(
    fragment: Fragment,
    listener: PermissionsGrantedListener
) {
    private val permissionsHelper = PermissionsHelper(
        fragment,
        listener,
        ReadContactsRationaleSharedPreferences(fragment.requireContext()),
        arrayOf(Manifest.permission.READ_CONTACTS),
        R.string.permissions_function_get_data_from_contact,
        R.string.permissions_read_contact
    )

    fun requestPermissions() = permissionsHelper.requestPermissions()
}