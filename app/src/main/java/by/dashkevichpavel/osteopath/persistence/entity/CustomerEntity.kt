package by.dashkevichpavel.osteopath.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.model.CustomerStatus
import by.dashkevichpavel.osteopath.persistence.DbContract
import java.util.*

@Entity(
    tableName = DbContract.Customers.TABLE_NAME,
    indices = [Index(DbContract.Customers.COLUMN_NAME_ID, DbContract.Customers.COLUMN_NAME_NAME)]
)
data class CustomerEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_ID)
    var id: Long = 0,

    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_NAME)
    var name: String = "",

    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_BIRTH_DATE)
    var birthDate: Date = Date(0),

    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_PHONE)
    var phone: String = "",

    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_EMAIL)
    var email: String = "",

    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_INSTAGRAM)
    var instagram: String = "",

    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_FACEBOOK)
    var facebook: String = "",

    @ColumnInfo(name = DbContract.Customers.COLUMN_NAME_CUSTOMER_STATUS_ID)
    var customerStatusId: Int = CustomerStatus.WORK.id
) {
    constructor(customer: Customer) : this(
        id = customer.id,
        name = customer.name,
        birthDate = customer.birthDate,
        phone = customer.phone,
        email = customer.email,
        instagram = customer.instagram,
        facebook = customer.facebook,
        customerStatusId = customer.customerStatusId
    )
}