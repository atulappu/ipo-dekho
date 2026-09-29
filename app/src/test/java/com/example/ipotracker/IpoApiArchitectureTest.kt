package com.example.ipotracker

import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.data.remote.DataSourceType
import com.example.ipotracker.data.remote.IpoConfig
import com.example.ipotracker.data.remote.dto.IpoDto
import com.example.ipotracker.data.remote.dto.MarketIndexDto
import com.example.ipotracker.data.remote.dto.SubscriptionDetailsDto
import com.example.ipotracker.data.remote.dto.SubscriptionRowDto
import com.example.ipotracker.data.remote.mapper.IpoDtoMapper
import org.junit.Assert.*
import org.junit.Test

class IpoApiArchitectureTest {

    @Test
    fun testDefaultDataSourceIsMock() {
        // MockDataSource must remain the default development source
        assertEquals(DataSourceType.MOCK, IpoConfig.DATA_SOURCE)
        assertTrue(IpoConfig.isMockMode)
    }

    @Test
    fun testSwitchingDataSourceToApi() {
        val original = IpoConfig.DATA_SOURCE
        try {
            IpoConfig.DATA_SOURCE = DataSourceType.API
            assertEquals(DataSourceType.API, IpoConfig.DATA_SOURCE)
            assertFalse(IpoConfig.isMockMode)
        } finally {
            IpoConfig.DATA_SOURCE = original
        }
    }

    @Test
    fun testIpoDtoMapperFullMapping() {
        val dto = IpoDto(
            id = "test_solar_ipo",
            name = "Test Solar Energy Ltd",
            symbol = "TESTSOLAR",
            category = "MAINBOARD",
            status = "OPEN",
            priceBandMin = 1850.0,
            priceBandMax = 1920.0,
            lotSize = 7,
            minInvestment = 13440.0,
            issueSizeCr = 14500.0,
            freshIssueCr = 10000.0,
            ofsCr = 4500.0,
            openDate = "2026-09-25",
            closeDate = "2026-09-28",
            allotmentDate = "2026-09-29",
            listingDate = "2026-10-01",
            currentGmp = 480.0,
            estimatedListingPrice = 2400.0,
            estimatedGainPercent = 25.0,
            lastGmpUpdated = "26 Sep, 17:00",
            currentSubscriptionTimes = 6.39,
            qibTimes = 8.24,
            niiTimes = 5.42,
            retailTimes = 4.18
        )

        val domain = IpoDtoMapper.mapIpoDtoToDomain(dto)

        assertEquals("test_solar_ipo", domain.id)
        assertEquals("Test Solar Energy Ltd", domain.name)
        assertEquals("TESTSOLAR", domain.symbol)
        assertEquals(IpoCategory.MAINBOARD, domain.category)
        assertEquals(IpoStatus.OPEN, domain.status)
        assertEquals(1850.0, domain.priceBandMin, 0.001)
        assertEquals(1920.0, domain.priceBandMax, 0.001)
        assertEquals(7, domain.lotSize)
        assertEquals(13440.0, domain.minInvestment, 0.001)
        assertEquals(14500.0, domain.issueSizeCr, 0.001)
        assertEquals(480.0, domain.currentGmp, 0.001)
        assertEquals(2400.0, domain.estimatedListingPrice, 0.001)
        assertEquals(25.0, domain.estimatedGainPercent, 0.001)
        assertEquals(6.39, domain.currentSubscriptionTimes, 0.001)
        assertFalse(domain.isDemoData)
    }

    @Test
    fun testIpoDtoMapperSmeCategoryAndNullFallbacks() {
        val dto = IpoDto(
            id = null,
            name = "SME Fresh Organics Ltd",
            symbol = null,
            category = "SME",
            status = "UPCOMING",
            priceBandMin = null,
            priceBandMax = 120.0,
            lotSize = 1000,
            currentGmp = null
        )

        val domain = IpoDtoMapper.mapIpoDtoToDomain(dto)

        assertEquals(IpoCategory.SME, domain.category)
        assertEquals(IpoStatus.UPCOMING, domain.status)
        assertEquals(120.0, domain.priceBandMin, 0.001)
        assertEquals(120.0, domain.priceBandMax, 0.001)
        assertEquals(1000, domain.lotSize)
        assertEquals(120000.0, domain.minInvestment, 0.001)
        assertEquals(0.0, domain.currentGmp, 0.001)
    }

    @Test
    fun testMarketIndexDtoMapper() {
        val dto = MarketIndexDto(
            name = "NIFTY 50",
            value = "25,790.95",
            change = "+112.40",
            percentChange = 0.44,
            isPositive = true
        )

        val domain = IpoDtoMapper.mapMarketIndexDtoToDomain(dto)

        assertEquals("NIFTY 50", domain.name)
        assertEquals("25,790.95", domain.value)
        assertEquals("+112.40", domain.change)
        assertEquals(0.44, domain.percentChange, 0.001)
        assertTrue(domain.isPositive)
    }

    @Test
    fun testSubscriptionDetailsDtoMapper() {
        val dto = SubscriptionDetailsDto(
            overallTimes = 6.39,
            qibTimes = 8.24,
            niiTimes = 5.42,
            retailTimes = 4.18,
            categoryRows = listOf(
                SubscriptionRowDto(category = "QIB", offeredShares = 5000000L, appliedShares = 41200000L, times = 8.24),
                SubscriptionRowDto(category = "Retail", offeredShares = 8000000L, appliedShares = 33440000L, times = 4.18)
            ),
            lastUpdated = "26-Sep-2026 17:05:26"
        )

        val domain = IpoDtoMapper.mapSubscriptionDetailsToDomain(dto)

        assertEquals(6.39, domain.overallTimes, 0.001)
        assertEquals(2, domain.categoryRows.size)
        assertEquals("QIB", domain.categoryRows[0].category)
        assertEquals(8.24, domain.categoryRows[0].times, 0.001)
    }
}
