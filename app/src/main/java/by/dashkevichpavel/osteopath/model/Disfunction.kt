package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity

data class Disfunction(
    val id: Long = 0,
    var description: String = "",
    var disfunctionStatusId: Int = DisfunctionStatus.WORK.id,
    var customerId: Long = 0
) {
    constructor(disfunctionEntity: DisfunctionEntity) : this(
        id = disfunctionEntity.id,
        description = disfunctionEntity.description,
        disfunctionStatusId = disfunctionEntity.disfunctionStatusId,
        customerId = disfunctionEntity.customerId
    )

    fun isTheSameById(other: Disfunction): Boolean = this.id == other.id

    fun isModified(other: Disfunction?): Boolean {
        if (other == null) return true

        return this.disfunctionStatusId != other.disfunctionStatusId ||
                this.description != other.description
    }
}
