package by.dashkevichpavel.osteopath.features.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import by.dashkevichpavel.osteopath.R

class CustomerDeleteConfirmationDialog :
    DialogFragment(),
    DialogInterface.OnClickListener {
    private var customerId: Long = 0L

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var customerName = ""

        if (savedInstanceState == null) {
            arguments?.let { args ->
                customerId = args.getLong(BUNDLE_KEY_CUSTOMER_ID, 0L)
                customerName = args.getString(BUNDLE_KEY_CUSTOMER_NAME, "")
            }
        }

        return AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.customer_delete_dialog_message, customerName))
            .setPositiveButton(R.string.customer_delete_dialog_button_delete, this)
            .setNegativeButton(R.string.customer_delete_dialog_button_cancel, this)
            .setNeutralButton(R.string.customer_delete_dialog_button_archive, this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        setFragmentResult(
            KEY_RESULT,
            packResultBundle(
                this,
                when (button) {
                    DialogInterface.BUTTON_POSITIVE -> UserAction.DELETE
                    DialogInterface.BUTTON_NEGATIVE -> UserAction.CANCEL
                    else -> UserAction.ARCHIVE
                }
            )
        )
    }

    companion object {
        const val KEY_RESULT = "CUSTOMER_DELETE_CONFIRMATION"
        private const val BUNDLE_KEY_CUSTOMER_NAME = "CUSTOMER_NAME"
        private const val BUNDLE_KEY_CUSTOMER_ID = "CUSTOMER_ID"
        private const val BUNDLE_KEY_USER_ACTION_ID = "USER_ACTION_ID"

        private fun packArgsBundle(customerName: String, customerId: Long): Bundle {
            return Bundle().apply {
                putLong(BUNDLE_KEY_CUSTOMER_ID, customerId)
                putString(BUNDLE_KEY_CUSTOMER_NAME, customerName)
            }
        }

        private fun packResultBundle(
            dialog: CustomerDeleteConfirmationDialog,
            userAction: UserAction
        ): Bundle {
            return Bundle().apply {
                putLong(BUNDLE_KEY_CUSTOMER_ID, dialog.customerId)
                putInt(BUNDLE_KEY_USER_ACTION_ID, userAction.ordinal)
            }
        }

        fun show(
            fragmentManager: FragmentManager, tag: String, customerName: String, customerId: Long
        ) {
            val fragment = CustomerDeleteConfirmationDialog()
            fragment.arguments = packArgsBundle(customerName, customerId)
            fragment.show(fragmentManager, tag)
        }

        fun extractResult(bundle: Bundle): Pair<Long, UserAction> {
            return bundle.getLong(BUNDLE_KEY_CUSTOMER_ID, 0L) to
                    UserAction
                        .values()[bundle.getInt(BUNDLE_KEY_USER_ACTION_ID, UserAction.CANCEL.ordinal)]
        }
    }

    enum class UserAction() {
        DELETE,
        ARCHIVE,
        CANCEL
    }
}