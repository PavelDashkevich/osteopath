package by.dashkevichpavel.osteopath.helpers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.dialogs.ItemDeleteConfirmationDialog
import by.dashkevichpavel.osteopath.model.Attachment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import java.lang.IllegalArgumentException
import java.util.*

fun String.toEditable(): Editable =
    Editable.Factory.getInstance().newEditable(this)

fun Date.formatDateAsString(): String =
    android.text.format.DateFormat.format("dd.MM.yyyy", this).toString()

fun Date.formatTimeAsString(): String =
    android.text.format.DateFormat.format("HH:mm", this).toString()

fun Date.formatDateTimeAsString(): String =
    android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", this).toString()

fun Date.formatDateAsDayOfMonthString(): String =
    android.text.format.DateFormat.format("dd", this).toString()

fun Date.formatDateAsMonthShortString(): String =
    android.text.format.DateFormat.format("MMMM", this).toString()
        .substring(0..2)
        .uppercase()

fun Date.formatAsDateTimeStamp(): String =
    android.text.format.DateFormat.format("yyyy_MM_dd_HH_mm", this).toString()

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

fun Fragment.safelyNavigateTo(actionId: Int, args: Bundle? = null) {
    try {
        this.findNavController().navigate(actionId, args)
    } catch (e: IllegalArgumentException) {
        // fires when navigation action repeated before destination is opened,
        // i. e. user taps second time on the same control which must navigate user
        // to some destination
        Log.d("OsteoApp", "Navigation exception: ${e.message}")
    }
}

fun Cursor.getStringByColumnName(columnName: String, defaultValue: String): String {
    val colIndex = this.getColumnIndex(columnName)

    if (colIndex >= 0) {
        return this.getString(colIndex)
    }

    return defaultValue
}

fun RequestManager.loadThumbnailFromAttachmentByMimeType(
    context: Context,
    attachment: Attachment
): RequestBuilder<Drawable> =
    this.load(
        when {
            attachment.mimeType.contains("audio/") ->
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.thumbnail_audio
                )
            attachment.mimeType == "application/pdf" ->
                attachment.thumbnail
            else ->
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.thumbnail_unknown
                )

        }
    )

fun Uri.takePersistableReadWritePermissions(context: Context) {
    context.contentResolver.takePersistableUriPermission(
        this,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
                or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    )
}

fun List<String>.toStringDelimitedByNewLines(): String {
    var result = ""
    this.forEach { item: String ->
        if (item.isNotBlank()) {
            result = result + if (result.isNotBlank()) { "\n\n" } else { "" } + item
        }
    }
    return result
}