package by.dashkevichpavel.osteopath.repositories.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.helpers.operationresult.OperationResult
import by.dashkevichpavel.osteopath.repositories.data.dao.*
import by.dashkevichpavel.osteopath.repositories.data.entity.*
import by.dashkevichpavel.osteopath.repositories.data.migrations.Migration_2_3
import by.dashkevichpavel.osteopath.repositories.data.migrations.Migration_3_4
import by.dashkevichpavel.osteopath.repositories.data.migrations.Migration_4_5
import by.dashkevichpavel.osteopath.repositories.data.migrations.Migration_5_6

@Database(
    entities = [
        AttachmentEntity::class,
        CustomerEntity::class,
        DisfunctionEntity::class,
        SessionEntity::class,
        SessionDisfunctionsEntity::class
    ],
    version = 6,
    exportSchema = true
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
                BackupHelper(applicationContext).rollbackRestoringFromBackupIfNeeded()
                instance = Room.databaseBuilder(
                    applicationContext,
                    LocalDb::class.java,
                    DbContract.DATABASE_NAME
                )
                    .addMigrations(
                        Migration_2_3,
                        Migration_3_4,
                        Migration_4_5,
                        Migration_5_6
                    )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }

            return instance as LocalDb
        }

        fun tryToOpen(
            applicationContext: Context,
            databaseName: String
        ): OperationResult {
            var result: OperationResult = OperationResult.Success()

            try {
                val db = SQLiteDatabase.openDatabase(
                    applicationContext.getDatabasePath(databaseName).toString(),
                    null,
                    0
                )
                db.close()
            } catch (e: SQLiteException) {
                result = OperationResult.Error("The app can't use this database file.")
            }

            return result
        }

        fun close() {
            instance?.close()
            instance = null
        }
    }
}