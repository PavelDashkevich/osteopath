package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import com.google.android.material.card.MaterialCardView

open class DisfunctionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class DisfunctionItemCategoryViewHolder(
    itemView: View,
    private val categoryCollapseExpandClickListener: DisfunctionCategoryCollapseExpandClickListener
): DisfunctionItemViewHolder(itemView) {

    private var tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
    private var ibCollapseExpand: AppCompatImageButton = itemView.findViewById(R.id.ib_expand_collapse)

    private var isCollapsed: Boolean = false

    fun bind(disfunctionListItemCategory: DisfunctionListItemCategory) {
        tvCategoryName.text =
            itemView.context.getString(disfunctionListItemCategory.disfunctionStatus.nameStringId)

        isCollapsed = disfunctionListItemCategory.collapsed

        if (disfunctionListItemCategory.isEmpty) {
            ibCollapseExpand.visibility = View.GONE
            tvCategoryName.isEnabled = false
        } else {
            ibCollapseExpand.visibility = View.VISIBLE
            tvCategoryName.isEnabled = true

            setCollapseExpandDrawable()

            ibCollapseExpand.setOnClickListener {
                setCollapseExpandDrawable(true)
                categoryCollapseExpandClickListener
                    .onCategoryClick(disfunctionListItemCategory.disfunctionStatus)
            }
        }
    }

    private fun setCollapseExpandDrawable(changeCollapsedStatus: Boolean = false) {
        if (changeCollapsedStatus) {
            isCollapsed = !isCollapsed
        }

        ibCollapseExpand.setImageDrawable(
            ContextCompat.getDrawable(
                itemView.context,
                if (isCollapsed) {
                    R.drawable.ic_baseline_keyboard_arrow_down_24
                } else {
                    R.drawable.ic_baseline_keyboard_arrow_up_24
                }
            )
        )
    }
}

class DisfunctionItemDataViewHolder(
    itemView: View,
    private val disfunctionClickListener: DisfunctionClickListener):
    DisfunctionItemViewHolder(itemView) {

    private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
    private val cvCard: MaterialCardView = itemView.findViewById(R.id.cv_card)

    fun bind(disfunctionListItemData: DisfunctionListItemData) {
        tvDescription.text = disfunctionListItemData.disfunction.description

        cvCard.setOnClickListener {
            disfunctionClickListener.onDisfunctionClick(
                disfunctionListItemData.disfunction.customerId,
                disfunctionListItemData.disfunction.id
            )
        }
    }
}