package com.example.ipotracker.domain.repository

import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.MarketIndex
import kotlinx.coroutines.flow.Flow

interface IpoRepository {
    fun getMarketSummary(): Flow<List<MarketIndex>>
    fun getAllIpos(): Flow<List<IpoItem>>
    fun getOpenIpos(): Flow<List<IpoItem>>
    fun getUpcomingIpos(): Flow<List<IpoItem>>
    fun getClosedIpos(): Flow<List<IpoItem>>
    fun getListedIpos(): Flow<List<IpoItem>>
    fun getIpoById(id: String): Flow<IpoItem?>
    fun searchIpos(query: String): Flow<List<IpoItem>>
    fun getWatchlist(): Flow<List<IpoItem>>
    suspend fun toggleWatchlist(ipoId: String)
    fun isWatchlisted(ipoId: String): Flow<Boolean>
    fun getRecentSearches(): Flow<List<String>>
    suspend fun saveSearchQuery(query: String)
    suspend fun deleteSearchQuery(query: String)
    suspend fun clearSearchHistory()
    suspend fun refreshData(): Result<Unit>
}
