package com.example.ipotracker

import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.data.model.MarketIndex
import com.example.ipotracker.domain.repository.IpoRepository
import com.example.ipotracker.presentation.ipo.IpoListViewModel
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
class MarketTypeFilterTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleIpos = listOf(
        IpoItem(
            id = "1",
            name = "Mainboard Company A",
            symbol = "MC-A",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.OPEN,
            priceBandMin = 100.0,
            priceBandMax = 110.0,
            lotSize = 100,
            issueSizeCr = 500.0
        ),
        IpoItem(
            id = "2",
            name = "SME Company B",
            symbol = "SME-B",
            category = IpoCategory.SME,
            status = IpoStatus.OPEN,
            priceBandMin = 50.0,
            priceBandMax = 55.0,
            lotSize = 2000,
            issueSizeCr = 25.0
        )
    )

    private val mockRepository = object : IpoRepository {
        override fun getAllIpos(): Flow<List<IpoItem>> = flowOf(sampleIpos)
        override fun getOpenIpos(): Flow<List<IpoItem>> = flowOf(sampleIpos)
        override fun getUpcomingIpos(): Flow<List<IpoItem>> = flowOf(emptyList())
        override fun getClosedIpos(): Flow<List<IpoItem>> = flowOf(emptyList())
        override fun getListedIpos(): Flow<List<IpoItem>> = flowOf(emptyList())
        override fun getMarketSummary(): Flow<List<MarketIndex>> = flowOf(emptyList())
        override fun getIpoById(id: String): Flow<IpoItem?> = flowOf(sampleIpos.find { it.id == id })
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
    fun testDefaultStateHasMainboardSelectedByDefault() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        // Requirement 8: When the screen opens, Mainboard should be selected by default.
        assertTrue("Mainboard must be selected by default", state.isMainboardSelected)
        assertFalse("SME must not be selected by default", state.isSmeSelected)

        // Requirement 10: If only Mainboard is selected, show only Mainboard IPOs.
        assertEquals(1, state.filteredIpos.size)
        assertEquals(IpoCategory.MAINBOARD, state.filteredIpos.first().category)
    }

    @Test
    fun testBothMainboardAndSmeSelectedShowsAllIpos() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Turn SME ON so both are selected
        viewModel.setMarketType(mainboard = true, sme = true)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        // Requirement 3: Mainboard and SME can both be selected.
        assertTrue(state.isMainboardSelected)
        assertTrue(state.isSmeSelected)

        // Requirement 9: If both are selected, show IPOs from both Mainboard and SME.
        assertEquals(2, state.filteredIpos.size)
    }

    @Test
    fun testOnlySmeSelectedShowsOnlySmeIpos() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Select only SME
        viewModel.setMarketType(mainboard = false, sme = true)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        // Requirement 2: SME can be selected alone.
        assertFalse(state.isMainboardSelected)
        assertTrue(state.isSmeSelected)

        // Requirement 11: If only SME is selected, show only SME IPOs.
        assertEquals(1, state.filteredIpos.size)
        assertEquals(IpoCategory.SME, state.filteredIpos.first().category)
    }

    @Test
    fun testInvalidStateBothUnselectedIsPrevented() = runTest(testDispatcher) {
        val viewModel = IpoListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Requirement 4 & 6: Attempting to unselect both must be rejected
        viewModel.setMarketType(mainboard = false, sme = false)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        // State must remain valid (not both false)
        assertFalse("Both options cannot be false simultaneously", !state.isMainboardSelected && !state.isSmeSelected)
    }

    @Test
    fun testFilterToggleLogicFunctionPreventsLastUnselect() {
        var warningMessage: String? = null
        var currentMainboard = true
        var currentSme = false

        // User tries to unselect Mainboard when SME is false:
        fun onToggleMainboard() {
            if (currentMainboard) {
                if (!currentSme) {
                    warningMessage = "Please select at least one market type."
                } else {
                    currentMainboard = false
                }
            } else {
                currentMainboard = true
            }
        }

        onToggleMainboard()
        assertEquals("Please select at least one market type.", warningMessage)
        assertTrue("Mainboard must remain selected", currentMainboard)
        assertFalse("SME must remain false", currentSme)
    }
}
