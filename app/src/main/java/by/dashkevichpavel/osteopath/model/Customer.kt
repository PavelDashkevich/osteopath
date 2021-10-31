package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable
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
    var isArchived: Boolean = false,
    var customerStatusId: Int = CustomerStatus.WORK.id,
    var disfunctions: MutableList<Disfunction> = mutableListOf(),
    var sessions: MutableList<Session> = mutableListOf(),
    var attachments: MutableList<Attachment> = mutableListOf()
) : DiffUtilComparable {
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
        isArchived = customerEntity.isArchived,
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
                customerStatusId != other.customerStatusId ||
                isArchived != other.isArchived
    }

    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is Customer) return false

        return id == item.id
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item !is Customer) return false

        return !isModified(item)
    }
}
