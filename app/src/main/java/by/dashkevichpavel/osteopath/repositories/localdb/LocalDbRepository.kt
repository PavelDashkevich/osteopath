package by.dashkevichpavel.osteopath.repositories.localdb

import android.content.Context
import android.util.Log
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.repositories.localdb.entity.CustomerEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionDisfunctionsEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalDbRepository(applicationContext: Context) {
    private val localDb = LocalDb.getInstance(applicationContext)

    suspend fun getAllCustomers(): List<CustomerEntity> = withContext(Dispatchers.IO) {
        return@withContext localDb.customerDao.getAll()
    }

    suspend fun getCustomerById(
        customerId: Long,
        loadDisfunctions: Boolean = false,
        loadSessions: Boolean = false
    ): Customer? = withContext(Dispatchers.IO) {
        val customerEntities = localDb.customerDao.getById(customerId)
        val customers = customerEntities.map {
            Customer(
                it,
                if (loadDisfunctions)
                    localDb.disfunctionDao.getByCustomerId(customerId)
                else
                    listOf(),
                if (loadSessions)
                    localDb.sessionDao.getByCustomerId(customerId)
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
            customerEntities.map { customerEntity -> Customer(customerEntity, listOf(), listOf()) }
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

    fun getSessionsWithDisfunctionsByCustomerId(customerId: Long): Flow<List<Session>> =
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

    suspend fun insertSession(session: Session) = withContext(Dispatchers.IO) {
        localDb.sessionDao.insert(SessionEntity(session))
        localDb.sessionDisfunctionDao.deleteBySessionId(session.id)
        localDb.sessionDisfunctionDao.insert(
            session.disfunctions.map { disfunction ->
                SessionDisfunctionsEntity(session.id, disfunction.id)
            }
        )
    }
}

object OsteoDbRepositorySingleton {
    private var repository: LocalDbRepository? = null

    fun getInstance(applicationContext: Context): LocalDbRepository {
        if (repository == null) {
            repository = LocalDbRepository(applicationContext)
        }

        return repository as LocalDbRepository
    }
}