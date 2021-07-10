package by.dashkevichpavel.osteopath.persistence.dao

import androidx.room.*
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.SessionWithDisfunctions
import by.dashkevichpavel.osteopath.persistence.entity.SessionWithDisfunctionsEntity
import by.dashkevichpavel.osteopath.persistence.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    suspend fun getByCustomerId(customerId: Long): List<SessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionEntities: List<SessionEntity>)

    @Transaction
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    fun getSessionsWithDisfunctionsByCustomerId(customerId: Long): Flow<List<SessionWithDisfunctions>>
}