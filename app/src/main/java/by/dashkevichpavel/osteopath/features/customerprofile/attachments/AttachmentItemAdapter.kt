package by.dashkevichpavel.osteopath.features.customerprofile.attachments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil
import by.dashkevichpavel.osteopath.model.Attachment

class AttachmentItemAdapter : RecyclerView.Adapter<AttachmentItemViewHolder>() {
    private val attachments: MutableList<Attachment> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentItemViewHolder =
        AttachmentItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_attachment, parent, false)
        )

    override fun onBindViewHolder(holder: AttachmentItemViewHolder, position: Int) {
        holder.bind(attachments[position])
    }

    override fun getItemCount(): Int = attachments.size

    fun setItems(newItems: List<Attachment>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(attachments, newItems))
        attachments.clear()
        attachments.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}