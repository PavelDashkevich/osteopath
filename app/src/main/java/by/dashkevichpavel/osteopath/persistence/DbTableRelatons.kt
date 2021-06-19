package by.dashkevichpavel.osteopath.persistence

import androidx.room.Embedded
import androidx.room.Relation
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity

data class CustomerAndDisfunctions(
    @Embedded
    val customer: CustomerEntity,

    @Relation(
        parentColumn = DbContract.Customer.COLUMN_NAME_ID,
        entityColumn = DbContract.Disfunction.COLUMN_NAME_CUSTOMER_ID
    )
    val disfunctions: List<DisfunctionEntity>
)