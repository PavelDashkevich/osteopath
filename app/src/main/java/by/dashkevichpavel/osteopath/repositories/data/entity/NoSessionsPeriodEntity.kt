package by.dashkevichpavel.osteopath.repositories.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.dashkevichpavel.osteopath.model.NoSessionsPeriod
import by.dashkevichpavel.osteopath.repositories.data.DbContract
import java.util.*

@Entity(
    tableName = DbContract.NoSessionPeriods.TABLE_NAME,
    indices = [Index(DbContract.NoSessionPeriods.COLUMN_NAME_ID)]
)
data class NoSessionsPeriodEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbContract.NoSessionPeriods.COLUMN_NAME_ID)
    var id: Long = 0,

    @ColumnInfo(name = DbContract.NoSessionPeriods.COLUMN_NAME_DATE_TIME_START)
    var dateTimeStart: Date = Date(0),

    @ColumnInfo(name = DbContract.NoSessionPeriods.COLUMN_NAME_DATE_TIME_END)
    var dateTimeEnd: Date = Date(0)
) {
    constructor(noSessionsPeriod: NoSessionsPeriod) : this(
        id = noSessionsPeriod.id,
        dateTimeStart = noSessionsPeriod.dateTimeStart,
        dateTimeEnd = noSessionsPeriod.dateTimeEnd
    )
}