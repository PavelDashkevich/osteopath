package by.dashkevichpavel.osteopath.features.selectdisfunctions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.customerprofile.disfunctions.DisfunctionsListSelectableItemData
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil
import by.dashkevichpavel.osteopath.model.Disfunction

class DisfunctionSelectableAdapter(
    private val disfunctionCheckedChangeListener: DisfunctionCheckedChangeListener
) : RecyclerView.Adapter<DisfunctionSelectableViewHolder>() {
    private val disfunctionItems: MutableList<DisfunctionsListSelectableItemData> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DisfunctionSelectableViewHolder =
        DisfunctionSelectableViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_disfunction_selectable, parent, false)
        )

    override fun onBindViewHolder(holder: DisfunctionSelectableViewHolder, position: Int) {
        holder.bind(disfunctionItems[position], disfunctionCheckedChangeListener)
    }

    override fun getItemCount(): Int = disfunctionItems.size

    fun setItems(newItems: List<Disfunction>, selectedIds: List<Long>) {
        val newList = newItems.map {  disfunction ->
            DisfunctionsListSelectableItemData(disfunction, false)
        }
        selectedIds.forEach { id ->
            newList.firstOrNull { newItem ->
                newItem.disfunction.id == id
            }?.let { newItem ->
                newItem.isSelected = true
            }
        }

        val result = DiffUtil.calculateDiff(DefaultDiffUtil(disfunctionItems, newList))
        disfunctionItems.clear()
        disfunctionItems.addAll(newList)
        result.dispatchUpdatesTo(this)
    }
}