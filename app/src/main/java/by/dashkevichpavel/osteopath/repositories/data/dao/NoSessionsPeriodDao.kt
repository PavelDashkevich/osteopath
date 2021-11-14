package by.dashkevichpavel.osteopath.repositories.data.dao

import androidx.room.*
import by.dashkevichpavel.osteopath.repositories.data.DbContract
import by.dashkevichpavel.osteopath.repositories.data.entity.NoSessionsPeriodEntity
import by.dashkevichpavel.osteopath.repositories.data.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoSessionsPeriodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noSessionsPeriodEntity: NoSessionsPeriodEntity): Long

    @Query("""SELECT * FROM ${DbContract.NoSessionPeriods.TABLE_NAME}
        WHERE ${DbContract.NoSessionPeriods.COLUMN_NAME_ID} == :noSessionsPeriodId""")
    suspend fun getById(noSessionsPeriodId: Long): List<NoSessionsPeriodEntity>

    @Query("""DELETE FROM ${DbContract.NoSessionPeriods.TABLE_NAME}
        WHERE ${DbContract.NoSessionPeriods.COLUMN_NAME_ID} == :noSessionsPeriodId""")
    suspend fun deleteById(noSessionsPeriodId: Long)

    @Transaction
    @Query("""SELECT * FROM ${DbContract.NoSessionPeriods.TABLE_NAME} 
        WHERE ${DbContract.NoSessionPeriods.COLUMN_NAME_DATE_TIME_START} >= :fromDateTime
        AND ${DbContract.NoSessionPeriods.COLUMN_NAME_DATE_TIME_START} <= :toDateTime
        ORDER BY ${DbContract.NoSessionPeriods.COLUMN_NAME_DATE_TIME_START} ASC""")
    fun getNoSessionPeriodsByPeriodAsFlow(fromDateTime: Long, toDateTime: Long): Flow<List<NoSessionsPeriodEntity>>
}