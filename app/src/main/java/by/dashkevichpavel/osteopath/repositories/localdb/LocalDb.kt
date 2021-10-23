package by.dashkevichpavel.osteopath.repositories.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.helpers.operationresult.OperationResult
import by.dashkevichpavel.osteopath.repositories.localdb.dao.*
import by.dashkevichpavel.osteopath.repositories.localdb.entity.*
import by.dashkevichpavel.osteopath.repositories.localdb.migrations.Migration_2_3
import by.dashkevichpavel.osteopath.repositories.localdb.migrations.Migration_3_4
import by.dashkevichpavel.osteopath.repositories.localdb.migrations.Migration_4_5

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
                BackupHelper(applicationContext).rollbackRestoringFromBackupIfNeeded()
                instance = openDatabase(applicationContext, DbContract.DATABASE_NAME)
            }

            return instance as LocalDb
        }

        private fun openDatabase(
            applicationContext: Context,
            databaseName: String = DbContract.DATABASE_NAME
        ): LocalDb {
            return Room.databaseBuilder(
                applicationContext,
                LocalDb::class.java,
                databaseName
            )
                .addMigrations(
                    Migration_2_3,
                    Migration_3_4,
                    Migration_4_5
                )
                .setJournalMode(JournalMode.TRUNCATE)
                .build()
        }

        fun tryToOpen(
            applicationContext: Context,
            databaseName: String = DbContract.DATABASE_NAME
        ): OperationResult {
            var result: OperationResult = OperationResult.Success()

            val resultOfTry: Result<LocalDb> = kotlin.runCatching {
                openDatabase(applicationContext, databaseName)
            }

            if (resultOfTry.isSuccess) {
                resultOfTry.getOrNull()?.close()
            } else {
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