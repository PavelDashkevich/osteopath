package by.dashkevichpavel.osteopath.features.session

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.Disfunction

class DisfunctionInSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
    private val ibDelete: AppCompatImageButton = itemView.findViewById(R.id.ib_delete)

    fun bind(disfunction: Disfunction, deleteClickListener: DisfunctionDeleteClickListener) {
        tvDescription.text = disfunction.description
        ibDelete.setOnClickListener { deleteClickListener.onDisfunctionDeleteClick(disfunction.id) }
    }
}