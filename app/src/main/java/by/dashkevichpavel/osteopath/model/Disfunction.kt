package by.dashkevichpavel.osteopath.model

import androidx.room.ColumnInfo
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity

data class Disfunction(
    val id: Int = 0,
    var description: String = "",
    var disfunctionStatusId: Int = DisfunctionStatus.WORK.id,
    var customerId: Int = 0
) {
    constructor(disfunctionEntity: DisfunctionEntity) : this(
        id = disfunctionEntity.id,
        description = disfunctionEntity.description,
        disfunctionStatusId = disfunctionEntity.disfunctionStatusId,
        customerId = disfunctionEntity.customerId
    )
}
