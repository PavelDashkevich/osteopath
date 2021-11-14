package by.dashkevichpavel.osteopath.repositories.data

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
        const val COLUMN_NAME_IS_ARCHIVED = "is_archived"
    }

    object Sessions {
        const val TABLE_NAME = "sessions"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_CUSTOMER_ID = "customer_id"
        const val COLUMN_NAME_DATE_TIME = "date_time"
        const val COLUMN_NAME_DATE_TIME_END = "date_time_end"
        const val COLUMN_NAME_PLAN = "plan"
        const val COLUMN_NAME_BODY_CONDITION = "body_condition"
        const val COLUMN_NAME_IS_DONE = "is_done"
    }

    object SessionDisfunctions {
        const val TABLE_NAME = "session_disfunctions"

        const val COLUMN_NAME_SESSION_ID = "session_id"
        const val COLUMN_NAME_DISFUNCTION_ID = "disfunction_id"
    }

    object Attachments {
        const val TABLE_NAME = "attachments"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_CUSTOMER_ID = "customer_id"
        const val COLUMN_NAME_THUMBNAIL = "thumbnail"
        const val COLUMN_NAME_PATH = "path"
        const val COLUMN_NAME_MIME_TYPE = "mime_type"
    }

    object NoSessionPeriods {
        const val TABLE_NAME = "no_session_periods"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_DATE_TIME_START = "date_time_start"
        const val COLUMN_NAME_DATE_TIME_END = "date_time_end"
    }
}