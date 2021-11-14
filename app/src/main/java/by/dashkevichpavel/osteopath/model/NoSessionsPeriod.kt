package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable
import by.dashkevichpavel.osteopath.repositories.data.entity.NoSessionsPeriodEntity
import java.util.*

data class NoSessionsPeriod(
    var id: Long = 0,
    var dateTimeStart: Date = Date(),
    var dateTimeEnd: Date = Date()
) : DiffUtilComparable {
    constructor(noSessionsPeriodEntity: NoSessionsPeriodEntity) : this(
        id = noSessionsPeriodEntity.id,
        dateTimeStart = noSessionsPeriodEntity.dateTimeStart,
        dateTimeEnd = noSessionsPeriodEntity.dateTimeEnd
    )

    fun isModified(other: NoSessionsPeriod?): Boolean = !contentsTheSameAs(other)

    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item !is NoSessionsPeriod) return false

        return id == item.id
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item !is NoSessionsPeriod) return false

        return dateTimeStart.time == item.dateTimeStart.time &&
                dateTimeEnd.time == item.dateTimeEnd.time
    }
}
