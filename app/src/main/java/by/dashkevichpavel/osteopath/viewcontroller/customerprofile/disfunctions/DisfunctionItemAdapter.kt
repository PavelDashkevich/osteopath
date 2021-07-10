package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus

class DisfunctionItemAdapter(
    var disfunctionItems: MutableList<DisfunctionListItem>,
    private val disfunctionCategoryCollapseExpandClickListener: DisfunctionCategoryCollapseExpandClickListener,
    private val disfunctionClickListener: DisfunctionClickListener
) : RecyclerView.Adapter<DisfunctionItemViewHolder>() {
    private val disfunctionCategories: MutableList<DisfunctionListItemCategory> =
        DisfunctionStatus.values().map {
            DisfunctionListItemCategory(it, false)
        } as MutableList<DisfunctionListItemCategory>

    override fun getItemViewType(position: Int): Int {
        return if (disfunctionItems[position] is DisfunctionListItemCategory) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisfunctionItemViewHolder {
        if (viewType == 0) {
            return DisfunctionItemCategoryViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.listitem_disfunction_category, parent, false),
                disfunctionCategoryCollapseExpandClickListener
            )
        }

        return DisfunctionItemDataViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_disfunction_data, parent, false),
            disfunctionClickListener
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

    fun setItems(newItems: List<Disfunction>, changeStateOfCategory: DisfunctionStatus? = null) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        val newList: MutableList<DisfunctionListItem> = mutableListOf()

        for (disfunctionCategoryItem in disfunctionCategories) {
            if (disfunctionCategoryItem.disfunctionStatus == changeStateOfCategory) {
                disfunctionCategoryItem.collapsed = !disfunctionCategoryItem.collapsed
            }

            val subList = newItems.filter { disfunction ->
                disfunction.disfunctionStatusId == disfunctionCategoryItem.disfunctionStatus.id
            }

            disfunctionCategoryItem.isEmpty = subList.isEmpty()

            newList.add(disfunctionCategoryItem.copy())

            if (subList.isNotEmpty() && !disfunctionCategoryItem.collapsed) {
                newList.addAll(subList.map { disfunction ->
                    DisfunctionListItemData(disfunction)
                })
            }
        }

        val result = DiffUtil.calculateDiff(DisfunctionsListDiffUtil(disfunctionItems, newList))
        disfunctionItems.clear()
        disfunctionItems.addAll(newList)
        result.dispatchUpdatesTo(this)
    }
}