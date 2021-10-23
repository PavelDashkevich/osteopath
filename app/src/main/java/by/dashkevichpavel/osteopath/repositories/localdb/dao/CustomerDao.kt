package by.dashkevichpavel.osteopath.repositories.localdb.dao

import androidx.room.*
import by.dashkevichpavel.osteopath.repositories.localdb.DbContract
import by.dashkevichpavel.osteopath.repositories.localdb.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("""SELECT * FROM ${DbContract.Customers.TABLE_NAME} 
        ORDER BY ${DbContract.Customers.COLUMN_NAME_NAME} COLLATE NOCASE""")
    suspend fun getAll(): List<CustomerEntity>

    @Query("""SELECT * FROM ${DbContract.Customers.TABLE_NAME} 
        ORDER BY ${DbContract.Customers.COLUMN_NAME_NAME} COLLATE NOCASE""")
    fun getAllAsFlow(): Flow<List<CustomerEntity>>

    @Query("""SELECT * FROM ${DbContract.Customers.TABLE_NAME} 
        WHERE ${DbContract.Customers.COLUMN_NAME_ID} == :customerId""")
    suspend fun getById(customerId: Long): List<CustomerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerEntities: List<CustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerEntity: CustomerEntity): Long

    @Query("""UPDATE ${DbContract.Customers.TABLE_NAME} 
        SET ${DbContract.Customers.COLUMN_NAME_IS_ARCHIVED} = :isArchived 
        WHERE ${DbContract.Customers.COLUMN_NAME_ID} == :customerId""")
    suspend fun updateIsArchived(customerId: Long, isArchived: Boolean)

    @Query("""DELETE FROM ${DbContract.Customers.TABLE_NAME}
        WHERE ${DbContract.Customers.COLUMN_NAME_ID} == :customerId""")
    suspend fun deleteById(customerId: Long)
}