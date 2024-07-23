package com.ardondev.tiendita.presentation.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
const val MMMM_d_yyyy_h_mm_a = "MMMM d, yyyy h:mm a"

fun getCurrentDateTime(): String {
    val dateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(
            DateTimeFormatter.ofPattern(
                yyyy_MM_dd_HH_mm_ss,
                Locale.getDefault()
            )
        )
    } else {
        SimpleDateFormat(
            yyyy_MM_dd_HH_mm_ss,
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
    }
    return dateTime
}

fun formatDate(
    input: String,
    inputFormat: String = yyyy_MM_dd_HH_mm_ss,
    outputFormat: String = yyyy_MM_dd_HH_mm_ss,
): String {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
            val dateTime = LocalDateTime.parse(input, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
            return dateTime.format(outputFormatter)
        } else {
            val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
            val dateTime = inputFormatter.parse(inputFormat)
            val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
            return outputFormatter.format(dateTime!!)
        }
    } catch (e: Exception) {
        return input
    }
}