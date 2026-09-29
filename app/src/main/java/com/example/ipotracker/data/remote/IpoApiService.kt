package com.example.ipotracker.data.remote

import com.example.ipotracker.data.remote.dto.GmpHistoryItemDto
import com.example.ipotracker.data.remote.dto.IpoDto
import com.example.ipotracker.data.remote.dto.MarketIndexDto
import com.example.ipotracker.data.remote.dto.SubscriptionDetailsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Clean Retrofit 2 REST API service for Indian IPO Tracker endpoints.
 */
interface IpoApiService {

    @GET("market/indices")
    suspend fun getMarketSummary(): Response<List<MarketIndexDto>>

    @GET("ipos")
    suspend fun getAllIpos(
        @Query("status") status: String? = null,
        @Query("category") category: String? = null,
        @Query("q") query: String? = null
    ): Response<List<IpoDto>>

    @GET("ipos/{id}")
    suspend fun getIpoDetails(@Path("id") id: String): Response<IpoDto>

    @GET("ipos/{id}/subscription")
    suspend fun getSubscriptionDetails(@Path("id") id: String): Response<SubscriptionDetailsDto>

    @GET("ipos/{id}/gmp-history")
    suspend fun getGmpHistory(@Path("id") id: String): Response<List<GmpHistoryItemDto>>

    @GET("ipos/search")
    suspend fun searchIpos(@Query("q") query: String): Response<List<IpoDto>>
}
