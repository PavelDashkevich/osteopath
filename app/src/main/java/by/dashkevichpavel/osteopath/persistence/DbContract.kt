package by.dashkevichpavel.osteopath.persistence

import android.provider.BaseColumns

object DbContract {
    const val DATABASE_NAME = "osteopath.db"

    object Disfunction {
        const val TABLE_NAME = "disfunction"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_DISFUNCTION_STATUS_ID = "description_status_id"
        const val COLUMN_NAME_CUSTOMER_ID = "customer_id"
    }

    object Customer {
        const val TABLE_NAME = "customer"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_PHONE = "phone"
        const val COLUMN_NAME_EMAIL = "email"
        const val COLUMN_NAME_INSTAGRAM = "instagram"
        const val COLUMN_NAME_FACEBOOK = "facebook"
        const val COLUMN_NAME_CUSTOMER_STATUS_ID = "customer_status_id"
    }

    object Session {
        const val TABLE_NAME = "session"

        // TODO add table fields
    }
}