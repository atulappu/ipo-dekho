package com.example.ipotracker.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private val timestampFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.ENGLISH)
    private val parseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val date = parseFormat.parse(dateStr) ?: return dateStr
            displayFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatLastUpdated(timestamp: Long = System.currentTimeMillis()): String {
        return timestampFormat.format(Date(timestamp))
    }

    fun formatDisplayDateTime(timestamp: Long = System.currentTimeMillis()): String {
        return SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date(timestamp))
    }

    /**
     * Calculates countdown string from closing date "yyyy-MM-dd HH:mm" or "yyyy-MM-dd"
     */
    fun getCountdownString(closingDateStr: String): String {
        return try {
            val targetDate = if (closingDateStr.contains(" ")) {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(closingDateStr)
            } else {
                // assume 17:00 IST market close
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse("$closingDateStr 17:00")
            } ?: return "Check schedule"

            val diff = targetDate.time - System.currentTimeMillis()
            if (diff <= 0) {
                return "Closed"
            }

            val days = TimeUnit.MILLISECONDS.toDays(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

            when {
                days > 0 -> "$days Days $hours Hours"
                hours > 0 -> "$hours Hours $minutes Mins"
                else -> "$minutes Mins"
            }
        } catch (e: Exception) {
            "Open"
        }
    }
}
