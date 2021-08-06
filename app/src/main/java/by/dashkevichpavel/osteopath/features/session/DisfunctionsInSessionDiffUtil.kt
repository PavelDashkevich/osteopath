package by.dashkevichpavel.osteopath.features.session

import androidx.recyclerview.widget.DiffUtil
import by.dashkevichpavel.osteopath.model.Disfunction

class DisfunctionsInSessionDiffUtil(
    private val oldItems: List<Disfunction>,
    private val newItems: List<Disfunction>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].isTheSameById(newItems[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}