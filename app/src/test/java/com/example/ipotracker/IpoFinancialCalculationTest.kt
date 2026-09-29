package com.example.ipotracker

import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ipotracker.utils.DateUtils
import org.junit.Assert.*
import org.junit.Test

class IpoFinancialCalculationTest {

    @Test
    fun testInvestmentCalculation() {
        val ipoPrice = 510.0
        val lotSize = 29
        val lots = 1
        val totalShares = lotSize * lots
        val investment = ipoPrice * totalShares

        assertEquals(29, totalShares)
        assertEquals(14790.0, investment, 0.001)
    }

    @Test
    fun testMultiLotInvestmentCalculation() {
        val ipoPrice = 510.0
        val lotSize = 29
        val lots = 5
        val totalShares = lotSize * lots
        val investment = ipoPrice * totalShares

        assertEquals(145, totalShares)
        assertEquals(73950.0, investment, 0.001)
    }

    @Test
    fun testGmpAndEstimatedReturnsCalculation() {
        val ipoPrice = 510.0
        val lotSize = 29
        val lots = 2
        val gmp = 125.0

        val totalShares = lotSize * lots
        val estimatedListingPrice = ipoPrice + gmp
        val gainPerShare = gmp
        val gainPerLot = gmp * lotSize
        val totalGain = gainPerShare * totalShares
        val gainPercent = (gainPerShare / ipoPrice) * 100

        assertEquals(635.0, estimatedListingPrice, 0.001)
        assertEquals(125.0, gainPerShare, 0.001)
        assertEquals(3625.0, gainPerLot, 0.001)
        assertEquals(7250.0, totalGain, 0.001)
        assertEquals(24.5098, gainPercent, 0.01)
    }

    @Test
    fun testCurrencyFormatting() {
        val formattedCrores = CurrencyFormatter.formatCrores(1850.0)
        assertEquals("₹1,850 Cr", formattedCrores)

        val formattedPercent = CurrencyFormatter.formatPercent(24.51, false)
        assertEquals("24.51%", formattedPercent)

        val formattedSubscription = CurrencyFormatter.formatSubscription(6.39)
        assertEquals("6.39x", formattedSubscription)
    }

    @Test
    fun testDateFormatting() {
        val formatted = DateUtils.formatDisplayDate("2026-09-24")
        assertTrue(formatted.contains("2026"))
        assertTrue(formatted.contains("Sep"))
    }
}
