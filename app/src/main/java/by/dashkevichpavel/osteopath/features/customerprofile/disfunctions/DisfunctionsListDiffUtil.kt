package by.dashkevichpavel.osteopath.features.customerprofile.disfunctions

import android.util.Log
import androidx.recyclerview.widget.DiffUtil

class DisfunctionsListDiffUtil(
    private var oldItems: List<DisfunctionListItem>,
    private var newItems: List<DisfunctionListItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        /*var res = false

        if (oldItems[oldItemPosition] is DisfunctionListItemCategory &&
                newItems[newItemPosition] is DisfunctionListItemCategory) {
            val oldDisfunctionCategory = oldItems[oldItemPosition] as DisfunctionListItemCategory
            val newDisfunctionCategory = newItems[newItemPosition] as DisfunctionListItemCategory
            res = (oldDisfunctionCategory.disfunctionStatus == newDisfunctionCategory.disfunctionStatus)
        } else if (oldItems[oldItemPosition] is DisfunctionListItemData &&
            newItems[newItemPosition] is DisfunctionListItemData) {
            res = ((oldItems[oldItemPosition] as DisfunctionListItemData).disfunction.id ==
                    (newItems[newItemPosition] as DisfunctionListItemData).disfunction.id)
        }

        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: " +
            "oldItemPos = $oldItemPosition, newItemPos = $newItemPosition, " +
            "oldItems[oldItemPosition] = ${oldItems[oldItemPosition]}, " +
            "newItems[newItemPosition] = ${newItems[newItemPosition]}, res = $res")*/

        return oldItems[oldItemPosition].isTheSame(newItems[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        /*var res = false

        if (oldItems[oldItemPosition] is DisfunctionListItemCategory &&
            newItems[newItemPosition] is DisfunctionListItemCategory) {
            res = ((oldItems[oldItemPosition] as DisfunctionListItemCategory).isEmpty ==
                    (newItems[newItemPosition] as DisfunctionListItemCategory).isEmpty)
        } else if (oldItems[oldItemPosition] is DisfunctionListItemData &&
            newItems[newItemPosition] is DisfunctionListItemData) {
            val oldDisfunction = (oldItems[oldItemPosition] as DisfunctionListItemData).disfunction
            val newDisfunction = (newItems[newItemPosition] as DisfunctionListItemData).disfunction

            res = (oldDisfunction.id == newDisfunction.id &&
                    oldDisfunction.description == newDisfunction.description)
        }

        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: " +
                "oldItemPos = $oldItemPosition, newItemPos = $newItemPosition, " +
                "oldItems[oldItemPosition] = ${oldItems[oldItemPosition]}, " +
                "newItems[newItemPosition] = ${newItems[newItemPosition]}, res = $res")*/

        return oldItems[oldItemPosition].contentsTheSame(newItems[newItemPosition])
    }
}