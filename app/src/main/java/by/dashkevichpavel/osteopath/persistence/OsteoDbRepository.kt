package by.dashkevichpavel.osteopath.persistence

import android.content.Context
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OsteoDbRepository(applicationContext: Context) {
    private val osteoDb = OsteoDb.getInstance(applicationContext)

    suspend fun getAllCustomers(): List<CustomerEntity> = withContext(Dispatchers.IO) {
        return@withContext osteoDb.customerDao.getAll()
    }

    suspend fun insertCustomers(customers: List<Customer>) = withContext(Dispatchers.IO) {
        osteoDb.customerDao.insert(customers.map { CustomerEntity(it) })
    }

    suspend fun getCustomerById(customerId: Int): Customer? = withContext(Dispatchers.IO) {
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

    fun getAllCustomersAsFlow(): Flow<List<CustomerEntity>> = osteoDb.customerDao.getAllAsFlow()
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