package com.example.ipotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubscriptionRowDto(
    @Json(name = "category") val category: String? = null,
    @Json(name = "offered_shares") val offeredShares: Long? = null,
    @Json(name = "applied_shares") val appliedShares: Long? = null,
    @Json(name = "times") val times: Double? = null
)

@JsonClass(generateAdapter = true)
data class SubscriptionDayProgressDto(
    @Json(name = "day_label") val dayLabel: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "overall_times") val overallTimes: Double? = null,
    @Json(name = "qib_times") val qibTimes: Double? = null,
    @Json(name = "nii_times") val niiTimes: Double? = null,
    @Json(name = "retail_times") val retailTimes: Double? = null,
    @Json(name = "employee_times") val employeeTimes: Double? = null,
    @Json(name = "other_times") val otherTimes: Double? = null
)

@JsonClass(generateAdapter = true)
data class SubscriptionDetailsDto(
    @Json(name = "overall_times") val overallTimes: Double? = null,
    @Json(name = "qib_times") val qibTimes: Double? = null,
    @Json(name = "nii_times") val niiTimes: Double? = null,
    @Json(name = "retail_times") val retailTimes: Double? = null,
    @Json(name = "employee_times") val employeeTimes: Double? = null,
    @Json(name = "other_times") val otherTimes: Double? = null,
    @Json(name = "category_rows") val categoryRows: List<SubscriptionRowDto>? = null,
    @Json(name = "day_progress") val dayProgress: List<SubscriptionDayProgressDto>? = null,
    @Json(name = "last_updated") val lastUpdated: String? = null
)
