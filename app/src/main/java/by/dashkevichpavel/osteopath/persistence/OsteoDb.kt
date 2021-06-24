package by.dashkevichpavel.osteopath.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.dashkevichpavel.osteopath.persistence.dao.CustomerDao
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity
import by.dashkevichpavel.osteopath.persistence.entity.DisfunctionEntity

@Database(
    entities = [
        CustomerEntity::class,
        DisfunctionEntity::class
    ],
    version = 1
)
@TypeConverters(DbTypeConverters::class)
abstract class OsteoDb : RoomDatabase() {
    abstract val customerDao: CustomerDao

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