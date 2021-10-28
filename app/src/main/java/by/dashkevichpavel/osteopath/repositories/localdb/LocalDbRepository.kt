package by.dashkevichpavel.osteopath.repositories.localdb

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.repositories.localdb.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalDbRepository(applicationContext: Context) {
    private var localDb = LocalDb.getInstance(applicationContext)

    fun refreshDbInstance(applicationContext: Context) {
        localDb = LocalDb.getInstance(applicationContext)
    }

    suspend fun getCustomerById(
        customerId: Long,
        loadDisfunctions: Boolean = false,
        loadSessions: Boolean = false,
        loadAttachments: Boolean = false
    ): Customer? = withContext(Dispatchers.IO) {
        val customerEntities = localDb.customerDao.getById(customerId)
        val customers = customerEntities.map { customerEntity ->
            Customer(
                customerEntity,
                if (loadDisfunctions)
                    localDb.disfunctionDao.getByCustomerId(customerId)
                else
                    listOf(),
                if (loadSessions)
                    localDb.sessionDao.getByCustomerId(customerId)
                else
                    listOf(),
                if (loadAttachments)
                    localDb.attachmentDao.getByCustomerId(customerId)
                else
                    listOf()
            )
        }

        return@withContext customers.firstOrNull()
    }

    suspend fun insertCustomers(customers: List<Customer>) = withContext(Dispatchers.IO) {
        for (customer in customers) {
            val customerId = localDb.customerDao.insert(CustomerEntity(customer))

            customer.disfunctions.forEach { disfunction -> disfunction.customerId = customerId }
            customer.sessions.forEach { session -> session.customerId = customerId }

            localDb.disfunctionDao.insert(customer.disfunctions.map { DisfunctionEntity(it) })
            localDb.sessionDao.insert(customer.sessions.map { SessionEntity(it) })
        }
    }

    suspend fun insertCustomer(customer: Customer): Long = withContext(Dispatchers.IO) {
        return@withContext localDb.customerDao.insert(CustomerEntity(customer))
    }

    fun getAllCustomersAsFlow(): Flow<List<Customer>> =
        localDb.customerDao.getAllAsFlow().map { customerEntities ->
            customerEntities.map { customerEntity ->
                Customer(customerEntity, listOf(), listOf(), listOf())
            }
        }

    fun getAllDisfunctionsByCustomerIdAsFlow(customerId: Long): Flow<List<Disfunction>> =
        localDb.disfunctionDao.getByCustomerIdAsFlow(customerId).map { disfunctionEntities ->
            disfunctionEntities.map { disfunctionEntity -> Disfunction(disfunctionEntity) }
        }

    suspend fun getDisfunctionById(disfunctionId: Long): Disfunction? = withContext(Dispatchers.IO) {
        val disfunctions = localDb.disfunctionDao.getById(disfunctionId).map { disfunctionEntity ->
            Disfunction(disfunctionEntity)
        }

        return@withContext disfunctions.firstOrNull()
    }

    suspend fun insertDisfunction(disfunction: Disfunction): Long = withContext(Dispatchers.IO) {
        Log.d("OsteoApp", "OsteoDbRepository: insertDisfunction")
        return@withContext localDb.disfunctionDao.insert(DisfunctionEntity(disfunction))
    }

    fun getSessionsWithDisfunctionsByCustomerIdAsFlow(customerId: Long): Flow<List<Session>> =
        localDb.sessionDao.getSessionsWithDisfunctionsByCustomerId(customerId).map {
            sessionsWithDisfunction ->
                sessionsWithDisfunction.map { sessionWithDisfunctions ->
                    Session(sessionWithDisfunctions)
                }
        }

    suspend fun getSessionById(sessionId: Long): Session? =
        withContext(Dispatchers.IO) {
            val sessionsAndDisfunctions = localDb.sessionDao.getSessionsWithDisfunctionsById(sessionId)
            val sessions = sessionsAndDisfunctions.map { sessionAndDisfunction ->
                Session(sessionAndDisfunction)
            }
            return@withContext sessions.firstOrNull()
    }

    suspend fun getDisfunctionsByCustomerId(customerId: Long): List<Disfunction> =
        withContext(Dispatchers.IO) {
            return@withContext localDb.disfunctionDao.getByCustomerId(customerId)
                .map { disfunctionEntity -> Disfunction(disfunctionEntity) }
    }

    suspend fun getDisfunctionsByIds(disfunctionsIds: List<Long>): List<Disfunction> =
        withContext(Dispatchers.IO) {
            return@withContext localDb.disfunctionDao.getByIds(disfunctionsIds)
                .map { disfunctionEntity -> Disfunction(disfunctionEntity) }
    }

    suspend fun insertSession(session: Session): Long = withContext(Dispatchers.IO) {
        val id = localDb.sessionDao.insert(SessionEntity(session))
        localDb.sessionDisfunctionDao.deleteBySessionId(session.id)
        localDb.sessionDisfunctionDao.insert(
            session.disfunctions.map { disfunction ->
                SessionDisfunctionsEntity(session.id, disfunction.id)
            }
        )
        return@withContext id
    }

    fun getAttachmentsByCustomerIdAsFlow(customerId: Long): Flow<List<Attachment>> =
        localDb.attachmentDao.getAllByCustomerIdAsFlow(customerId).map { attachmentsEntities ->
            attachmentsEntities.map { attachmentEntity-> Attachment(attachmentEntity) }
        }

    suspend fun insertAttachment(attachment: Attachment) = withContext(Dispatchers.IO) {
        localDb.attachmentDao.insert(AttachmentEntity(attachment))
    }

    suspend fun updateAttachment(attachment: Attachment) = withContext(Dispatchers.IO) {
        localDb.attachmentDao.update(AttachmentEntity(attachment))
    }

    suspend fun checkPoint() = withContext(Dispatchers.IO) {
        localDb.utilDao.checkPoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    }

    suspend fun updateCustomerIsArchived(customerId: Long, isArchived: Boolean) =
        withContext(Dispatchers.IO) {
            localDb.customerDao.updateIsArchived(customerId, isArchived)
        }

    suspend fun deleteCustomerById(customerId: Long) = withContext(Dispatchers.IO) {
        val customer = getCustomerById(
            customerId,
            loadDisfunctions = true,
            loadSessions = true,
            loadAttachments = true
        )

        customer?.let {
            customer.sessions.forEach { session ->
                localDb.sessionDisfunctionDao.deleteBySessionId(session.id)
                localDb.sessionDao.deleteById(session.id)
            }
            localDb.disfunctionDao.deleteByIds(
                customer.disfunctions.map { disfunction -> disfunction.id }
            )
            localDb.attachmentDao.deleteByIds(
                customer.attachments.map { attachment -> attachment.id }
            )
            localDb.customerDao.deleteById(customerId)
        }
    }

    suspend fun deleteDisfunctionById(disfunctionId: Long) = withContext(Dispatchers.IO) {
        localDb.sessionDisfunctionDao.deleteByDisfunctionId(disfunctionId)
        localDb.disfunctionDao.deleteById(disfunctionId)
    }

    suspend fun updateDisfunctionStatusById(disfunctionId: Long, disfunctionStatusId: Int) =
        withContext(Dispatchers.IO) {
            localDb.disfunctionDao.updateStatusById(disfunctionId, disfunctionStatusId)
        }

    suspend fun deleteSessionById(sessionId: Long) = withContext(Dispatchers.IO) {
        localDb.sessionDao.deleteById(sessionId)
    }

    suspend fun updateSessionIsDone(sessionId: Long, isDone: Boolean) =
        withContext(Dispatchers.IO) {
            localDb.sessionDao.updateIsDoneById(sessionId, isDone)
    }

    fun close() = LocalDb.close()
}

object OsteoDbRepositorySingleton {
    private var repository: LocalDbRepository? = null

    fun getInstance(applicationContext: Context): LocalDbRepository {
        if (repository == null) {
            repository = LocalDbRepository(applicationContext)
        }

        repository?.refreshDbInstance(applicationContext)

        return repository as LocalDbRepository
    }
}