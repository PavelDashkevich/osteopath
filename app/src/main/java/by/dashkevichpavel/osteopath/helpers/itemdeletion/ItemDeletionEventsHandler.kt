package by.dashkevichpavel.osteopath.helpers.itemdeletion

import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction

class ItemDeletionEventsHandler(
    val onItemDeleteAction: (itemId: Long, userAction: DialogUserAction) -> Unit = { _, _ -> }
) : DeletableInterface {
    override fun onItemDelete(itemId: Long, userAction: DialogUserAction) =
        onItemDeleteAction(itemId, userAction)
}