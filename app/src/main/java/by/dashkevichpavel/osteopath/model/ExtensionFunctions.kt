package by.dashkevichpavel.osteopath.model

import android.text.Editable
import java.util.*

fun String.toEditable(): Editable =
    Editable.Factory.getInstance().newEditable(this)

fun Date.formatDateAsString(): String =
    android.text.format.DateFormat.format("dd.MM.yyyy", this).toString()

fun Date.formatDateAsEditable(): Editable =
    this.formatDateAsString().toEditable()