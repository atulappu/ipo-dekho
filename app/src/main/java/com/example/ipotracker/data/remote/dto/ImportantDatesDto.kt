package com.example.ipotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImportantDateItemDto(
    @Json(name = "title") val title: String? = null,
    @Json(name = "date_str") val dateStr: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "note") val note: String? = null
)
