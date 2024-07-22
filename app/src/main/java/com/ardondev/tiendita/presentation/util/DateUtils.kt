package com.ardondev.tiendita.presentation.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

fun getCurrentDateTime(): String {
    val dateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } else {
        SimpleDateFormat("", Locale.getDefault()).format(Calendar.getInstance().time)
    }
    return dateTime
}