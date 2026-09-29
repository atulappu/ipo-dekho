package com.example.ipotracker.data.remote

import com.example.ipotracker.data.model.*

/**
 * Contract for remote IPO network data source operations.
 */
interface IpoRemoteDataSource {
    suspend fun getMarketSummary(): Result<List<MarketIndex>>
    suspend fun getAllIpos(status: String? = null, category: String? = null, query: String? = null): Result<List<IpoItem>>
    suspend fun getIpoDetails(id: String): Result<IpoItem>
    suspend fun getSubscriptionDetails(id: String): Result<SubscriptionDetails>
    suspend fun getGmpHistory(id: String): Result<List<GmpHistoryItem>>
    suspend fun searchIpos(query: String): Result<List<IpoItem>>
}
