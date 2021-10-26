package by.dashkevichpavel.osteopath.features.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import by.dashkevichpavel.osteopath.R

class ItemDeleteConfirmationDialog :
    DialogFragment(),
    DialogInterface.OnClickListener {
    private var itemId: Long = 0L
    private var dialogMessage: String = ""
    private var neutralButtonTextResId: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let { args ->
            itemId = args.getLong(BUNDLE_KEY_ITEM_ID, 0L)
            dialogMessage = args.getString(BUNDLE_KEY_MESSAGE, "")
            neutralButtonTextResId = args.getInt(BUNDLE_KEY_NEUTRAL_BUTTON_TEXT_RES_ID, 0)
        }

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setMessage(dialogMessage)
            .setPositiveButton(R.string.dialog_button_delete, this)
            .setNegativeButton(R.string.dialog_button_cancel, this)

        if (neutralButtonTextResId != 0) {
            dialogBuilder.setNeutralButton(neutralButtonTextResId, this)
        }

        return dialogBuilder.create()
    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        val dialogUserAction = when (button) {
            DialogInterface.BUTTON_POSITIVE -> DialogUserAction.POSITIVE
            DialogInterface.BUTTON_NEUTRAL -> DialogUserAction.NEUTRAL
            else -> DialogUserAction.NEGATIVE
        }

        setFragmentResult(
            KEY_RESULT,
            Bundle().apply {
                putLong(BUNDLE_KEY_ITEM_ID, itemId)
                putInt(BUNDLE_KEY_USER_ACTION_ID, dialogUserAction.ordinal)
            }
        )
    }

    companion object {
        const val KEY_RESULT = "ITEM_DELETE_CONFIRMATION"
        private const val BUNDLE_KEY_ITEM_ID = "ITEM_ID"
        private const val BUNDLE_KEY_MESSAGE = "DIALOG_MESSAGE"
        private const val BUNDLE_KEY_NEUTRAL_BUTTON_TEXT_RES_ID = "NEUTRAL_BUTTON_TEXT_RES_ID"
        private const val BUNDLE_KEY_USER_ACTION_ID = "USER_ACTION_ID"

        fun show(
            fragmentManager: FragmentManager,
            itemId: Long,
            message: String,
            neutralButtonTextResId: Int = 0
        ) {
            val fragment = ItemDeleteConfirmationDialog()
            fragment.arguments = Bundle().apply {
                putLong(BUNDLE_KEY_ITEM_ID, itemId)
                putString(BUNDLE_KEY_MESSAGE, message)
                putInt(BUNDLE_KEY_NEUTRAL_BUTTON_TEXT_RES_ID, neutralButtonTextResId)
            }
            fragment.show(fragmentManager, KEY_RESULT)
        }

        fun extractResult(bundle: Bundle): Pair<Long, DialogUserAction> {
            return bundle.getLong(BUNDLE_KEY_ITEM_ID, 0L) to
                    DialogUserAction.values()[
                            bundle.getInt(
                                BUNDLE_KEY_USER_ACTION_ID,
                                DialogUserAction.NEGATIVE.ordinal
                            )
                    ]
        }
    }
}