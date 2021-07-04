package by.dashkevichpavel.osteopath.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.dashkevichpavel.osteopath.persistence.DbContract

@Entity(
    tableName = DbContract.DisfunctionsInSession.TABLE_NAME,
    indices = [Index(DbContract.DisfunctionsInSession.COLUMN_NAME_ID)]
)
data class DisfunctionsInSessionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbContract.DisfunctionsInSession.COLUMN_NAME_ID)
    var id: Int = 0,

    @ColumnInfo(name = DbContract.DisfunctionsInSession.COLUMN_NAME_SESSION_ID)
    var sessionId: Int = 0,

    @ColumnInfo(name = DbContract.DisfunctionsInSession.COLUMN_NAME_DISFUNCTION_ID)
    var disfunctionId: Int = 0
)
