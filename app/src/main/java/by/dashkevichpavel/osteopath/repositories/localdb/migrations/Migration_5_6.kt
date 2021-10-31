package by.dashkevichpavel.osteopath.repositories.localdb.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_5_6 : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE sessions 
            ADD COLUMN date_time_end INTEGER NOT NULL DEFAULT 0
            """)
        database.execSQL("""UPDATE sessions 
            SET date_time_end = date_time + $DEFAULT_SESSION_TIME_MILLIS
            """)
    }

    private const val DEFAULT_SESSION_TIME_MILLIS = 5_400_000L
}