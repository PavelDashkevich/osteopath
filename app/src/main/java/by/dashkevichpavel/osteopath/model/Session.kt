package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable
import by.dashkevichpavel.osteopath.repositories.data.SessionAndDisfunctions
import by.dashkevichpavel.osteopath.repositories.data.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.repositories.data.entity.SessionEntity
import java.util.*

data class Session(
    var id: Long = 0,
    var customerId: Long = 0,
    var dateTime: Date = Date(System.currentTimeMillis()),
    var dateTimeEnd: Date = Date(System.currentTimeMillis()),
    var plan: String = "",
    var bodyCondition: String = "",
    var isDone: Boolean = false,
    var disfunctions: MutableList<Disfunction> = mutableListOf()
) : DiffUtilComparable {
    constructor(
        sessionEntity: SessionEntity,
        disfunctionEntities: List<DisfunctionEntity>
    ) : this (
        id = sessionEntity.id,
        customerId = sessionEntity.customerId,
        dateTime = sessionEntity.dateTime,
        dateTimeEnd = sessionEntity.dateTimeEnd,
        plan = sessionEntity.plan,
        bodyCondition = sessionEntity.bodyCondition,
        isDone = sessionEntity.isDone,
        disfunctions = disfunctionEntities.map {
                disfunctionEntity -> Disfunction(disfunctionEntity)
        } as MutableList<Disfunction>
            )

    constructor(sessionAndDisfunctions: SessionAndDisfunctions) : this (
        sessionEntity = sessionAndDisfunctions.sessionEntity,
        disfunctionEntities = sessionAndDisfunctions.disfunctionEntities
            )

    fun isModified(other: Session?): Boolean = !contentsTheSameAs(other)

    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is Session) return false

        return this.id == item.id
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false

        if (item !is Session) return false

        if ((dateTime != item.dateTime)
            || (dateTimeEnd != item.dateTimeEnd)
            || (plan != item.plan)
            || (bodyCondition != item.bodyCondition)
            || (isDone != item.isDone)) {
            return false
        }

        val mapOfDisfunctions = disfunctions.associateBy { disfunction -> disfunction.id }
        val mapOfDisfunctionsOther = item.disfunctions.associateBy { disfunction -> disfunction.id }

        if (mapOfDisfunctions.keys != mapOfDisfunctionsOther.keys) {
            return false
        }

        for (key in mapOfDisfunctions.keys) {
            if (mapOfDisfunctions[key]?.description != mapOfDisfunctionsOther[key]?.description) {
                return false
            }
        }

        return true
    }
}