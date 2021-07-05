package by.dashkevichpavel.osteopath.model

import androidx.room.ColumnInfo
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.SessionEntity
import java.util.*

data class Session(
    var id: Long = 0,
    var customerId: Long = 0,
    var dateTime: Date = Date(0),
    var isDone: Boolean = false
) {
    constructor(sessionEntity: SessionEntity) : this (
        id = sessionEntity.id,
        customerId = sessionEntity.customerId,
        dateTime = sessionEntity.dateTime,
        isDone = sessionEntity.isDone
            )
}
