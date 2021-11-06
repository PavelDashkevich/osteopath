package by.dashkevichpavel.osteopath.repositories.data.dao

import androidx.room.*
import by.dashkevichpavel.osteopath.repositories.data.DbContract
import by.dashkevichpavel.osteopath.repositories.data.SessionAndDisfunctions
import by.dashkevichpavel.osteopath.repositories.data.entity.SessionEntity
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
    suspend fun insert(sessionEntity: SessionEntity): Long

    @Query("""DELETE FROM ${DbContract.Sessions.TABLE_NAME}
        WHERE ${DbContract.Sessions.COLUMN_NAME_ID} == :sessionId""")
    suspend fun deleteById(sessionId: Long)

    @Transaction
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    fun getSessionsWithDisfunctionsByCustomerId(customerId: Long): Flow<List<SessionAndDisfunctions>>

    @Transaction
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_DATE_TIME} >= :fromDateTime
        AND ${DbContract.Sessions.COLUMN_NAME_IS_DONE} == 0
        ORDER BY ${DbContract.Sessions.COLUMN_NAME_DATE_TIME} ASC""")
    fun getUpcomingSessionsWithDisfunctionsFromDateTime(fromDateTime: Long):
            Flow<List<SessionAndDisfunctions>>

    @Transaction
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_DATE_TIME_END} < :fromDateTime
        OR ${DbContract.Sessions.COLUMN_NAME_IS_DONE} == 1
        ORDER BY ${DbContract.Sessions.COLUMN_NAME_DATE_TIME} DESC""")
    fun getRecentSessionsWithDisfunctionsFromDateTime(fromDateTime: Long):
            Flow<List<SessionAndDisfunctions>>

    @Transaction
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_ID} == :sessionId""")
    suspend fun getSessionsWithDisfunctionsById(sessionId: Long): List<SessionAndDisfunctions>

    @Query("""UPDATE ${DbContract.Sessions.TABLE_NAME} 
        SET ${DbContract.Sessions.COLUMN_NAME_IS_DONE} = :isDone 
        WHERE ${DbContract.Sessions.COLUMN_NAME_ID} == :sessionId""")
    suspend fun updateIsDoneById(sessionId: Long, isDone: Boolean)
}