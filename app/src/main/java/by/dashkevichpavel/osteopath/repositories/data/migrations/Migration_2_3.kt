package by.dashkevichpavel.osteopath.repositories.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_2_3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""CREATE TABLE attachments (
            _id INTEGER NOT NULL PRIMARY KEY,
            customer_id INTEGER NOT NULL DEFAULT 0,
            thumbnail TEXT NOT NULL DEFAULT NULL,
            path TEXT NOT NULL DEFAULT NULL
            )""")
        database.execSQL("CREATE INDEX index_attachments ON attachments (_id, customer_id)")
    }
}