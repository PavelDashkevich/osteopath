package by.dashkevichpavel.osteopath.repositories.localdb.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_3_4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE attachments 
            ADD COLUMN mime_type TEXT NOT NULL DEFAULT ''
            """)
    }
}