package by.dashkevichpavel.osteopath.repositories.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.dashkevichpavel.osteopath.repositories.localdb.DbContract
import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisfunctionDao {
    @Query("""SELECT * FROM ${DbContract.Disfunctions.TABLE_NAME} 
        WHERE ${DbContract.Disfunctions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    suspend fun getByCustomerId(customerId: Long): List<DisfunctionEntity>

    @Query("""SELECT * FROM ${DbContract.Disfunctions.TABLE_NAME} 
        WHERE ${DbContract.Disfunctions.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    fun getByCustomerIdAsFlow(customerId: Long): Flow<List<DisfunctionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(disfunctionEntity: DisfunctionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(disfunctionEntities: List<DisfunctionEntity>)

    @Query("""SELECT * FROM ${DbContract.Disfunctions.TABLE_NAME} 
        WHERE ${DbContract.Disfunctions.COLUMN_NAME_ID} == :disfunctionId""")
    suspend fun getById(disfunctionId: Long): List<DisfunctionEntity>

    @Query("""SELECT * FROM ${DbContract.Disfunctions.TABLE_NAME} 
        WHERE ${DbContract.Disfunctions.COLUMN_NAME_ID} IN (:disfunctionsIds)""")
    suspend fun getByIds(disfunctionsIds: List<Long>): List<DisfunctionEntity>

    @Query("""DELETE FROM ${DbContract.Disfunctions.TABLE_NAME}
        WHERE ${DbContract.Disfunctions.COLUMN_NAME_ID} == :disfunctionId""")
    suspend fun deleteById(disfunctionId: Long)

    @Query("""DELETE FROM ${DbContract.Disfunctions.TABLE_NAME}
        WHERE ${DbContract.Disfunctions.COLUMN_NAME_ID} IN (:disfunctionIds)""")
    suspend fun deleteByIds(disfunctionIds: List<Long>)

    @Query("""UPDATE ${DbContract.Disfunctions.TABLE_NAME} 
        SET ${DbContract.Disfunctions.COLUMN_NAME_DISFUNCTION_STATUS_ID} = :statusId 
        WHERE ${DbContract.Disfunctions.COLUMN_NAME_ID} == :disfunctionId""")
    suspend fun updateStatusById(disfunctionId: Long, statusId: Int)
}