package com.example.ipotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketIndexDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "value") val value: String? = null,
    @Json(name = "change") val change: String? = null,
    @Json(name = "percent_change") val percentChange: Double? = null,
    @Json(name = "is_positive") val isPositive: Boolean? = null
)
