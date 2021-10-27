package by.dashkevichpavel.osteopath.helpers.itemdeletion

import by.dashkevichpavel.osteopath.features.dialogs.DialogUserAction

interface DeletableInterface {
    fun onItemDelete(itemId: Long, userAction: DialogUserAction)
}