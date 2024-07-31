package com.ardondev.tiendita.presentation.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

val DECIMAL_PATTERN = Regex("^\\d*\\.?\\d{0,2}$")

fun formatToIntNumber(input: String): String {
    return input.replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
}

fun getOnlyDigits(input: String, decimal: Boolean = false): String {
    return if (decimal) input.replace(Regex("[^\\d.]"), "") else input.replace(Regex("[^\\d]"), "")
}

fun formatToUSD(input: String): String {
    val cleanString = getOnlyDigits(input, true)
    if (cleanString.isEmpty()) return "0.00"
    val number = cleanString.toDoubleOrNull() ?: return ""
    val decimalFormat = DecimalFormat("######0.00", DecimalFormatSymbols(Locale.US))
    return decimalFormat.format(number)
}