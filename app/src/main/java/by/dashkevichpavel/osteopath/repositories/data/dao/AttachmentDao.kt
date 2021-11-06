package by.dashkevichpavel.osteopath.repositories.data.dao

import androidx.room.*
import by.dashkevichpavel.osteopath.repositories.data.DbContract
import by.dashkevichpavel.osteopath.repositories.data.entity.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Query("""SELECT * FROM ${DbContract.Attachments.TABLE_NAME} 
        WHERE ${DbContract.Attachments.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    fun getAllByCustomerIdAsFlow(customerId: Long): Flow<List<AttachmentEntity>>

    @Query("""SELECT * FROM ${DbContract.Attachments.TABLE_NAME} 
        WHERE ${DbContract.Attachments.COLUMN_NAME_CUSTOMER_ID} == :customerId""")
    suspend fun getByCustomerId(customerId: Long): List<AttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachmentEntity: AttachmentEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(attachmentEntity: AttachmentEntity)

    @Query("""DELETE FROM ${DbContract.Attachments.TABLE_NAME}
        WHERE ${DbContract.Attachments.COLUMN_NAME_ID} == :attachmentId""")
    suspend fun deleteById(attachmentId: Long)

    @Query("""DELETE FROM ${DbContract.Attachments.TABLE_NAME}
        WHERE ${DbContract.Attachments.COLUMN_NAME_ID} IN (:attachmentIds)""")
    suspend fun deleteByIds(attachmentIds: List<Long>)
}