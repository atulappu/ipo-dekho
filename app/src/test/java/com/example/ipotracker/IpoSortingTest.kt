package com.example.ipotracker

import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.data.model.MarketIndex
import com.example.ipotracker.domain.repository.IpoRepository
import com.example.ipotracker.presentation.ipo.IpoListViewModel
import com.example.ipotracker.presentation.ipo.IpoSortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IpoSortingTest {

    private val testDispatcher = StandardTestDispatcher()

    private val testIpos = listOf(
        IpoItem(
            id = "1",
            name = "Zenith Infotech Ltd",
            symbol = "ZENITH",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.OPEN,
            priceBandMin = 200.0,
            priceBandMax = 220.0,
            lotSize = 65,
            currentGmp = 45.0,
            closeDate = "2026-10-05"
        ),
        IpoItem(
            id = "2",
            name = "Apex Solar Energy Ltd",
            symbol = "APEX",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.OPEN,
            priceBandMin = 100.0,
            priceBandMax = 110.0,
            lotSize = 100,
            currentGmp = 150.0,
            closeDate = "2026-09-30"
        ),
        IpoItem(
            id = "3",
            name = "Beta SME Innovations",
            symbol = "BETA",
            category = IpoCategory.SME,
            status = IpoStatus.OPEN,
            priceBandMin = 50.0,
            priceBandMax = 55.0,
            lotSize = 2000,
            currentGmp = 0.0, // Unavailable GMP
            closeDate = "TBD" // Unavailable close date
        ),
        IpoItem(
            id = "4",
            name = "Delta Electronics Ltd",
            symbol = "DELTA",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.OPEN,
            priceBandMin = 300.0,
            priceBandMax = 320.0,
            lotSize = 45,
            currentGmp = 80.0,
            closeDate = "2026-10-02"
        )
    )

    private val mockRepository = object : IpoRepository {
        override fun getAllIpos(): Flow<List<IpoItem>> = flowOf(testIpos)
        override fun getOpenIpos(): Flow<List<IpoItem>> = flowOf(testIpos)
        override fun getUpcomingIpos(): Flow<List<IpoItem>> = flowOf(emptyList())
        override fun getClosedIpos(): Flow<List<IpoItem>> = flowOf(emptyList())
        override fun getListedIpos(): Flow<List<IpoItem>> = flowOf(emptyList())
        override fun getMarketSummary(): Flow<List<MarketIndex>> = flowOf(emptyList())
        override fun getIpoById(id: String): Flow<IpoItem?> = flowOf(testIpos.find { it.id == id })
        override fun searchIpos(query: String): Flow<List<IpoItem>> = flowOf(emptyList())
        override fun getWatchlist(): Flow<List<IpoItem>> = flowOf(emptyList())
        override suspend fun toggleWatchlist(ipoId: String) {}
        override fun isWatchlisted(ipoId: String): Flow<Boolean> = flowOf(false)
        override fun getRecentSearches(): Flow<List<String>> = flowOf(emptyList())
        override suspend fun saveSearchQuery(query: String) {}
        override suspend fun deleteSearchQuery(query: String) {}
        override suspend fun clearSearchHistory() {}
        override suspend fun refreshData(): Result<Unit> = Result.success(Unit)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testDefaultSortMaintainsOriginalOrder() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Enable both Mainboard & SME to check full ordering
        viewModel.setMarketType(mainboard = true, sme = true)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(IpoSortOption.DEFAULT, state.sortOption)
        assertEquals(4, state.filteredIpos.size)
        assertEquals("1", state.filteredIpos[0].id)
        assertEquals("2", state.filteredIpos[1].id)
        assertEquals("3", state.filteredIpos[2].id)
        assertEquals("4", state.filteredIpos[3].id)
    }

    @Test
    fun testGmpHighToLowSortsHighestFirstAndPlacesUnavailableLast() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setMarketType(mainboard = true, sme = true)
        viewModel.setSortOption(IpoSortOption.GMP_HIGH_TO_LOW)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val ipos = state.filteredIpos

        // Order should be: Apex (150.0), Delta (80.0), Zenith (45.0), Beta (0.0/unavailable last)
        assertEquals("Apex Solar Energy Ltd", ipos[0].name)
        assertEquals(150.0, ipos[0].currentGmp, 0.001)

        assertEquals("Delta Electronics Ltd", ipos[1].name)
        assertEquals(80.0, ipos[1].currentGmp, 0.001)

        assertEquals("Zenith Infotech Ltd", ipos[2].name)
        assertEquals(45.0, ipos[2].currentGmp, 0.001)

        assertEquals("Beta SME Innovations", ipos[3].name)
        assertEquals(0.0, ipos[3].currentGmp, 0.001)
    }

    @Test
    fun testCloseDateSortsNearestFirstAndPlacesUnavailableLast() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setMarketType(mainboard = true, sme = true)
        viewModel.setSortOption(IpoSortOption.CLOSE_DATE)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val ipos = state.filteredIpos

        // Order should be: 2026-09-30 (Apex), 2026-10-02 (Delta), 2026-10-05 (Zenith), TBD/unavailable (Beta)
        assertEquals("Apex Solar Energy Ltd", ipos[0].name)
        assertEquals("Delta Electronics Ltd", ipos[1].name)
        assertEquals("Zenith Infotech Ltd", ipos[2].name)
        assertEquals("Beta SME Innovations", ipos[3].name)
    }

    @Test
    fun testNameAscendingAndDescending() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setMarketType(mainboard = true, sme = true)

        // Name A to Z
        viewModel.setSortOption(IpoSortOption.NAME_A_TO_Z)
        testDispatcher.scheduler.advanceUntilIdle()

        var ipos = viewModel.uiState.value.filteredIpos
        assertEquals("Apex Solar Energy Ltd", ipos[0].name)
        assertEquals("Beta SME Innovations", ipos[1].name)
        assertEquals("Delta Electronics Ltd", ipos[2].name)
        assertEquals("Zenith Infotech Ltd", ipos[3].name)

        // Name Z to A
        viewModel.setSortOption(IpoSortOption.NAME_Z_TO_A)
        testDispatcher.scheduler.advanceUntilIdle()

        ipos = viewModel.uiState.value.filteredIpos
        assertEquals("Zenith Infotech Ltd", ipos[0].name)
        assertEquals("Delta Electronics Ltd", ipos[1].name)
        assertEquals("Beta SME Innovations", ipos[2].name)
        assertEquals("Apex Solar Energy Ltd", ipos[3].name)
    }

    @Test
    fun testPreservesMarketTypeFiltersWhenSorting() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Mainboard = ON, SME = OFF by default
        assertTrue(viewModel.uiState.value.isMainboardSelected)
        assertFalse(viewModel.uiState.value.isSmeSelected)

        // Sort by GMP High to Low
        viewModel.setSortOption(IpoSortOption.GMP_HIGH_TO_LOW)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        // Filters must be preserved!
        assertTrue(state.isMainboardSelected)
        assertFalse(state.isSmeSelected)

        // Only Mainboard items present
        assertTrue(state.filteredIpos.all { it.category == IpoCategory.MAINBOARD })
        assertEquals(3, state.filteredIpos.size)
        assertEquals("Apex Solar Energy Ltd", state.filteredIpos[0].name)
        assertEquals("Delta Electronics Ltd", state.filteredIpos[1].name)
        assertEquals("Zenith Infotech Ltd", state.filteredIpos[2].name)
    }
}
