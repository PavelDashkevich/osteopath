package by.dashkevichpavel.osteopath.repositories.localdb

import android.content.Context
import android.util.Log
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.Session
import by.dashkevichpavel.osteopath.repositories.localdb.entity.CustomerEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OsteoDbRepository(applicationContext: Context) {
    private val osteoDb = OsteoDb.getInstance(applicationContext)

    suspend fun getAllCustomers(): List<CustomerEntity> = withContext(Dispatchers.IO) {
        return@withContext osteoDb.customerDao.getAll()
    }

    suspend fun getCustomerById(
        customerId: Long,
        loadDisfunctions: Boolean = false,
        loadSessions: Boolean = false
    ): Customer? = withContext(Dispatchers.IO) {
        val customerEntities = osteoDb.customerDao.getById(customerId)
        val customers = customerEntities.map {
            Customer(
                it,
                if (loadDisfunctions)
                    osteoDb.disfunctionDao.getByCustomerId(customerId)
                else
                    listOf(),
                if (loadSessions)
                    osteoDb.sessionDao.getByCustomerId(customerId)
                else
                    listOf()
            )
        }

        return@withContext customers.firstOrNull()
    }

    suspend fun insertCustomers(customers: List<Customer>) = withContext(Dispatchers.IO) {
        for (customer in customers) {
            val customerId = osteoDb.customerDao.insert(CustomerEntity(customer))

            customer.disfunctions.forEach { disfunction -> disfunction.customerId = customerId }
            customer.sessions.forEach { session -> session.customerId = customerId }

            osteoDb.disfunctionDao.insert(customer.disfunctions.map { DisfunctionEntity(it) })
            osteoDb.sessionDao.insert(customer.sessions.map { SessionEntity(it) })
        }
    }

    fun getAllCustomersAsFlow(): Flow<List<Customer>> =
        osteoDb.customerDao.getAllAsFlow().map { customerEntities ->
            customerEntities.map { customerEntity -> Customer(customerEntity, listOf(), listOf()) }
        }

    fun getAllDisfunctionsByCustomerId(customerId: Long): Flow<List<Disfunction>> =
        osteoDb.disfunctionDao.getByCustomerIdAsFlow(customerId).map { disfunctionEntities ->
            disfunctionEntities.map { disfunctionEntity -> Disfunction(disfunctionEntity) }
        }

    suspend fun getDisfunctionById(disfunctionId: Long): Disfunction? = withContext(Dispatchers.IO) {
        val disfunctions = osteoDb.disfunctionDao.getById(disfunctionId).map { disfunctionEntity ->
            Disfunction(disfunctionEntity)
        }

        return@withContext disfunctions.firstOrNull()
    }

    suspend fun insertDisfunction(disfunction: Disfunction): Long = withContext(Dispatchers.IO) {
        Log.d("OsteoApp", "OsteoDbRepository: insertDisfunction")
        return@withContext osteoDb.disfunctionDao.insert(DisfunctionEntity(disfunction))
    }

    fun getSessionsWithDisfunctionsByCustomerId(customerId: Long): Flow<List<Session>> =
        osteoDb.sessionDao.getSessionsWithDisfunctionsByCustomerId(customerId).map {
            sessionsWithDisfunction ->
                sessionsWithDisfunction.map { sessionWithDisfunctions ->
                    Session(sessionWithDisfunctions)
                }
        }

    suspend fun getSessionById(sessionId: Long): Session? = withContext(Dispatchers.IO) {
        val sessionsAndDisfunctions = osteoDb.sessionDao.getSessionsWithDisfunctionsById(sessionId)
        val sessions = sessionsAndDisfunctions.map { sessionAndDisfunction ->
            Session(sessionAndDisfunction)
        }
        return@withContext sessions.firstOrNull()
    }
}

object OsteoDbRepositorySingleton {
    private var repository: OsteoDbRepository? = null

    fun getInstance(applicationContext: Context): OsteoDbRepository {
        if (repository == null) {
            repository = OsteoDbRepository(applicationContext)
        }

        return repository as OsteoDbRepository
    }
}