package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.repositories.localdb.SessionAndDisfunctions
import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionEntity
import java.util.*

data class Session(
    var id: Long = 0,
    var customerId: Long = 0,
    var dateTime: Date = Date(0),
    var plan: String = "",
    var bodyCondition: String = "",
    var isDone: Boolean = false,
    var disfunctions: MutableList<Disfunction> = mutableListOf()
) {
    constructor(
        sessionEntity: SessionEntity,
        disfunctionEntities: List<DisfunctionEntity>
    ) : this (
        id = sessionEntity.id,
        customerId = sessionEntity.customerId,
        dateTime = sessionEntity.dateTime,
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

    fun isContentTheSame(other: Session): Boolean {
        var res = false

        if ((dateTime != other.dateTime)
            || (plan != other.plan)
            || (bodyCondition != other.bodyCondition)
            || (isDone != other.isDone)) {
                return false
        }

        val mapOfDisfunctions = disfunctions.associateBy { disfunction -> disfunction.id }
        val mapOfDisfunctionsOther = other.disfunctions.associateBy { disfunction -> disfunction.id }

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
