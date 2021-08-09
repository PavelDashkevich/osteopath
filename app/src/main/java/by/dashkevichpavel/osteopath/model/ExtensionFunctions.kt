package by.dashkevichpavel.osteopath.model

import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import java.util.*

fun String.toEditable(): Editable =
    Editable.Factory.getInstance().newEditable(this)

fun Date.formatDateAsString(): String =
    android.text.format.DateFormat.format("dd.MM.yyyy", this).toString()

fun Date.formatTimeAsString(): String =
    android.text.format.DateFormat.format("HH:mm", this).toString()

fun Date.formatDateTimeAsString(): String =
    android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", this).toString()

fun Date.formatDateAsEditable(): Editable =
    this.formatDateAsString().toEditable()

fun Date.formatTimeAsEditable(): Editable =
    this.formatTimeAsString().toEditable()

private fun Date.setPartFromTimeImMillis(timeInMillis: Long, fields: List<Int>) {
    val oldDateTime = Calendar.getInstance()
    oldDateTime.timeInMillis = this.time

    val newDateTime = Calendar.getInstance()
    newDateTime.timeInMillis = timeInMillis

    oldDateTime.copyFields(fields, newDateTime)

    this.time = oldDateTime.timeInMillis
}

fun Date.setDatePartFromTimeInMillis(timeInMillis: Long) {
    setPartFromTimeImMillis(timeInMillis, listOf(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH))
}

fun Date.setTimePartFromTimeInMillis(timeInMillis: Long) {
    setPartFromTimeImMillis(timeInMillis, listOf(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND))
}

fun Calendar.setDateComponents(year: Int, month: Int, dayOfMonth: Int) {
    this.set(Calendar.YEAR, year)
    this.set(Calendar.MONTH, month)
    this.set(Calendar.DAY_OF_MONTH, dayOfMonth)
}

fun Calendar.setTimeComponents(hourOfDay: Int, minute: Int) {
    this.set(Calendar.HOUR_OF_DAY, hourOfDay)
    this.set(Calendar.MINUTE, minute)
}

fun Calendar.copyFields(fields: List<Int>, fromCalendar: Calendar) {
    for (field in fields) {
        this.set(field, fromCalendar.get(field))
    }
}

fun Fragment.actionCallPhoneNumber(phoneNumber: String) {
    if (phoneNumber.isBlank()) return

    startActivity(
        Intent(
            Intent.ACTION_DIAL,
            Uri.fromParts("tel", phoneNumber, null)
        )
    )
}

fun Fragment.actionOpenSocialProfile(link: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
}

fun Fragment.actionOpenInstagram(userName: String) {
    if (userName.isBlank()) return

    try {
        actionOpenSocialProfile("https://instagram.com/_u/$userName")
    } catch(e: ActivityNotFoundException) {
        actionOpenSocialProfile("https://instagram.com/$userName")
    }
}

fun Fragment.setupToolbar(toolbar: Toolbar) {
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
}

fun Cursor.getStringByColumnName(columnName: String, defaultValue: String): String {
    val colIndex = this.getColumnIndex(columnName)

    if (colIndex >= 0) {
        return this.getString(colIndex)
    }

    return defaultValue
}

fun Cursor.getIntByColumnName(columnName: String, defaultValue: Int): Int {
    val colIndex = this.getColumnIndex(columnName)

    if (colIndex >= 0) {
        return this.getInt(colIndex)
    }

    return defaultValue
}