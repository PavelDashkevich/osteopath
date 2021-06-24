package by.dashkevichpavel.osteopath.persistence

import androidx.room.TypeConverter
import java.util.*

class DbTypeConverters {
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time.toLong()
}