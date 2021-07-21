package by.dashkevichpavel.osteopath.features.customerprofile.sessions

import androidx.recyclerview.widget.DiffUtil
import by.dashkevichpavel.osteopath.model.Session

class SessionsListDiffUtil(
    private val oldItems: List<Session>,
    private val newItems: List<Session>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].id == newItems[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].isContentTheSame(newItems[newItemPosition])

}