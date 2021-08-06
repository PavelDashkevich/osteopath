package by.dashkevichpavel.osteopath.repositories.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.dashkevichpavel.osteopath.repositories.localdb.DbContract
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionDisfunctionsEntity

@Dao
interface SessionDisfunctionDao {
    @Query("""DELETE FROM ${DbContract.SessionDisfunctions.TABLE_NAME}
        WHERE ${DbContract.SessionDisfunctions.COLUMN_NAME_SESSION_ID} == :sessionId""")
    suspend fun deleteBySessionId(sessionId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionDisfunctionEntities: List<SessionDisfunctionsEntity>)
}