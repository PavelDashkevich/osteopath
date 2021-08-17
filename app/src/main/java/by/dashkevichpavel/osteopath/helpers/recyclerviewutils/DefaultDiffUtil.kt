package by.dashkevichpavel.osteopath.helpers.recyclerviewutils

import androidx.recyclerview.widget.DiffUtil

class DefaultDiffUtil<T : DiffUtilComparable>(
    private val oldItems: List<T>,
    private val newItems: List<T>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].isTheSameItemAs(newItems[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].contentsTheSameAs(newItems[newItemPosition])

}

interface DiffUtilComparable {
    fun isTheSameItemAs(item: DiffUtilComparable?): Boolean
    fun contentsTheSameAs(item: DiffUtilComparable?): Boolean
}