package by.dashkevichpavel.osteopath.repositories.data.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface UtilDao {
    @RawQuery
    suspend fun checkPoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}