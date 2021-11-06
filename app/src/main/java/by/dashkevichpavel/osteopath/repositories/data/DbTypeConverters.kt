package by.dashkevichpavel.osteopath.repositories.data

import androidx.room.TypeConverter
import java.util.*

class DbTypeConverters {
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time.toLong()
}