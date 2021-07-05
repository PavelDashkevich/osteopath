package by.dashkevichpavel.osteopath.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.persistence.DbContract
import java.util.*

@Entity(
    tableName = DbContract.Sessions.TABLE_NAME,
    indices = [Index(DbContract.Sessions.COLUMN_NAME_ID)]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbContract.Sessions.COLUMN_NAME_ID)
    var id: Long = 0,

    @ColumnInfo(name = DbContract.Sessions.COLUMN_NAME_CUSTOMER_ID)
    var customerId: Long = 0,

    @ColumnInfo(name = DbContract.Sessions.COLUMN_NAME_DATE_TIME)
    var dateTime: Date = Date(0),

    @ColumnInfo(name = DbContract.Sessions.COLUMN_NAME_IS_DONE)
    var isDone: Boolean = false
) {
    constructor(session: Session) : this(
        id = session.id,
        customerId = session.customerId,
        dateTime = session.dateTime,
        isDone = session.isDone
    )
}