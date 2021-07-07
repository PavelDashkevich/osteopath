package by.dashkevichpavel.osteopath.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity
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
}