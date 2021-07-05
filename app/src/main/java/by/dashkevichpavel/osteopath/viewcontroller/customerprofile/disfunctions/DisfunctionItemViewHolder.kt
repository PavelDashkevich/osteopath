package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class DisfunctionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class DisfunctionItemCategoryViewHolder(itemView: View):
    DisfunctionItemViewHolder(itemView) {

    fun bind(disfunctionListItemCategory: DisfunctionListItemCategory) {

    }
}

class DisfunctionItemDataViewHolder(itemView: View):
    DisfunctionItemViewHolder(itemView) {

    fun bind(disfunctionListItemData: DisfunctionListItemData) {

    }
}