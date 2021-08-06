package by.dashkevichpavel.osteopath.repositories.localdb.dao

import androidx.room.*
import by.dashkevichpavel.osteopath.repositories.localdb.DbContract
import by.dashkevichpavel.osteopath.repositories.localdb.SessionAndDisfunctions
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    suspend fun getByCustomerId(customerId: Long): List<SessionEntity>

    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME}
        WHERE ${DbContract.Sessions.COLUMN_NAME_ID} == :sessionId""")
    suspend fun getById(sessionId: Long): List<SessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionEntities: List<SessionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionEntity: SessionEntity)

    @Transaction
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    fun getSessionsWithDisfunctionsByCustomerId(customerId: Long): Flow<List<SessionAndDisfunctions>>

    @Transaction
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_ID} == :sessionId""")
    suspend fun getSessionsWithDisfunctionsById(sessionId: Long): List<SessionAndDisfunctions>
}