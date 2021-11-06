package by.dashkevichpavel.osteopath.repositories.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_4_5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE customers 
            ADD COLUMN is_archived INTEGER NOT NULL DEFAULT 0
            """)
    }
}