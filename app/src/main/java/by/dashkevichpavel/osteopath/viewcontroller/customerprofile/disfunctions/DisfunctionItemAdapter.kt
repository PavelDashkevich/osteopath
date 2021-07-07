package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus
import by.dashkevichpavel.osteopath.model.DisfunctionStatusHelper

class DisfunctionItemAdapter(
    var disfunctionItems: MutableList<DisfunctionListItem>
) : RecyclerView.Adapter<DisfunctionItemViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return if (disfunctionItems[position] is DisfunctionListItemCategory) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisfunctionItemViewHolder {
        if (viewType == 0) {
            return DisfunctionItemCategoryViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.listitem_disfunction_category, parent, false)
            )
        }

        return DisfunctionItemDataViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_disfunction_data, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DisfunctionItemViewHolder, position: Int) {
        when (holder) {
            is DisfunctionItemCategoryViewHolder -> {
                holder.bind(disfunctionItems[position] as DisfunctionListItemCategory)
            }
            is DisfunctionItemDataViewHolder -> {
                holder.bind(disfunctionItems[position] as DisfunctionListItemData)
            }
        }
    }

    override fun getItemCount(): Int = disfunctionItems.size

    fun setItems(newItems: List<Disfunction>) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        val disfunctionsByStatus = newItems.groupBy { it.disfunctionStatusId }
        val newList: MutableList<DisfunctionListItem> = mutableListOf()

        for (key in disfunctionsByStatus.keys) {
            newList.add(DisfunctionListItemCategory(DisfunctionStatusHelper.getNameStringIdById(key)))

            disfunctionsByStatus[key]?.let { listOfDisfunctions ->
                newList.addAll(
                    listOfDisfunctions.map { disfunction ->
                        DisfunctionListItemData(disfunction)
                    }
                )
            }
        }

        val result = DiffUtil.calculateDiff(DisfunctionListDiffUtil(disfunctionItems, newList))
        disfunctionItems.clear()
        disfunctionItems.addAll(newList)
        result.dispatchUpdatesTo(this)
        //notifyDataSetChanged()
    }
}