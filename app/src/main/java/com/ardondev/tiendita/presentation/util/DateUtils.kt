package com.ardondev.tiendita.presentation.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
const val MMMM_d_yyyy_h_mm_a = "MMMM d, yyyy h:mm a"
const val MMMM_d_h_mm_a = "MMMM d, h:mm a"
const val dd_MM_yyyy_h_mm_a = "dd/MM/yyyy h:mm a"

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
    legibleDate: Boolean = false
): String {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
            val dateTime = LocalDateTime.parse(input, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
            val daysBetween = ChronoUnit.DAYS.between(dateTime.toLocalDate(), LocalDateTime.now())
            val formattedDate = outputFormatter.format(dateTime)
            return if (legibleDate) {
                when (daysBetween) {
                    0L -> "Hoy, ${DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()).format(dateTime)}"
                    1L -> "Ayer, ${DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()).format(dateTime)} "
                    else -> formattedDate
                }
            } else {
                formattedDate
            }
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