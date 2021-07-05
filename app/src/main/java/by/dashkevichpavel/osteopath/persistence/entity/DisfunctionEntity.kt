package by.dashkevichpavel.osteopath.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus
import by.dashkevichpavel.osteopath.persistence.DbContract

@Entity(
    tableName = DbContract.Disfunctions.TABLE_NAME,
    indices = [Index(DbContract.Disfunctions.COLUMN_NAME_ID)]
)
data class DisfunctionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbContract.Disfunctions.COLUMN_NAME_ID)
    val id: Long = 0,

    @ColumnInfo(name = DbContract.Disfunctions.COLUMN_NAME_DESCRIPTION)
    var description: String = "",

    @ColumnInfo(name = DbContract.Disfunctions.COLUMN_NAME_DISFUNCTION_STATUS_ID)
    var disfunctionStatusId: Int = DisfunctionStatus.WORK.id,

    @ColumnInfo(name = DbContract.Disfunctions.COLUMN_NAME_CUSTOMER_ID)
    var customerId: Long = 0
) {
    constructor(disfunction: Disfunction) : this(
        id = disfunction.id,
        description = disfunction.description,
        disfunctionStatusId = disfunction.disfunctionStatusId,
        customerId = disfunction.customerId
    )
}