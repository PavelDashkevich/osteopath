package by.dashkevichpavel.osteopath.persistence

import android.content.Context
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.persistence.entity.SessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OsteoDbRepository(applicationContext: Context) {
    private val osteoDb = OsteoDb.getInstance(applicationContext)

    suspend fun getAllCustomers(): List<CustomerEntity> = withContext(Dispatchers.IO) {
        return@withContext osteoDb.customerDao.getAll()
    }

    suspend fun getCustomerById(customerId: Long): Customer? = withContext(Dispatchers.IO) {
        val customerEntities = osteoDb.customerDao.getById(customerId)
        val customers = customerEntities.map {
            Customer(
                it,
                osteoDb.disfunctionDao.getByCustomerId(customerId),
                osteoDb.sessionDao.getByCustomerId(customerId)
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