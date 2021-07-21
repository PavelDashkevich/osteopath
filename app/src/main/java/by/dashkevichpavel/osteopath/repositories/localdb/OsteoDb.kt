package by.dashkevichpavel.osteopath.repositories.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.dashkevichpavel.osteopath.repositories.localdb.dao.CustomerDao
import by.dashkevichpavel.osteopath.repositories.localdb.dao.DisfunctionDao
import by.dashkevichpavel.osteopath.repositories.localdb.dao.SessionDao
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
    version = 1
)
@TypeConverters(DbTypeConverters::class)
abstract class OsteoDb : RoomDatabase() {
    abstract val customerDao: CustomerDao
    abstract val disfunctionDao: DisfunctionDao
    abstract val sessionDao: SessionDao

    companion object {
        private var instance: OsteoDb? = null

        fun getInstance(applicationContext: Context): OsteoDb {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    applicationContext,
                    OsteoDb::class.java,
                    DbContract.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance as OsteoDb
        }
    }
}