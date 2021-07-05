package by.dashkevichpavel.osteopath.model

import androidx.lifecycle.viewModelScope
import by.dashkevichpavel.osteopath.persistence.OsteoDbRepository
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class CustomerListProcessor(
    private val handler: CustomerListProcessorSubscriber,
    private val repository: OsteoDbRepository,
    private val scope: CoroutineScope
) {
    private var filterValues = FilterValues()
    private var searchQuery: String = ""
    private var allCustomers: List<Customer> = emptyList()
    private var jobFlow: Job? = null

    init {
        startCustomersTableChangeListening()
    }

    fun setFilter(newFilterValues: FilterValues) {
        if (newFilterValues != filterValues) {
            filterValues = newFilterValues
            if (jobFlow != null) {
                updateFilteredCustomersList()
            }
        }
    }

    fun setQueryString(newSearchQuery: String) {
        if (!newSearchQuery.equals(searchQuery, true)) {
            searchQuery = newSearchQuery
            updateFilteredCustomersList()
        }
    }

    private fun startCustomersTableChangeListening() {
        if (jobFlow == null) {
            jobFlow = scope.launch {
                repository.getAllCustomersAsFlow().collect { listOfCustomers ->
                    allCustomers = listOfCustomers
                    updateFilteredCustomersList()
                }
            }
        }
    }

    private fun updateFilteredCustomersList() {
        var newFilteredList: List<Customer> = allCustomers
        var isSearchOrFilterResult = false

        // filter by category ang by age
        if (!filterValues.isFilterOff()) {
            val listOfSelectedCustomerStatusIds: MutableList<Int> = mutableListOf()

            if (filterValues.byCategoryWork) {
                listOfSelectedCustomerStatusIds.add(CustomerStatus.WORK.id)
            }

            if (filterValues.byCategoryWorkDone) {
                listOfSelectedCustomerStatusIds.add(CustomerStatus.WORK_DONE.id)
            }

            if (filterValues.byCategoryNoHelp) {
                listOfSelectedCustomerStatusIds.add(CustomerStatus.NO_HELP.id)
            }

            if (listOfSelectedCustomerStatusIds.isNotEmpty()) {
                newFilteredList = newFilteredList.filter { customer ->
                    listOfSelectedCustomerStatusIds.contains(customer.customerStatusId)
                }
            }

            if (!(filterValues.byAgeChildren && filterValues.byAgeAdults)) {
                val now = Date()
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = now.time
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                calendar.add(Calendar.YEAR, -18)

                val age18 = Date(calendar.timeInMillis)

                newFilteredList = if (filterValues.byAgeChildren) {
                    newFilteredList.filter { customer ->
                        customer.birthDate.time > age18.time
                    }
                } else {
                    newFilteredList.filter { customer ->
                        customer.birthDate.time <= age18.time
                    }
                }
            }

            isSearchOrFilterResult = true
        }

        // filter by search query
        if (searchQuery.isNotEmpty()) {
            newFilteredList = newFilteredList.filter { customer ->
                customer.name.contains(searchQuery, true)
            }

            isSearchOrFilterResult = true
        }

        handler.onCustomersProcessed(newFilteredList, isSearchOrFilterResult)
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
                    Session(isDone = true),Session(isDone = false),
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

interface CustomerListProcessorSubscriber {
    fun onCustomersProcessed(customers: List<Customer>, isSearchOrFilterResult: Boolean)
}