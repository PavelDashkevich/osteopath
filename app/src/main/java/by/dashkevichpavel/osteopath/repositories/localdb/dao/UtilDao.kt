package by.dashkevichpavel.osteopath.repositories.localdb.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface UtilDao {
    @RawQuery
    suspend fun checkPoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}