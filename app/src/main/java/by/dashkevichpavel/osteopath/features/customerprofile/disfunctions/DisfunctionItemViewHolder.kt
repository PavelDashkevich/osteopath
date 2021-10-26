package by.dashkevichpavel.osteopath.features.customerprofile.disfunctions

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemDisfunctionCategoryBinding
import by.dashkevichpavel.osteopath.databinding.ListitemDisfunctionDataBinding
import com.google.android.material.card.MaterialCardView

abstract class DisfunctionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(disfunctionListItem: DisfunctionListItem)
}

class DisfunctionItemCategoryViewHolder(
    itemView: View,
    private val categoryCollapseExpandClickListener: DisfunctionCategoryCollapseExpandClickListener
): DisfunctionItemViewHolder(itemView) {
    private val binding = ListitemDisfunctionCategoryBinding.bind(itemView)
    private var isCollapsed: Boolean = false

    override fun bind(disfunctionListItem: DisfunctionListItem) {
        if (disfunctionListItem !is DisfunctionListItemCategory) { return }

        val disfunctionListItemCategory: DisfunctionListItemCategory = disfunctionListItem

        binding.tvCategoryName.text =
            itemView.context.getString(disfunctionListItemCategory.disfunctionStatus.nameStringId)

        isCollapsed = disfunctionListItemCategory.collapsed

        if (disfunctionListItemCategory.isEmpty) {
            binding.ibExpandCollapse.visibility = View.GONE
            binding.tvCategoryName.isEnabled = false
        } else {
            binding.ibExpandCollapse.visibility = View.VISIBLE
            binding.tvCategoryName.isEnabled = true

            setCollapseExpandDrawable()

            binding.ibExpandCollapse.setOnClickListener {
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

        binding.ibExpandCollapse.setImageDrawable(
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
    private val disfunctionClickListener: DisfunctionClickListener,
    private val disfunctionContextMenuClickListener: DisfunctionContextMenuClickListener
    ) : DisfunctionItemViewHolder(itemView) {
    private val binding = ListitemDisfunctionDataBinding.bind(itemView)

    override fun bind(disfunctionListItem: DisfunctionListItem) {
        if (disfunctionListItem !is DisfunctionListItemData) {
            return
        }

        val disfunctionListItemData: DisfunctionListItemData = disfunctionListItem

        binding.tvDescription.text = disfunctionListItemData.disfunction.description

        binding.cvCard.setOnClickListener {
            disfunctionClickListener.onDisfunctionClick(
                disfunctionListItemData.disfunction.customerId,
                disfunctionListItemData.disfunction.id
            )
        }

        binding.ibContextActions.setOnClickListener {
            disfunctionContextMenuClickListener.onDisfunctionContextMenuClick(
                disfunctionListItem.disfunction,
                itemView
            )
        }
    }
}