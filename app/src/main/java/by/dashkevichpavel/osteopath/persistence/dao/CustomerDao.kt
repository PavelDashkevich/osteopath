package by.dashkevichpavel.osteopath.persistence.dao

import androidx.room.*
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("""SELECT * FROM ${DbContract.Customers.TABLE_NAME} 
        ORDER BY ${DbContract.Customers.COLUMN_NAME_NAME}""")
    suspend fun getAll(): List<CustomerEntity>

    @Query("""SELECT * FROM ${DbContract.Customers.TABLE_NAME} 
        ORDER BY ${DbContract.Customers.COLUMN_NAME_NAME}""")
    fun getAllAsFlow(): Flow<List<CustomerEntity>>

    @Query("""SELECT * FROM ${DbContract.Customers.TABLE_NAME} 
        WHERE ${DbContract.Customers.COLUMN_NAME_ID} == :customerId""")
    suspend fun getById(customerId: Long): List<CustomerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerEntities: List<CustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerEntity: CustomerEntity): Long
}