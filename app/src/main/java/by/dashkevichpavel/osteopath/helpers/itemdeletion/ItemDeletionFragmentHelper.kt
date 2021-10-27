package by.dashkevichpavel.osteopath.helpers.itemdeletion

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.features.dialogs.ItemDeleteConfirmationDialog

class ItemDeletionFragmentHelper(
    private val fragment: Fragment,
    private val handler: DeletableInterface,
    private val deletionInList: Boolean = true
) {
    init {
        fragment.childFragmentManager.setFragmentResultListener(
            ItemDeleteConfirmationDialog.KEY_RESULT,
            fragment.viewLifecycleOwner,
            this::onItemDeleteConfirm
        )
    }

    fun showDialog(itemId: Long, message: String, neutralButtonTextResId: Int = 0) {
        ItemDeleteConfirmationDialog.show(fragment.childFragmentManager, itemId, message,
            neutralButtonTextResId)
    }

    private fun onItemDeleteConfirm(key: String, bundle: Bundle) {
        if (key != ItemDeleteConfirmationDialog.KEY_RESULT) return

        val result = ItemDeleteConfirmationDialog.extractResult(bundle)
        handler.onItemDelete(result.first, result.second)
        if (!deletionInList) fragment.findNavController().navigateUp()
    }
}