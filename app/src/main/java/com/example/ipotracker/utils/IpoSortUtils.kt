package com.example.ipotracker.utils

import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.presentation.ipo.IpoSortOption
import java.text.SimpleDateFormat
import java.util.Locale

object IpoSortUtils {

    /**
     * Sorts IPO items according to the specified [IpoSortOption]:
     * - DEFAULT: Preserves original order from API / data source.
     * - GMP_HIGH_TO_LOW: Highest GMP first; unavailable/null (<= 0) placed at the bottom.
     * - CLOSE_DATE: Nearest/upcoming close date first; unavailable/TBD dates placed at the bottom.
     * - NAME_A_TO_Z: Alphabetical ascending order (A to Z).
     * - NAME_Z_TO_A: Alphabetical descending order (Z to A).
     */
    fun sortIpos(ipos: List<IpoItem>, sortOption: IpoSortOption): List<IpoItem> {
        return when (sortOption) {
            IpoSortOption.DEFAULT -> ipos
            IpoSortOption.GMP_HIGH_TO_LOW -> {
                ipos.sortedWith(
                    compareByDescending<IpoItem> { it.currentGmp > 0.0 }
                        .thenByDescending { it.currentGmp }
                )
            }
            IpoSortOption.CLOSE_DATE -> {
                ipos.sortedWith(
                    compareByDescending<IpoItem> { isCloseDateAvailable(it.closeDate) }
                        .thenBy { parseCloseDateToMillis(it.closeDate) }
                        .thenBy { it.closeDate }
                )
            }
            IpoSortOption.NAME_A_TO_Z -> {
                ipos.sortedBy { it.name.trim().lowercase() }
            }
            IpoSortOption.NAME_Z_TO_A -> {
                ipos.sortedByDescending { it.name.trim().lowercase() }
            }
        }
    }

    fun isCloseDateAvailable(dateStr: String?): Boolean {
        if (dateStr.isNullOrBlank()) return false
        val trimmed = dateStr.trim().uppercase()
        if (trimmed == "TBD" || trimmed == "N/A" || trimmed == "--" || trimmed == "-") return false
        return trimmed.any { it.isDigit() }
    }

    fun parseCloseDateToMillis(dateStr: String): Long {
        if (!isCloseDateAvailable(dateStr)) return Long.MAX_VALUE
        val trimmed = dateStr.trim()
        val formats = listOf(
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm",
            "dd MMM yyyy",
            "dd-MMM-yyyy",
            "dd/MM/yyyy"
        )
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                sdf.isLenient = false
                val date = sdf.parse(trimmed)
                if (date != null) return date.time
            } catch (e: Exception) {
                // Continue trying next format
            }
        }
        return Long.MAX_VALUE - 1
    }
}

fun List<IpoItem>.applyIpoSorting(sortOption: IpoSortOption): List<IpoItem> {
    return IpoSortUtils.sortIpos(this, sortOption)
}
