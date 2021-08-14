package by.dashkevichpavel.osteopath.features.session

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemSessionDisfunctionBinding
import by.dashkevichpavel.osteopath.model.Disfunction

class DisfunctionInSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListitemSessionDisfunctionBinding.bind(itemView)

    fun bind(disfunction: Disfunction, deleteClickListener: DisfunctionDeleteClickListener) {
        binding.tvDescription.text = disfunction.description
        binding.ibDelete.setOnClickListener {
            deleteClickListener.onDisfunctionDeleteClick(disfunction.id)
        }
    }
}