package by.dashkevichpavel.osteopath.repositories.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.dashkevichpavel.osteopath.repositories.localdb.dao.*
import by.dashkevichpavel.osteopath.repositories.localdb.entity.*
import by.dashkevichpavel.osteopath.repositories.localdb.migrations.Migration_2_3
import by.dashkevichpavel.osteopath.repositories.localdb.migrations.Migration_3_4
import by.dashkevichpavel.osteopath.repositories.localdb.migrations.Migration_4_5
import java.io.File

@Database(
    entities = [
        AttachmentEntity::class,
        CustomerEntity::class,
        DisfunctionEntity::class,
        SessionEntity::class,
        SessionDisfunctionsEntity::class
    ],
    version = 5
)
@TypeConverters(DbTypeConverters::class)
abstract class LocalDb : RoomDatabase() {
    abstract val attachmentDao: AttachmentDao
    abstract val customerDao: CustomerDao
    abstract val disfunctionDao: DisfunctionDao
    abstract val sessionDao: SessionDao
    abstract val sessionDisfunctionDao: SessionDisfunctionDao
    abstract val utilDao: UtilDao

    companion object {
        private var instance: LocalDb? = null

        fun getInstance(applicationContext: Context): LocalDb {
            if (instance == null || instance?.isOpen == false) {
                val currentDb: File = applicationContext.getDatabasePath(DbContract.DATABASE_NAME)
                val tempDb = File((currentDb.parent ?: "") + DbContract.TEMP_DATABASE_NAME)

                if (tempDb.exists() && !currentDb.exists()) {
                    tempDb.renameTo(currentDb)
                }

                instance = Room.databaseBuilder(
                    applicationContext,
                    LocalDb::class.java,
                    DbContract.DATABASE_NAME
                )
                    .addMigrations(
                        Migration_2_3,
                        Migration_3_4,
                        Migration_4_5
                    )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }

            return instance as LocalDb
        }
    }
}