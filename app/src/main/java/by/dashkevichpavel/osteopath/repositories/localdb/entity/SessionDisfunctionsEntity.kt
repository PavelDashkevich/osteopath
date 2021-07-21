package by.dashkevichpavel.osteopath.repositories.localdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import by.dashkevichpavel.osteopath.repositories.localdb.DbContract

@Entity(
    tableName = DbContract.SessionDisfunctions.TABLE_NAME,
    indices = [
        Index(
            DbContract.SessionDisfunctions.COLUMN_NAME_SESSION_ID,
            DbContract.SessionDisfunctions.COLUMN_NAME_DISFUNCTION_ID
        )
              ],
    primaryKeys = [
        DbContract.SessionDisfunctions.COLUMN_NAME_SESSION_ID,
        DbContract.SessionDisfunctions.COLUMN_NAME_DISFUNCTION_ID
    ]
)
data class SessionDisfunctionsEntity(
    @ColumnInfo(name = DbContract.SessionDisfunctions.COLUMN_NAME_SESSION_ID)
    var sessionId: Long = 0,

    @ColumnInfo(name = DbContract.SessionDisfunctions.COLUMN_NAME_DISFUNCTION_ID)
    var disfunctionId: Long = 0
)
