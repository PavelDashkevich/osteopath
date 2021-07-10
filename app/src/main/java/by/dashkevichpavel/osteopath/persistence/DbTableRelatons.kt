package by.dashkevichpavel.osteopath.persistence

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.persistence.entity.SessionWithDisfunctionsEntity
import by.dashkevichpavel.osteopath.persistence.entity.SessionEntity

data class CustomerAndDisfunctions(
    @Embedded
    val customerEntity: CustomerEntity,

    @Relation(
        parentColumn = DbContract.Customers.COLUMN_NAME_ID,
        entityColumn = DbContract.Disfunctions.COLUMN_NAME_CUSTOMER_ID
    )
    val disfunctionEntities: List<DisfunctionEntity>
)

data class SessionWithDisfunctions(
    @Embedded
    val sessionEntity: SessionEntity,

    @Relation(
        parentColumn = DbContract.Sessions.COLUMN_NAME_ID,
        entityColumn = DbContract.Disfunctions.COLUMN_NAME_ID,
        associateBy = Junction(SessionWithDisfunctionsEntity::class)
    )
    val disfunctionEntities: List<DisfunctionEntity>
)