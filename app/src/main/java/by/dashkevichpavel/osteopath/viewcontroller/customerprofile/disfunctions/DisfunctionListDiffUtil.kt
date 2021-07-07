package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import androidx.recyclerview.widget.DiffUtil

class DisfunctionListDiffUtil(
    private var oldItems: List<DisfunctionListItem>,
    private var newItems: List<DisfunctionListItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        var res = false

        if (oldItems[oldItemPosition] is DisfunctionListItemCategory &&
                newItems[newItemPosition] is DisfunctionListItemCategory) {
            res = ((oldItems[oldItemPosition] as DisfunctionListItemCategory).nameStringId ==
                    (newItems[newItemPosition] as DisfunctionListItemCategory).nameStringId)
        } else if (oldItems[oldItemPosition] is DisfunctionListItemData &&
            newItems[newItemPosition] is DisfunctionListItemData) {
            res = ((oldItems[oldItemPosition] as DisfunctionListItemData).disfunction.id ==
                    (newItems[newItemPosition] as DisfunctionListItemData).disfunction.id)
        }

        return res
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        var res = false

        if (oldItems[oldItemPosition] is DisfunctionListItemCategory &&
            newItems[newItemPosition] is DisfunctionListItemCategory) {
            res = ((oldItems[oldItemPosition] as DisfunctionListItemCategory).nameStringId ==
                    (newItems[newItemPosition] as DisfunctionListItemCategory).nameStringId)
        } else if (oldItems[oldItemPosition] is DisfunctionListItemData &&
            newItems[newItemPosition] is DisfunctionListItemData) {
            val oldDisfunction = (oldItems[oldItemPosition] as DisfunctionListItemData).disfunction
            val newDisfunction = (newItems[newItemPosition] as DisfunctionListItemData).disfunction

            res = (oldDisfunction.id == newDisfunction.id &&
                    oldDisfunction.description == newDisfunction.description)
        }

        return res
    }
}