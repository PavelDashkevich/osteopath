package by.dashkevichpavel.osteopath.helpers.backups

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import by.dashkevichpavel.osteopath.helpers.formatAsDateTimeStamp
import by.dashkevichpavel.osteopath.repositories.localdb.DbContract
import by.dashkevichpavel.osteopath.repositories.localdb.OsteoDbRepositorySingleton
import by.dashkevichpavel.osteopath.repositories.sharedprefs.BackupSettingsSharedPreferences
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BackupHelper(
    private val applicationContext: Context
) {
    private var backupDirFile: DocumentFile? = null
    val backupSettingsSharedPrefs = BackupSettingsSharedPreferences(applicationContext)

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

    suspend fun createBackup(isAutoBackup: Boolean = true): BackupCreateResult {
        val backupDirCheckResult = initBackupDirFile()
        var backupCreateResult: BackupCreateResult = BackupCreateResult.Success()

        if (backupDirCheckResult == BackupDirCheckResult.EXISTS_ACCESSIBLE) {
            val repository = OsteoDbRepositorySingleton.getInstance(applicationContext)
            deleteOldestBackupFile()
            repository.checkPoint()
            backupCreateResult = createBackupFile()
        }

        saveLastBackupStatus(isAutoBackup, backupCreateResult)

        return backupCreateResult
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

    private fun createBackupFile(): BackupCreateResult {
        var backupCreateResult: BackupCreateResult = BackupCreateResult.Success()

        backupDirFile?.let { dirFile ->
            // copy database file to backup folder
            val currentDbFile = DocumentFile.fromFile(
                applicationContext.getDatabasePath(DbContract.DATABASE_NAME)
            )

            /*val mimeType = applicationContext.contentResolver.getType(currentDbFile.uri)

            if (mimeType == null) {
                backupCreateResult = BackupCreateResult.Error(
                    "MIME type is not defined for database file."
                )
                return@let
            }*/

            var fileExtension = DbContract.DATABASE_NAME.substringAfterLast(".", "")
            fileExtension = if (fileExtension.isNotEmpty()) ".$fileExtension" else ""
            val currentDateTimeStamp = Date(System.currentTimeMillis()).formatAsDateTimeStamp()

            val newBackupFile: DocumentFile? = dirFile.createFile(
                "application/vnd.sqlite3", // mimeType,
                "${BACKUP_NAME_PREFIX}_$currentDateTimeStamp$fileExtension"
            )

            if (newBackupFile == null) {
                backupCreateResult = BackupCreateResult.Error(
                    "New backup file was not created."
                )
                return@let
            }

            val outputStreamResult: Result<OutputStream?> = kotlin.runCatching {
                applicationContext.contentResolver.openOutputStream(newBackupFile.uri)
            }

            val outputStream: OutputStream? = outputStreamResult.getOrNull()

            val inputStreamResult: Result<InputStream?> = kotlin.runCatching {
                applicationContext.contentResolver.openInputStream(currentDbFile.uri)
            }

            val inputStream: InputStream? = inputStreamResult.getOrNull()

            if (inputStream != null && outputStream != null) {
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int = 0
                var bytesWritten = 0L

                try {
                    do {
                        bytesRead = inputStream.read(buffer)

                        if (bytesRead > 0) {
                            outputStream.write(buffer, 0, bytesRead)
                            bytesWritten += bytesRead
                        }
                    } while (bytesRead > 0)
                } catch (e: IOException) {
                    e.printStackTrace()

                    backupCreateResult = BackupCreateResult.Error(
                        "Exception occurred in copying process."
                    )
                } finally {
                    closeStream(inputStream)
                    closeStream(outputStream)

                    if (bytesWritten < currentDbFile.length()) {
                        backupCreateResult = BackupCreateResult.Error(
                            "Copied $bytesWritten bytes from ${currentDbFile.length()}."
                        )

                        if (!newBackupFile.delete()) {
                            newBackupFile.renameTo(
                                INVALID_FILE_PREFIX + "_" +
                                        currentDateTimeStamp + "." +
                                        INVALID_FILE_EXTENSION
                            )
                        }
                    }
                }
            }
        }

        return backupCreateResult
    }

    private fun closeStream(stream: Closeable) {
        try {
            stream.close()
        } catch (e: IOException) {
            Log.d("OsteoApp", "closeStream")
            e.printStackTrace()
        }
    }

    private fun saveLastBackupStatus(isAutoBackup: Boolean, backupCreateResult: BackupCreateResult) {
        backupSettingsSharedPrefs.lastBackupDateTime = System.currentTimeMillis()
        backupSettingsSharedPrefs.isLastBackupTypeAuto = isAutoBackup

        when (backupCreateResult) {
            is BackupCreateResult.Error -> {
                backupSettingsSharedPrefs.isLastBackupFailed = true
                backupSettingsSharedPrefs.lastBackupFailureMessage = backupCreateResult.errorMessage
            }
            is BackupCreateResult.Success -> {
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
    }
}