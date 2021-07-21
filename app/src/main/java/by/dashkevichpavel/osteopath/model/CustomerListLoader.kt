package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class CustomerListLoader(
    private val customerListLoaderSubscriber: CustomerListLoaderSubscriber,
    private val repository: OsteoDbRepository,
    private val scope: CoroutineScope
) {
    private var jobFlow: Job? = null

    init {
        startCustomersTableChangeListening()
    }

    private fun startCustomersTableChangeListening() {
        if (jobFlow == null) {
            jobFlow = scope.launch {
                repository.getAllCustomersAsFlow().collect { listOfCustomers ->
                    customerListLoaderSubscriber.onCustomersLoaded(listOfCustomers)
                }
            }
        }
    }

    fun loadTestData() {
        val customers: MutableList<Customer> = mutableListOf(
            Customer(
                name = "Иванов Иван",
                birthDate = Date(),
                phone = "+375291112233",
                email = "ivanov.ivan@ff.ru",
                instagram = "ivaiva",
                facebook = "ivaivaiva",
                customerStatusId = CustomerStatus.WORK.id,
                disfunctions = mutableListOf(
                    Disfunction(
                        description = "Болит левое плечо. Хромает на правую ногу. Хрустят колени.",
                        disfunctionStatusId = DisfunctionStatus.WORK.id),
                    Disfunction(
                        description = "Тут очень сложное и большое описание дисфункции.",
                        disfunctionStatusId = DisfunctionStatus.WORK.id),
                    Disfunction(
                        description = "Искривление позвоночника, уход среднего отдела влево.",
                        disfunctionStatusId = DisfunctionStatus.WORK_DONE.id),
                    Disfunction(
                        description = "Некоторый набор слов, характеризующий дисфункцию.",
                        disfunctionStatusId = DisfunctionStatus.WORK_DONE.id),
                    Disfunction(
                        description = "Мучают тики.",
                        disfunctionStatusId = DisfunctionStatus.WORK_FAIL.id),
                    Disfunction(
                        description = "Выпадение волос и их ломкость.",
                        disfunctionStatusId = DisfunctionStatus.WRONG_DIAGNOSED.id)
                ),
                sessions = mutableListOf(
                    Session(isDone = true),
                    Session(isDone = false),
                    Session(isDone = true),
                    Session(isDone = true)
                )
            ),
            Customer(
                name = "Карымова Ира",
                birthDate = Date(),
                phone = "+3752922222222",
                email = "ira.ira@ff.ru",
                instagram = "ira_karymova",
                facebook = "iraira",
                customerStatusId = CustomerStatus.WORK.id,
                disfunctions = mutableListOf(
                    Disfunction(
                        description = "Несимметричность левой пятки относительно правой.",
                        disfunctionStatusId = DisfunctionStatus.WORK.id),
                    Disfunction(
                        description = "Некоторый набор слов, характеризующий дисфункцию.",
                        disfunctionStatusId = DisfunctionStatus.WORK_DONE.id)
                ),
                sessions = mutableListOf(
                    Session(isDone = true),Session(isDone = false),
                    Session(isDone = true),
                    Session(isDone = true)
                )
            ),
            Customer(
                name = "Ключинская Ира",
                birthDate = Date(),
                phone = "+375293333333",
                email = "ira.ira@ff.ru",
                instagram = "ira_kluch",
                facebook = "iraira",
                customerStatusId = CustomerStatus.WORK_DONE.id,
                disfunctions = mutableListOf(
                    Disfunction(
                        description = "Несимметричность левой пятки относительно правой.",
                        disfunctionStatusId = DisfunctionStatus.WORK.id),
                    Disfunction(
                        description = "Некоторый набор слов, характеризующий дисфункцию.",
                        disfunctionStatusId = DisfunctionStatus.WORK_DONE.id)
                ),
                sessions = mutableListOf(
                    Session(isDone = true),
                    Session(isDone = false),
                    Session(isDone = true),
                    Session(isDone = true)
                )
            ),
            Customer(
                name = "Мама",
                birthDate = Date(),
                phone = "+375294444444",
                customerStatusId = CustomerStatus.WORK_DONE.id
            ),
            Customer(
                name = "Папа",
                birthDate = Date(),
                phone = "+375295555555",
                customerStatusId = CustomerStatus.WORK.id
            )
        )

        scope.launch {
            repository.insertCustomers(customers)
        }
    }
}

interface CustomerListLoaderSubscriber {
    fun onCustomersLoaded(customers: List<Customer>)
}