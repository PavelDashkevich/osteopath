package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.repositories.localdb.entity.CustomerEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionEntity
import java.util.*

data class Customer(
    var id: Long = 0,
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
        sessions = sessionEntities.map { sessionEntity ->
            Session(sessionEntity, emptyList())
        } as MutableList<Session>
                )
}
