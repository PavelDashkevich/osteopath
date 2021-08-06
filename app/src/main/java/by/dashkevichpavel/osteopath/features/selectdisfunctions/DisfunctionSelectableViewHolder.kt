package by.dashkevichpavel.osteopath.features.selectdisfunctions

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.customerprofile.disfunctions.DisfunctionsListSelectableItemData
import by.dashkevichpavel.osteopath.model.Disfunction
import com.google.android.material.checkbox.MaterialCheckBox

class DisfunctionSelectableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
    private val cbSelect: MaterialCheckBox = itemView.findViewById(R.id.cb_select)

    fun bind(disfunctionItem: DisfunctionsListSelectableItemData, disfunctionCheckedChangeListener: DisfunctionCheckedChangeListener) {
        tvDescription.text = disfunctionItem.disfunction.description
        cbSelect.isChecked = disfunctionItem.isSelected
        cbSelect.setOnCheckedChangeListener { _, isChecked ->
            disfunctionCheckedChangeListener.onCheckedChange(disfunctionItem.disfunction.id, isChecked)
        }
    }
}