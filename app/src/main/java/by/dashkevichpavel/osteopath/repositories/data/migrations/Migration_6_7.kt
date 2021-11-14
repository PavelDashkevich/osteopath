package by.dashkevichpavel.osteopath.repositories.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_6_7 : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""CREATE TABLE IF NOT EXISTS no_session_periods (
            _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
            date_time_start INTEGER NOT NULL, 
            date_time_end INTEGER NOT NULL
        )""")
        database.execSQL("""CREATE INDEX IF NOT EXISTS index_no_session_periods__id 
            ON no_session_periods (_id)""")
    }
}