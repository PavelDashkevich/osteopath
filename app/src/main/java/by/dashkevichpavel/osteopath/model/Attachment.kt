package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable
import by.dashkevichpavel.osteopath.repositories.data.entity.AttachmentEntity

data class Attachment(
    var id: Long = 0,
    var customerId: Long = 0,
    var thumbnail: String = "",
    var path: String = "",
    var mimeType: String = ""
) : DiffUtilComparable {
    constructor(attachmentEntity: AttachmentEntity) : this(
        id = attachmentEntity.id,
        customerId = attachmentEntity.customer_id,
        thumbnail = attachmentEntity.thumbnail,
        path = attachmentEntity.path,
        mimeType = attachmentEntity.mimeType
    )

    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is Attachment) return false

        return id == item.id
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item !is Attachment) return false

        return (id == item.id) && (thumbnail == item.thumbnail) && (path == item.path)
    }
}