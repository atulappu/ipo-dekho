package com.example.ipotracker.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val indianLocale = Locale("en", "IN")

    /**
     * Formats integer or double amount into Indian Rupee string: e.g. ₹1,25,000 or ₹14,500
     */
    fun formatRupee(amount: Double, includeDecimals: Boolean = false): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(indianLocale)
            if (!includeDecimals) {
                format.maximumFractionDigits = 0
            } else {
                format.maximumFractionDigits = 2
                format.minimumFractionDigits = 2
            }
            format.format(amount)
        } catch (e: Exception) {
            if (includeDecimals) "₹%.2f".format(amount) else "₹%.0f".format(amount)
        }
    }

    fun formatRupee(amount: Long): String {
        return formatRupee(amount.toDouble(), false)
    }

    fun formatRupee(amount: Int): String {
        return formatRupee(amount.toDouble(), false)
    }

    /**
     * Formats number in Cr / Lakhs:
     * e.g. 14500000000 -> "₹1,450 Cr"
     * 1450.0 Cr -> "₹1,450 Cr"
     */
    fun formatCrores(amountInCrores: Double): String {
        val df = DecimalFormat("#,##,##0.##")
        return "₹${df.format(amountInCrores)} Cr"
    }

    fun formatLakhs(amountInLakhs: Double): String {
        val df = DecimalFormat("#,##,##0.##")
        return "${df.format(amountInLakhs)} Lakh"
    }

    /**
     * Formats multiplier e.g. 6.39 -> "6.39x"
     */
    fun formatSubscription(times: Double): String {
        return "%.2fx".format(Locale.US, times)
    }

    /**
     * Formats percentage with sign: e.g. +45.2% or -3.1%
     */
    fun formatPercent(percent: Double, showPlusSign: Boolean = true): String {
        val sign = if (showPlusSign && percent > 0) "+" else ""
        return "$sign%.2f%%".format(Locale.US, percent)
    }

    /**
     * Formats shares with Indian comma system: e.g. 35,68,000
     */
    fun formatShares(shares: Long): String {
        val df = DecimalFormat("#,##,###")
        return df.format(shares)
    }

    /**
     * Formats shares in compact readable Indian units: e.g. 1.27 Cr or 45.98 L or 750 K
     */
    fun formatSharesCompact(shares: Long): String {
        return when {
            shares >= 10_000_000 -> "%.2f Cr".format(Locale.US, shares / 10_000_000.0)
            shares >= 100_000 -> "%.2f L".format(Locale.US, shares / 100_000.0)
            shares >= 1_000 -> "%.1f K".format(Locale.US, shares / 1_000.0)
            else -> shares.toString()
        }
    }

    /**
     * Formats price band with support for fixed price issues and TBA
     */
    fun formatPriceBand(min: Double, max: Double): String {
        return when {
            min <= 0.0 && max <= 0.0 -> "Price TBA"
            min > 0.0 && (max <= 0.0 || min == max) -> "₹${min.toInt()} (Fixed)"
            min <= 0.0 && max > 0.0 -> "Up to ₹${max.toInt()}"
            else -> "₹${min.toInt()} - ₹${max.toInt()}"
        }
    }
}
