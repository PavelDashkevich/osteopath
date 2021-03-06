package by.dashkevichpavel.osteopath.repositories.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.dashkevichpavel.osteopath.repositories.data.DbContract
import by.dashkevichpavel.osteopath.repositories.data.entity.SessionDisfunctionsEntity

@Dao
interface SessionDisfunctionDao {
    @Query("""DELETE FROM ${DbContract.SessionDisfunctions.TABLE_NAME}
        WHERE ${DbContract.SessionDisfunctions.COLUMN_NAME_SESSION_ID} == :sessionId""")
    suspend fun deleteBySessionId(sessionId: Long)

    @Query("""DELETE FROM ${DbContract.SessionDisfunctions.TABLE_NAME}
        WHERE ${DbContract.SessionDisfunctions.COLUMN_NAME_DISFUNCTION_ID} == :disfunctionId""")
    suspend fun deleteByDisfunctionId(disfunctionId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionDisfunctionEntities: List<SessionDisfunctionsEntity>)
}