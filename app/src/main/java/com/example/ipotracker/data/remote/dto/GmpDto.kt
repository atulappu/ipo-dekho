package com.example.ipotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GmpHistoryItemDto(
    @Json(name = "date") val date: String? = null,
    @Json(name = "gmp") val gmp: Double? = null,
    @Json(name = "estimated_listing_price") val estimatedListingPrice: Double? = null,
    @Json(name = "estimated_gain_percent") val estimatedGainPercent: Double? = null,
    @Json(name = "trend") val trend: String? = null
)
