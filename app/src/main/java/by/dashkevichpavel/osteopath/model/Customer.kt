package by.dashkevichpavel.osteopath.model

import androidx.room.ColumnInfo
import by.dashkevichpavel.osteopath.persistence.CustomerAndDisfunctions
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.persistence.entity.SessionEntity
import java.util.*

data class Customer(
    var id: Int = 0,
    var name: String = "",
    var birthDate: Date = Date(0),
    var phone: String = "",
    var email: String = "",
    var instagram: String = "",
    var facebook: String = "",
    var customerStatusId: Int = CustomerStatus.WORK.id,
    var disfunctions: MutableList<Disfunction> = mutableListOf(),
    var sessions: MutableList<Session> = mutableListOf()
) {
    constructor(
        customerEntity: CustomerEntity,
        disfunctionEntities: List<DisfunctionEntity>,
        sessionEntities: List<SessionEntity>
    ) : this (
        id = customerEntity.id,
        name = customerEntity.name,
        birthDate = customerEntity.birthDate,
        phone = customerEntity.phone,
        email = customerEntity.email,
        instagram = customerEntity.instagram,
        facebook = customerEntity.facebook,
        customerStatusId = customerEntity.customerStatusId,
        disfunctions = disfunctionEntities.map { Disfunction(it) } as MutableList<Disfunction>,
        sessions = sessionEntities.map { Session(it) } as MutableList<Session>
                )
}
