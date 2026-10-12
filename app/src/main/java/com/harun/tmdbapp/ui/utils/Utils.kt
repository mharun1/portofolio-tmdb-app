package com.harun.tmdbapp.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
private val formatter = DateTimeFormatter.ofPattern("yyyy MM dd", Locale("id", "ID"))

@RequiresApi(Build.VERSION_CODES.O)
fun String?.toDisplayDate(): String {
    if (this.isNullOrBlank()) return "Unknown"

    return try {
        val date = LocalDate.parse(this)
        date.format(formatter)
    } catch (e: Exception) {
        "Unknown"
    }
}

fun Int?.toDisplayRuntime(): String {
    if (this == null || this == 0) return "-"

    val hours = this / 60
    val minutes = this % 60

    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}min"
        hours > 0 -> "${hours}h"
        else -> "${minutes}min"
    }
}
