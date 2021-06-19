package by.dashkevichpavel.osteopath.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import by.dashkevichpavel.osteopath.persistence.DbContract
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity

@Dao
interface CustomerDao {
    @Query("SELECT * FROM ${DbContract.Customer.TABLE_NAME}")
    suspend fun getAll(): List<CustomerEntity>
}