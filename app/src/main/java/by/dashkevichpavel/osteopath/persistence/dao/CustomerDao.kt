package by.dashkevichpavel.osteopath.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("""SELECT * FROM ${DbContract.Customer.TABLE_NAME} 
        ORDER BY ${DbContract.Customer.COLUMN_NAME_NAME}""")
    suspend fun getAll(): List<CustomerEntity>

    @Query("""SELECT * FROM ${DbContract.Customer.TABLE_NAME} 
        ORDER BY ${DbContract.Customer.COLUMN_NAME_NAME}""")
    fun getAllAsFlow(): Flow<List<CustomerEntity>>
}