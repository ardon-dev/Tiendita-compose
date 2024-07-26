package com.ardondev.tiendita.presentation.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
const val MMMM_d_yyyy_h_mm_a = "MMMM d, yyyy h:mm a"
const val MMMM_d_h_mm_a = "MMMM d, h:mm a"
const val dd_MM_yyyy_h_mm_a = "dd/MM/yyyy h:mm a"
const val yyyy_MM_dd = "yyyy-MM-dd"
const val HH_mm_ss = "HH:mm:ss"
const val MMMM_d = "MMMM d"
const val h_mm_a = "h:mm a"

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

fun getCurrentDate(): String {
    val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern(
                yyyy_MM_dd,
                Locale.getDefault()
            )
        )
    } else {
        SimpleDateFormat(
            yyyy_MM_dd,
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
    }
    return date
}

fun getCurrentTime(): String {
    val time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalTime.now().format(
            DateTimeFormatter.ofPattern(
                HH_mm_ss,
                Locale.getDefault()
            )
        )
    } else {
        SimpleDateFormat(
            HH_mm_ss,
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
    }
    return time
}

fun formatDate(
    input: String,
    inputFormat: String = yyyy_MM_dd_HH_mm_ss,
    outputFormat: String = yyyy_MM_dd_HH_mm_ss,
): String {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
            val dateTime = LocalDate.parse(input, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
            val formattedDate = outputFormatter.format(dateTime)
            return formattedDate
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

fun formatTime(
    input: String,
    inputFormat: String = HH_mm_ss,
    outputFormat: String = HH_mm_ss,
): String {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
            val time = LocalTime.parse(input, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
            val formattedDate = outputFormatter.format(time)
            return formattedDate
        } else {
            val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
            val time = inputFormatter.parse(inputFormat)
            val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
            return outputFormatter.format(time!!)
        }
    } catch (e: Exception) {
        return input
    }
}