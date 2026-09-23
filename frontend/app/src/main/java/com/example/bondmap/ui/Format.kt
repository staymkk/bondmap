package com.example.bondmap.ui

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val ru = Locale("ru", "RU")
private val isoDate: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val ruDate: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun parseDate(value: String?): LocalDate? {
    if (value.isNullOrBlank()) return null
    val raw = value.trim().take(10)
    return try {
        LocalDate.parse(raw, isoDate)
    } catch (_: DateTimeParseException) {
        try {
            LocalDate.parse(raw, ruDate)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}

fun formatDate(value: String?): String {
    val date = parseDate(value) ?: return value?.takeIf { it.isNotBlank() } ?: "—"
    return date.format(ruDate)
}

fun formatChartDate(value: String?): String {
    val date = parseDate(value) ?: return "—"
    return date.format(DateTimeFormatter.ofPattern("dd.MM"))
}

fun formatPercent(value: Double?, digits: Int = 2): String {
    if (value == null) return "—"
    return String.format(ru, "%.${digits}f%%", value)
}

fun formatSignedPp(value: Double?): String {
    if (value == null) return "—"
    return String.format(ru, "%+.2f п.п.", value)
}

fun formatNumber(value: Double?, digits: Int = 2): String {
    if (value == null) return "—"
    return String.format(ru, "%.${digits}f", value)
}
