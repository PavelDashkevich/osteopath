package by.dashkevichpavel.osteopath.features.selectdisfunctions

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemDisfunctionSelectableBinding
import by.dashkevichpavel.osteopath.features.customerprofile.disfunctions.DisfunctionsListSelectableItemData
import com.google.android.material.checkbox.MaterialCheckBox

class DisfunctionSelectableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListitemDisfunctionSelectableBinding.bind(itemView)

    fun bind(disfunctionItem: DisfunctionsListSelectableItemData, disfunctionCheckedChangeListener: DisfunctionCheckedChangeListener) {
        binding.tvDescription.text = disfunctionItem.disfunction.description
        binding.cbSelect.isChecked = disfunctionItem.isSelected
        binding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            disfunctionCheckedChangeListener.onCheckedChange(disfunctionItem.disfunction.id, isChecked)
        }
    }
}