package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R

open class DisfunctionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class DisfunctionItemCategoryViewHolder(itemView: View):
    DisfunctionItemViewHolder(itemView) {

    private var tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
    private var ibCollapseExpand: AppCompatImageButton = itemView.findViewById(R.id.ib_expand_collapse)

    fun bind(disfunctionListItemCategory: DisfunctionListItemCategory) {
        tvCategoryName.text = itemView.context.getString(disfunctionListItemCategory.nameStringId)
    }
}

class DisfunctionItemDataViewHolder(itemView: View):
    DisfunctionItemViewHolder(itemView) {

    private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)

    fun bind(disfunctionListItemData: DisfunctionListItemData) {
        tvDescription.text = disfunctionListItemData.disfunction.description
    }
}