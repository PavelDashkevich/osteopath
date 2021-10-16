package by.dashkevichpavel.osteopath.features.settings.backup.create

enum class BackupCreateUIState {
    START,
    NO_BACKUP_DIR,
    BACKUP_DIR_NOT_EXISTS,
    BACKUP_DIR_NOT_ACCESSIBLE,
    READY,
    MANUAL_BACKUP_IN_PROGRESS,
    FINISH
}