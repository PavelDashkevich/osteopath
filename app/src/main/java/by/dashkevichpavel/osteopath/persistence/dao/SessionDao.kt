package by.dashkevichpavel.osteopath.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.persistence.entity.SessionEntity

@Dao
interface SessionDao {
    @Query("""SELECT * FROM ${DbContract.Sessions.TABLE_NAME} 
        WHERE ${DbContract.Sessions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    suspend fun getByCustomerId(customerId: Long): List<SessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionEntities: List<SessionEntity>)
}