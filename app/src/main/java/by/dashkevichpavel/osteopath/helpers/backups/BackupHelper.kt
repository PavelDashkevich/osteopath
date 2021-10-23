package by.dashkevichpavel.osteopath.helpers.backups

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import by.dashkevichpavel.osteopath.helpers.formatAsDateTimeStamp
import by.dashkevichpavel.osteopath.helpers.operationresult.OperationResult
import by.dashkevichpavel.osteopath.repositories.localdb.DbContract
import by.dashkevichpavel.osteopath.repositories.localdb.LocalDb
import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepositorySingleton
import by.dashkevichpavel.osteopath.repositories.sharedprefs.BackupSettingsSharedPreferences
import java.io.*
import java.util.*

class BackupHelper(
    private val applicationContext: Context
) {
    private var backupDirFile: DocumentFile? = null
    val backupSettingsSharedPrefs = BackupSettingsSharedPreferences(applicationContext)

    fun rollbackRestoringFromBackupIfNeeded() {
        val currentDb: File = applicationContext.getDatabasePath(DbContract.DATABASE_NAME)
        val rollBackDb = File((currentDb.parent ?: "") + ROLLBACK_FILE_NAME)

        if (rollBackDb.exists() && !currentDb.exists()) {
            rollBackDb.renameTo(currentDb)
        }
    }

    fun checkBackupDir(): BackupDirCheckResult = initBackupDirFile()

    private fun initBackupDirFile(): BackupDirCheckResult {
        var result = BackupDirCheckResult.NOT_SET

        if (backupSettingsSharedPrefs.backupDir.isNotBlank()) {
            result = try {
                backupDirFile = DocumentFile.fromTreeUri(
                    applicationContext,
                    Uri.parse(backupSettingsSharedPrefs.backupDir)
                )

                if (backupDirFile == null || backupDirFile?.exists() == false) {
                    BackupDirCheckResult.SET_NOT_EXISTS
                } else {
                    BackupDirCheckResult.EXISTS_ACCESSIBLE
                }
            } catch (e: SecurityException) {
                BackupDirCheckResult.EXISTS_NOT_ACCESSIBLE
            }
        }

        return result
    }

    suspend fun createBackup(isAutoBackup: Boolean = true): OperationResult {
        val backupDirCheckResult = initBackupDirFile()
        var backupCreateResult: OperationResult = OperationResult.Success()

        if (backupDirCheckResult == BackupDirCheckResult.EXISTS_ACCESSIBLE) {
            val repository = OsteoDbRepositorySingleton.getInstance(applicationContext)
            deleteOldestBackupFile()
            repository.checkPoint()
            backupCreateResult = createBackupFile()
        }

        saveLastBackupStatus(isAutoBackup, backupCreateResult)

        return backupCreateResult
    }

    fun restoreFromBackup(backupFileUri: Uri): OperationResult {
        var result: OperationResult

        // delete rollback file
        val fileDbDir: File = applicationContext
            .getDatabasePath(DbContract.DATABASE_NAME)
            .parentFile
            ?:
            return OperationResult.Error("Database path no found.")

        val fileRollback = File(fileDbDir, ROLLBACK_FILE_NAME)

        if (fileRollback.exists()) {
            val docFileRollback = DocumentFile.fromFile(fileRollback)
            docFileRollback.delete()
        }

        // copy backup file to database folder
        val docFileDbDir: DocumentFile = DocumentFile.fromFile(fileDbDir)
        val fileBackupFileInDbDir = File(fileDbDir, BACKUP_FILE_NAME)
        val docFileBackupFileInBackupDir: DocumentFile =
            DocumentFile.fromSingleUri(applicationContext, backupFileUri)
                ?:
                return OperationResult.Error("Can't work with backup file.")

        val docFileBackupFileInDbDir: DocumentFile =
            if (!fileBackupFileInDbDir.exists()) {
                docFileDbDir.createFile(MIME_TYPE, BACKUP_FILE_NAME)
                    ?:
                    return OperationResult.Error("Can't create backup file in database dir.")
            } else {
                DocumentFile.fromFile(fileBackupFileInDbDir)
            }

        result = copyDocumentFile(docFileBackupFileInBackupDir, docFileBackupFileInDbDir)

        if (result is OperationResult.Error) return result

        // check backup file: can it be used for work or not
        result = LocalDb.tryToOpen(applicationContext, BACKUP_FILE_NAME)

        if (result is OperationResult.Error) {
            docFileBackupFileInDbDir.delete()
            return result
        }

        // close current DB
        OsteoDbRepositorySingleton.getInstance(applicationContext).close()

        // rename current db file to rollback file
        val docFileCurrentDb = DocumentFile.fromFile(
            applicationContext.getDatabasePath(DbContract.DATABASE_NAME)
        )
        if (!docFileCurrentDb.renameTo(ROLLBACK_FILE_NAME)) {
            return OperationResult.Error("Can't make rollback file.")
        }

        // rename backup file in database directory to current db file name
        if (!docFileBackupFileInDbDir.renameTo(DbContract.DATABASE_NAME)) {
            return OperationResult.Error("Can't rename backup file.")
        }

        // try to open new current db file
        result = LocalDb.tryToOpen(applicationContext, DbContract.DATABASE_NAME)

        if (result is OperationResult.Success) {
            docFileCurrentDb.delete()
            return result
        }

        // rollback changes:
        // 1) delete file of current db
        docFileBackupFileInDbDir.delete()

        // 2) rename rollback file to current db filename
        docFileCurrentDb.renameTo(DbContract.DATABASE_NAME)

        // 3) init db
        OsteoDbRepositorySingleton.getInstance(applicationContext)

        return result
    }

    private fun deleteOldestBackupFile() {
        backupDirFile?.let { dirFile ->
            val files = dirFile.listFiles()
            val backupFiles: MutableList<DocumentFile> = mutableListOf()

            files.forEach { docFile: DocumentFile? ->
                docFile?.let {
                    if (docFile.isFile && docFile.name?.contains(BACKUP_NAME_PREFIX) == true) {
                        backupFiles.add(docFile)
                    }
                }
            }

            if (backupFiles.size >= MAX_BACKUPS_NUMBER && backupFiles.size > 0) {
                var oldestBackup: DocumentFile? = null

                // try to find oldest file by date of last modification
                backupFiles.forEach { backupFile ->
                    if (backupFile.lastModified() != 0L &&
                        (oldestBackup?.lastModified() ?: 0L >= backupFile.lastModified())
                    ) {
                        oldestBackup = backupFile
                    }
                }

                // try to find oldest file by display name
                // its format looks like osteo_backup_yyyymmdd_hhMMss.db
                if (oldestBackup == null) {
                    val mapOfFiles: MutableMap<String, DocumentFile> = mutableMapOf()

                    backupFiles.forEach { backupFile ->
                        backupFile.name?.let { fileName: String ->
                            mapOfFiles[fileName] = backupFile
                        }
                    }

                    val sortedFileNames = mapOfFiles.keys.sorted()

                    if (sortedFileNames.isNotEmpty()) {
                        oldestBackup = mapOfFiles[sortedFileNames[0]]
                    }
                }

                oldestBackup?.delete()
            }
        }
    }

    private fun createBackupFile(): OperationResult {
        var backupCreateResult: OperationResult = OperationResult.Success()

        backupDirFile?.let { dirFile ->
            // copy database file to backup folder
            val currentDbFile = DocumentFile.fromFile(
                applicationContext.getDatabasePath(DbContract.DATABASE_NAME)
            )

            var fileExtension = DbContract.DATABASE_NAME.substringAfterLast(".", "")
            fileExtension = if (fileExtension.isNotEmpty()) ".$fileExtension" else ""
            val currentDateTimeStamp = Date(System.currentTimeMillis()).formatAsDateTimeStamp()

            val newBackupFile: DocumentFile? = dirFile.createFile(
                MIME_TYPE,
                "${BACKUP_NAME_PREFIX}_$currentDateTimeStamp$fileExtension"
            )

            if (newBackupFile == null) {
                backupCreateResult = OperationResult.Error(
                    "New backup file was not created."
                )
                return@let
            }

            backupCreateResult = copyDocumentFile(newBackupFile, currentDbFile)
        }

        return backupCreateResult
    }

    private fun copyDocumentFile(docFileSrc: DocumentFile, docFileDst: DocumentFile): OperationResult {
        var result: OperationResult = OperationResult.Success()

        val outputStreamResult: Result<OutputStream?> = kotlin.runCatching {
            applicationContext.contentResolver.openOutputStream(docFileDst.uri)
        }

        val outputStream: OutputStream? = outputStreamResult.getOrNull()

        val inputStreamResult: Result<InputStream?> = kotlin.runCatching {
            applicationContext.contentResolver.openInputStream(docFileSrc.uri)
        }

        val inputStream: InputStream? = inputStreamResult.getOrNull()

        if (inputStream != null && outputStream != null) {
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            var bytesWritten = 0L

            try {
                outputStream.flush()

                do {
                    bytesRead = inputStream.read(buffer)

                    if (bytesRead > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                        bytesWritten += bytesRead
                    }
                } while (bytesRead > 0)
            } catch (e: IOException) {
                e.printStackTrace()

                result = OperationResult.Error(
                    "Exception occurred in copying process."
                )
            } finally {
                closeStream(inputStream)
                closeStream(outputStream)

                if (bytesWritten < docFileSrc.length()) {
                    result = OperationResult.Error(
                        "Copied $bytesWritten bytes from ${docFileSrc.length()}."
                    )

                    if (!docFileDst.delete()) {
                        docFileDst.renameTo(
                            "${INVALID_FILE_PREFIX}.$INVALID_FILE_EXTENSION"
                        )
                    }
                }
            }
        }

        return result
    }

    private fun closeStream(stream: Closeable) {
        try {
            stream.close()
        } catch (e: IOException) {
            Log.d("OsteoApp", "closeStream")
            e.printStackTrace()
        }
    }

    private fun saveLastBackupStatus(isAutoBackup: Boolean, backupCreateResult: OperationResult) {
        backupSettingsSharedPrefs.lastBackupDateTime = System.currentTimeMillis()
        backupSettingsSharedPrefs.isLastBackupTypeAuto = isAutoBackup

        when (backupCreateResult) {
            is OperationResult.Error -> {
                backupSettingsSharedPrefs.isLastBackupFailed = true
                backupSettingsSharedPrefs.lastBackupFailureMessage = backupCreateResult.errorMessage
            }
            is OperationResult.Success -> {
                backupSettingsSharedPrefs.isLastBackupFailed = false
                backupSettingsSharedPrefs.lastBackupFailureMessage = ""
            }
        }

        backupSettingsSharedPrefs.saveSettings()
    }

    companion object {
        const val MAX_BACKUPS_NUMBER = 5
        const val BACKUP_NAME_PREFIX = "osteo_backup"
        const val INVALID_FILE_PREFIX = "corrupted"
        const val INVALID_FILE_EXTENSION = "tmp"
        const val BUFFER_SIZE = 1024
        const val MIME_TYPE = "application/octet-stream" //"application/vnd.sqlite3"
        const val ROLLBACK_FILE_NAME = "osteo_rollback.db"
        const val BACKUP_FILE_NAME = "osteo_backup.db"
    }
}