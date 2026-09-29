package com.example.ipotracker.data.remote

import android.util.Log
import com.example.ipotracker.data.model.*
import com.example.ipotracker.data.remote.mapper.IpoDtoMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IpoRemoteDataSourceImpl(
    private val apiService: IpoApiService
) : IpoRemoteDataSource {

    private val tag = "IpoRemoteDataSource"

    override suspend fun getMarketSummary(): Result<List<MarketIndex>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMarketSummary()
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                Result.success(dtos.map { IpoDtoMapper.mapMarketIndexDtoToDomain(it) })
            } else {
                Log.w(tag, "Failed to fetch market indices: ${response.code()} ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching market indices", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllIpos(
        status: String?,
        category: String?,
        query: String?
    ): Result<List<IpoItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllIpos(status = status, category = category, query = query)
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                Result.success(dtos.map { IpoDtoMapper.mapIpoDtoToDomain(it) })
            } else {
                Log.w(tag, "Failed to fetch IPOs: ${response.code()} ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching IPOs", e)
            Result.failure(e)
        }
    }

    override suspend fun getIpoDetails(id: String): Result<IpoItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getIpoDetails(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(IpoDtoMapper.mapIpoDtoToDomain(response.body()!!))
            } else {
                Log.w(tag, "Failed to fetch IPO details for $id: ${response.code()} ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching IPO details for $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getSubscriptionDetails(id: String): Result<SubscriptionDetails> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSubscriptionDetails(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(IpoDtoMapper.mapSubscriptionDetailsToDomain(response.body()!!))
            } else {
                Log.w(tag, "Failed to fetch subscription for $id: ${response.code()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching subscription for $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getGmpHistory(id: String): Result<List<GmpHistoryItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGmpHistory(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { IpoDtoMapper.mapGmpHistoryItemToDomain(it) })
            } else {
                Log.w(tag, "Failed to fetch GMP history for $id: ${response.code()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching GMP history for $id", e)
            Result.failure(e)
        }
    }

    override suspend fun searchIpos(query: String): Result<List<IpoItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchIpos(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { IpoDtoMapper.mapIpoDtoToDomain(it) })
            } else {
                Log.w(tag, "Failed to search IPOs with query '$query': ${response.code()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Network error searching IPOs for '$query'", e)
            Result.failure(e)
        }
    }
}
