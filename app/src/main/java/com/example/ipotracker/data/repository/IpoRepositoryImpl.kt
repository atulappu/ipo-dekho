package com.example.ipotracker.data.repository

import android.util.Log
import com.example.ipotracker.data.local.dao.SearchHistoryDao
import com.example.ipotracker.data.local.dao.WatchlistDao
import com.example.ipotracker.data.local.entity.SearchHistoryEntity
import com.example.ipotracker.data.local.entity.WatchlistEntity
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.data.model.MarketIndex
import com.example.ipotracker.data.remote.DataSourceType
import com.example.ipotracker.data.remote.IpoConfig
import com.example.ipotracker.data.remote.IpoRemoteDataSource
import com.example.ipotracker.data.remote.MockIpoDataSource
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class IpoRepositoryImpl(
    private val remoteDataSource: IpoRemoteDataSource,
    private val watchlistDao: WatchlistDao,
    private val searchHistoryDao: SearchHistoryDao
) : IpoRepository {

    private val tag = "IpoRepository"
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val iposState = MutableStateFlow<List<IpoItem>>(MockIpoDataSource.ipoList)
    private val marketIndicesState = MutableStateFlow<List<MarketIndex>>(MockIpoDataSource.marketIndices)

    init {
        // If API data source is selected, immediately trigger remote fetch
        if (IpoConfig.DATA_SOURCE == DataSourceType.API) {
            repositoryScope.launch {
                fetchRemoteData()
            }
        }
    }

    private suspend fun fetchRemoteData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Fetching live data from remote API: ${IpoConfig.API_BASE_URL}")
            val indicesResult = remoteDataSource.getMarketSummary()
            val iposResult = remoteDataSource.getAllIpos()

            indicesResult.onSuccess { indices ->
                if (indices.isNotEmpty()) {
                    marketIndicesState.value = indices
                }
            }

            iposResult.onSuccess { remoteIpos ->
                if (remoteIpos.isNotEmpty()) {
                    iposState.value = remoteIpos.map { it.copy(isDemoData = false) }
                    Log.d(tag, "Successfully loaded ${remoteIpos.size} IPOs from remote API")
                }
            }.onFailure { err ->
                Log.w(tag, "Remote IPO fetch returned error. Retaining local/fallback cache.", err)
            }

            if (iposResult.isSuccess) Result.success(Unit)
            else Result.failure(iposResult.exceptionOrNull() ?: Exception("Remote fetch failed"))
        } catch (e: Exception) {
            Log.e(tag, "Error during remote data sync", e)
            Result.failure(e)
        }
    }

    override fun getMarketSummary(): Flow<List<MarketIndex>> {
        return marketIndicesState
    }

    override fun getAllIpos(): Flow<List<IpoItem>> {
        return combine(iposState, watchlistDao.getAllWatchlist()) { ipos, watchlistEntities ->
            val watchlistedIds = watchlistEntities.map { it.ipoId }.toSet()
            ipos.map { item ->
                item.copy(
                    isWatchlisted = watchlistedIds.contains(item.id),
                    isDemoData = IpoConfig.DATA_SOURCE == DataSourceType.MOCK
                )
            }
        }
    }

    override fun getOpenIpos(): Flow<List<IpoItem>> {
        return getAllIpos().map { list -> list.filter { it.status == IpoStatus.OPEN } }
    }

    override fun getUpcomingIpos(): Flow<List<IpoItem>> {
        return getAllIpos().map { list -> list.filter { it.status == IpoStatus.UPCOMING } }
    }

    override fun getClosedIpos(): Flow<List<IpoItem>> {
        return getAllIpos().map { list -> list.filter { it.status.isClosed } }
    }

    override fun getListedIpos(): Flow<List<IpoItem>> {
        return getAllIpos().map { list -> list.filter { it.status == IpoStatus.LISTED } }
    }

    override fun getIpoById(id: String): Flow<IpoItem?> {
        return getAllIpos().map { list -> list.find { it.id == id } }
    }

    override fun searchIpos(query: String): Flow<List<IpoItem>> {
        val q = query.trim().lowercase()
        return getAllIpos().map { list ->
            if (q.isEmpty()) emptyList()
            else list.filter {
                it.name.lowercase().contains(q) ||
                it.symbol.lowercase().contains(q) ||
                it.sector.lowercase().contains(q)
            }
        }
    }

    override fun getWatchlist(): Flow<List<IpoItem>> {
        return getAllIpos().map { list -> list.filter { it.isWatchlisted } }
    }

    override suspend fun toggleWatchlist(ipoId: String) {
        try {
            val exists = watchlistDao.isWatchlisted(ipoId).first()
            if (exists) {
                watchlistDao.removeFromWatchlist(ipoId)
            } else {
                watchlistDao.addToWatchlist(WatchlistEntity(ipoId = ipoId))
            }
        } catch (e: Exception) {
            watchlistDao.addToWatchlist(WatchlistEntity(ipoId = ipoId))
        }
    }

    override fun isWatchlisted(ipoId: String): Flow<Boolean> {
        return watchlistDao.isWatchlisted(ipoId)
    }

    override fun getRecentSearches(): Flow<List<String>> {
        return searchHistoryDao.getRecentSearches().map { list ->
            list.map { it.query }
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            searchHistoryDao.insertSearch(SearchHistoryEntity(query = trimmed))
        }
    }

    override suspend fun deleteSearchQuery(query: String) {
        searchHistoryDao.deleteSearch(query)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.clearHistory()
    }

    private fun determineIpoStatus(ipo: IpoItem): IpoStatus {
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH).format(java.util.Date())
        return try {
            if (ipo.listingDate.isNotBlank() && todayStr >= ipo.listingDate) {
                IpoStatus.LISTED
            } else if (ipo.allotmentDate.isNotBlank() && todayStr >= ipo.allotmentDate) {
                if (ipo.allotmentInfo?.isAvailable == true || ipo.allotmentStatus == com.example.ipotracker.data.model.AllotmentStatus.AVAILABLE) {
                    IpoStatus.ALLOTMENT_AVAILABLE
                } else {
                    IpoStatus.ALLOTMENT_PENDING
                }
            } else if (ipo.closeDate.isNotBlank() && todayStr > ipo.closeDate) {
                IpoStatus.CLOSED
            } else if (ipo.openDate.isNotBlank() && todayStr >= ipo.openDate) {
                IpoStatus.OPEN
            } else if (ipo.openDate.isNotBlank() && todayStr < ipo.openDate) {
                IpoStatus.UPCOMING
            } else {
                ipo.status
            }
        } catch (e: Exception) {
            ipo.status
        }
    }

    override suspend fun refreshData(): Result<Unit> {
        return if (IpoConfig.DATA_SOURCE == DataSourceType.API) {
            fetchRemoteData()
        } else {
            kotlinx.coroutines.delay(650) // Realistic network delay for smooth pull-to-refresh UI animation
            val updatedIpos = MockIpoDataSource.ipoList.map { ipo ->
                val currentStatus = determineIpoStatus(ipo)
                ipo.copy(
                    status = currentStatus,
                    lastGmpUpdated = "Just now"
                )
            }
            iposState.value = updatedIpos
            marketIndicesState.value = MockIpoDataSource.marketIndices
            Result.success(Unit)
        }
    }
}
