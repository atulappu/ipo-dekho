package com.example.ipotracker

import com.example.ipotracker.data.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IpoLifecycleStateMachineTest {

    @Test
    fun testOpenIpoLifecycleState() {
        val ipo = IpoItem(
            id = "test-open",
            name = "Test Open Corp",
            symbol = "TESTOPEN",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.OPEN,
            allotmentStatus = AllotmentStatus.PENDING,
            listingStatus = ListingStatus.NOT_LISTED,
            priceBandMin = 100.0,
            priceBandMax = 110.0,
            lotSize = 50
        )

        assertEquals(IpoLifecycleState.OPEN, ipo.lifecycleState)
        assertFalse(ipo.status.isClosed)
    }

    @Test
    fun testClosedAllotmentPendingLifecycleState() {
        val ipo = IpoItem(
            id = "test-waiting",
            name = "Test Waiting Corp",
            symbol = "TESTWAIT",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.CLOSED,
            allotmentStatus = AllotmentStatus.PENDING,
            listingStatus = ListingStatus.NOT_LISTED,
            priceBandMin = 100.0,
            priceBandMax = 110.0,
            lotSize = 50,
            allotmentInfo = AllotmentInfo(
                registrarName = "Bigshare",
                registrarUrl = "https://bigshare.com",
                allotmentDate = "2026-10-01",
                isAvailable = false
            )
        )

        assertEquals(IpoLifecycleState.CLOSED_ALLOTMENT_PENDING, ipo.lifecycleState)
        assertTrue(ipo.status.isClosed)
    }

    @Test
    fun testAllotmentAvailableLifecycleState() {
        val ipo = IpoItem(
            id = "test-allotment",
            name = "Test Allotment Corp",
            symbol = "TESTALLOT",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.ALLOTMENT_AVAILABLE,
            allotmentStatus = AllotmentStatus.AVAILABLE,
            listingStatus = ListingStatus.NOT_LISTED,
            priceBandMin = 100.0,
            priceBandMax = 110.0,
            lotSize = 50,
            allotmentInfo = AllotmentInfo(
                registrarName = "Link Intime",
                registrarUrl = "https://linkintime.co.in",
                allotmentDate = "2026-09-26",
                isAvailable = true
            )
        )

        assertEquals(IpoLifecycleState.ALLOTMENT_AVAILABLE, ipo.lifecycleState)
        assertTrue(ipo.status.isClosed)
    }

    @Test
    fun testListedLifecycleState() {
        val ipo = IpoItem(
            id = "test-listed",
            name = "Test Listed Corp",
            symbol = "TESTLIST",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.LISTED,
            allotmentStatus = AllotmentStatus.AVAILABLE,
            listingStatus = ListingStatus.LISTED,
            priceBandMin = 100.0,
            priceBandMax = 110.0,
            lotSize = 50,
            liveMarketData = LiveMarketData(
                companyName = "Test Listed Corp",
                symbol = "TESTLIST",
                listingPrice = 140.0,
                currentPrice = 152.0,
                change = 12.0,
                changePercent = 8.57,
                volume = 5000000L,
                week52High = 160.0,
                week52Low = 135.0,
                issuePrice = 110.0,
                listingGainLoss = 30.0,
                listingGainLossPercent = 27.27
            )
        )

        assertEquals(IpoLifecycleState.LISTED, ipo.lifecycleState)
        assertTrue(ipo.status.isClosed)
    }
}
