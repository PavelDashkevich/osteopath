package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.repositories.localdb.entity.AttachmentEntity
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
    var sessions: MutableList<Session> = mutableListOf(),
    var attachments: MutableList<Attachment> = mutableListOf()
) {
    constructor(
        customerEntity: CustomerEntity,
        disfunctionEntities: List<DisfunctionEntity>,
        sessionEntities: List<SessionEntity>,
        attachmentEntities: List<AttachmentEntity>
    ) : this (
        id = customerEntity.id,
        name = customerEntity.name,
        birthDate = customerEntity.birthDate,
        phone = customerEntity.phone,
        email = customerEntity.email,
        instagram = customerEntity.instagram,
        facebook = customerEntity.facebook,
        customerStatusId = customerEntity.customerStatusId,
        disfunctions = disfunctionEntities.map { Disfunction(it) }.toMutableList(),
        sessions = sessionEntities.map { Session(it, emptyList()) }.toMutableList(),
        attachments = attachmentEntities.map { Attachment(it) }.toMutableList()
                )

    fun isModified(other: Customer?): Boolean {
        if (other == null) return true

        return name != other.name ||
                birthDate != other.birthDate ||
                phone != other.phone ||
                email != other.email ||
                instagram != other.instagram ||
                facebook != other.facebook ||
                customerStatusId != other.customerStatusId
    }
}
