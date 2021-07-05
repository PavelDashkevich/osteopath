package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R

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
}