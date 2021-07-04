package by.dashkevichpavel.osteopath.persistence

import android.provider.BaseColumns

object DbContract {
    const val DATABASE_NAME = "osteopath.db"

    object Disfunctions {
        const val TABLE_NAME = "disfunctions"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_DISFUNCTION_STATUS_ID = "description_status_id"
        const val COLUMN_NAME_CUSTOMER_ID = "customer_id"
    }

    object Customers {
        const val TABLE_NAME = "customers"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_BIRTH_DATE = "birth_date"
        const val COLUMN_NAME_PHONE = "phone"
        const val COLUMN_NAME_EMAIL = "email"
        const val COLUMN_NAME_INSTAGRAM = "instagram"
        const val COLUMN_NAME_FACEBOOK = "facebook"
        const val COLUMN_NAME_CUSTOMER_STATUS_ID = "customer_status_id"
    }

    object Sessions {
        const val TABLE_NAME = "sessions"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_CUSTOMER_ID = "customer_id"
        const val COLUMN_NAME_DATE_TIME = "date_time"
        const val COLUMN_NAME_IS_DONE = "is_done"
    }

    object DisfunctionsInSession {
        const val TABLE_NAME = "disfunctions_in_session"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_SESSION_ID = "session_id"
        const val COLUMN_NAME_DISFUNCTION_ID = "disfunction_id"
    }
}