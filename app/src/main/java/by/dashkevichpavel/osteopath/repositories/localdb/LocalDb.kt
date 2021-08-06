package by.dashkevichpavel.osteopath.repositories.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.dashkevichpavel.osteopath.repositories.localdb.dao.CustomerDao
import by.dashkevichpavel.osteopath.repositories.localdb.dao.DisfunctionDao
import by.dashkevichpavel.osteopath.repositories.localdb.dao.SessionDao
import by.dashkevichpavel.osteopath.repositories.localdb.dao.SessionDisfunctionDao
import by.dashkevichpavel.osteopath.repositories.localdb.entity.CustomerEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.DisfunctionEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionEntity
import by.dashkevichpavel.osteopath.repositories.localdb.entity.SessionDisfunctionsEntity

@Database(
    entities = [
        CustomerEntity::class,
        DisfunctionEntity::class,
        SessionEntity::class,
        SessionDisfunctionsEntity::class
    ],
    version = 2
)
@TypeConverters(DbTypeConverters::class)
abstract class LocalDb : RoomDatabase() {
    abstract val customerDao: CustomerDao
    abstract val disfunctionDao: DisfunctionDao
    abstract val sessionDao: SessionDao
    abstract val sessionDisfunctionDao: SessionDisfunctionDao

    companion object {
        private var instance: LocalDb? = null

        fun getInstance(applicationContext: Context): LocalDb {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    applicationContext,
                    LocalDb::class.java,
                    DbContract.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance as LocalDb
        }
    }
}