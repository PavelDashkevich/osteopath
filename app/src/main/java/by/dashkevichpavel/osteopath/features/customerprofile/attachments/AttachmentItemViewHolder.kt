package by.dashkevichpavel.osteopath.features.customerprofile.attachments

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.databinding.ListitemAttachmentBinding
import by.dashkevichpavel.osteopath.helpers.loadThumbnailFromAttachmentByMimeType
import by.dashkevichpavel.osteopath.model.Attachment
import com.bumptech.glide.Glide

class AttachmentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListitemAttachmentBinding.bind(itemView)

    fun bind(attachment: Attachment) {
        Glide
            .with(itemView.context)
            .load(attachment.path)
            .thumbnail(
                Glide
                    .with(itemView.context)
                    .loadThumbnailFromAttachmentByMimeType(itemView.context, attachment)
                    //.override(150, 150)
            )
            .fitCenter()
            .into(binding.ivPreview)
    }
}