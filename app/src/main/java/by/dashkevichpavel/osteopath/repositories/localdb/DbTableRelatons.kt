package by.dashkevichpavel.osteopath.repositories.localdb

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import by.dashkevichpavel.osteopath.repositories.localdb.entity.CustomerEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionDisfunctionsEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionEntity

data class CustomerAndDisfunctions(
    @Embedded
    val customerEntity: CustomerEntity,

    @Relation(
        parentColumn = DbContract.Customers.COLUMN_NAME_ID,
        entityColumn = DbContract.Disfunctions.COLUMN_NAME_CUSTOMER_ID
    )
    val disfunctionEntities: List<DisfunctionEntity>
)

data class SessionAndDisfunctions(
    @Embedded
    val sessionEntity: SessionEntity,

    @Relation(
        parentColumn = DbContract.Sessions.COLUMN_NAME_ID,
        entityColumn = DbContract.Disfunctions.COLUMN_NAME_ID,
        associateBy = Junction(
            value = SessionDisfunctionsEntity::class,
            parentColumn = DbContract.SessionDisfunctions.COLUMN_NAME_SESSION_ID,
            entityColumn = DbContract.SessionDisfunctions.COLUMN_NAME_DISFUNCTION_ID
        )
    )
    val disfunctionEntities: List<DisfunctionEntity>
)