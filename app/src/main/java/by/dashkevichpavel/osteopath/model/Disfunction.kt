package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable
import by.dashkevichpavel.osteopath.repositories.data.entity.DisfunctionEntity

data class Disfunction(
    var id: Long = 0,
    var description: String = "",
    var disfunctionStatusId: Int = DisfunctionStatus.WORK.id,
    var customerId: Long = 0
) : DiffUtilComparable {
    constructor(disfunctionEntity: DisfunctionEntity) : this(
        id = disfunctionEntity.id,
        description = disfunctionEntity.description,
        disfunctionStatusId = disfunctionEntity.disfunctionStatusId,
        customerId = disfunctionEntity.customerId
    )

    fun isModified(other: Disfunction?): Boolean {
        if (other == null) return true

        return this.disfunctionStatusId != other.disfunctionStatusId ||
                this.description != other.description
    }

    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is Disfunction) return false

        return this.id == item.id
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is Disfunction) return false

        return item == this
    }
}
