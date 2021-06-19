package by.dashkevichpavel.osteopath.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.dashkevichpavel.osteopath.persistence.DbContract

@Entity(
    tableName = DbContract.Disfunction.TABLE_NAME,
    indices = [Index(DbContract.Disfunction.COLUMN_NAME_ID)]
)
data class DisfunctionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbContract.Disfunction.COLUMN_NAME_ID)
    val id: Int,

    @ColumnInfo(name = DbContract.Disfunction.COLUMN_NAME_DESCRIPTION)
    var description: String,

    @ColumnInfo(name = DbContract.Disfunction.COLUMN_NAME_DISFUNCTION_STATUS_ID)
    var disfunctionStatusId: Int,

    @ColumnInfo(name = DbContract.Disfunction.COLUMN_NAME_CUSTOMER_ID)
    var customerId: Int
)
