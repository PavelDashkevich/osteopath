package by.dashkevichpavel.osteopath.features.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import by.dashkevichpavel.osteopath.R

class DisfunctionDeleteConfirmationDialog :
    DialogFragment(),
    DialogInterface.OnClickListener {
    private var disfunctionId: Long = 0L

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState == null) {
            arguments?.let { args ->
                disfunctionId = args.getLong(BUNDLE_KEY_DISFUNCTION_ID, 0L)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.disfunction_delete_dialog_message))
            .setPositiveButton(R.string.disfunction_delete_dialog_button_positive, this)
            .setNegativeButton(R.string.disfunction_delete_dialog_button_negative, this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        setFragmentResult(
            KEY_RESULT,
            packResultBundle(
                this,
                when (button) {
                    DialogInterface.BUTTON_POSITIVE -> DialogUserAction.POSITIVE
                    else -> DialogUserAction.NEGATIVE
                }
            )
        )
    }

    companion object {
        const val KEY_RESULT = "DISFUNCTION_DELETE_CONFIRMATION"
        private const val BUNDLE_KEY_DISFUNCTION_ID = "DISFUNCTION_ID"
        private const val BUNDLE_KEY_USER_ACTION_ID = "USER_ACTION_ID"

        private fun packArgsBundle(disfunctionId: Long): Bundle {
            return Bundle().apply {
                putLong(BUNDLE_KEY_DISFUNCTION_ID, disfunctionId)
            }
        }

        private fun packResultBundle(
            dialog: DisfunctionDeleteConfirmationDialog,
            userAction: DialogUserAction
        ): Bundle {
            return Bundle().apply {
                putLong(BUNDLE_KEY_DISFUNCTION_ID, dialog.disfunctionId)
                putInt(BUNDLE_KEY_USER_ACTION_ID, userAction.ordinal)
            }
        }

        fun show(fragmentManager: FragmentManager, tag: String, disfunctionId: Long) {
            val fragment = DisfunctionDeleteConfirmationDialog()
            fragment.arguments = packArgsBundle(disfunctionId)
            fragment.show(fragmentManager, tag)
        }

        fun extractResult(bundle: Bundle): Pair<Long, DialogUserAction> {
            return bundle.getLong(BUNDLE_KEY_DISFUNCTION_ID, 0L) to
                    DialogUserAction.values()[
                            bundle.getInt(BUNDLE_KEY_USER_ACTION_ID, DialogUserAction.NEGATIVE.ordinal)
                    ]
        }
    }
}