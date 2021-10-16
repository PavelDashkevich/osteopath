package by.dashkevichpavel.osteopath.helpers.backups

sealed class BackupCreateResult {
    @Suppress("CanSealedSubClassBeObject")
    class Success : BackupCreateResult()
    class Error(val errorMessage: String) : BackupCreateResult()
}