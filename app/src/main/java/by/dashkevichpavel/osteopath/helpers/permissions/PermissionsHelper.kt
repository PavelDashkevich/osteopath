package by.dashkevichpavel.osteopath.helpers.permissions

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.repositories.sharedprefs.PermissionRationale
import com.google.android.material.snackbar.Snackbar

class PermissionsHelper(
    private val fragment: Fragment,
    private val listener: PermissionsGrantedListener,
    private val rationaleShown: PermissionRationale,
    private val permissions: Array<String>,
    private val functionNameStringResId: Int,
    private val requestedPermissionsStringResId: Int
) {
    private val getPermission: ActivityResultLauncher<Array<String>> =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            if (!permissions.values.contains(false)) {
                listener.onPermissionsGranted()
            } else {
                Snackbar
                    .make(
                        fragment.requireView(),
                        R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        }

    fun requestPermissions() {
        when {
            checkSelfPermissions() -> listener.onPermissionsGranted()
            shouldShowRequestPermissionsRationale() -> showPermissionExplanationDialog()
            rationaleShown.getValue() -> showPermissionDeniedDialog()
            else -> requestAllPermissions()
        }
    }

    private fun checkSelfPermissions(): Boolean {
        var res = true

        permissions.forEach { permission ->
            res = res && (
                    ContextCompat.checkSelfPermission(fragment.requireContext(), permission) ==
                            PackageManager.PERMISSION_GRANTED
                    )
        }

        return res
    }

    private fun shouldShowRequestPermissionsRationale(): Boolean {
        var res = true

        permissions.forEach { permission ->
            res = res && fragment.shouldShowRequestPermissionRationale(permission)
        }

        return res
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(fragment.requireContext())
            .setMessage(
                fragment.requireContext().resources.getString(
                    R.string.permissions_explanation_template,
                    fragment.requireContext().resources.getString(functionNameStringResId),
                    fragment.requireContext().resources.getString(requestedPermissionsStringResId)
                )
            )
            .setPositiveButton(R.string.alert_dialog_button_yes) { _, _ ->
                rationaleShown.setValue(true)
                requestAllPermissions()
            }
            .setNegativeButton(R.string.alert_dialog_button_no) { _, _ -> }
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(fragment.requireContext())
            .setMessage(
                fragment.requireContext().resources.getString(
                    R.string.permissions_denied_template,
                    fragment.requireContext().resources.getString(functionNameStringResId),
                    fragment.requireContext().resources.getString(requestedPermissionsStringResId)
                )
            )
            .setPositiveButton(R.string.alert_dialog_button_yes) { _, _ ->
                fragment.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + fragment.requireActivity().packageName)
                    )
                )
            }
            .setNegativeButton(R.string.alert_dialog_button_no) { _, _ -> }
            .show()
    }

    private fun requestAllPermissions() {
        getPermission.launch(permissions)
    }
}

interface PermissionsGrantedListener {
    fun onPermissionsGranted()
}