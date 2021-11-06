package by.dashkevichpavel.osteopath.repositories.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.dashkevichpavel.osteopath.model.Attachment
import by.dashkevichpavel.osteopath.repositories.data.DbContract

@Entity(
    tableName = DbContract.Attachments.TABLE_NAME,
    indices = [
        Index(
            DbContract.Attachments.COLUMN_NAME_ID,
            DbContract.Attachments.COLUMN_NAME_CUSTOMER_ID
        )
    ]
)
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbContract.Attachments.COLUMN_NAME_ID)
    var id: Long = 0,

    @ColumnInfo(name = DbContract.Attachments.COLUMN_NAME_CUSTOMER_ID)
    var customer_id: Long = 0,

    @ColumnInfo(name = DbContract.Attachments.COLUMN_NAME_THUMBNAIL)
    var thumbnail: String = "",

    @ColumnInfo(name = DbContract.Attachments.COLUMN_NAME_PATH)
    var path: String = "",

    @ColumnInfo(name = DbContract.Attachments.COLUMN_NAME_MIME_TYPE)
    var mimeType: String = ""
) {
    constructor(attachment: Attachment) : this(
        customer_id = attachment.customerId,
        thumbnail = attachment.thumbnail,
        path = attachment.path,
        mimeType = attachment.mimeType
    )
}