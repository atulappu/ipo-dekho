package com.example.ipotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinancialYearDataDto(
    @Json(name = "fiscal_year") val fiscalYear: String? = null,
    @Json(name = "revenue_cr") val revenueCr: Double? = null,
    @Json(name = "ebitda_cr") val ebitdaCr: Double? = null,
    @Json(name = "pat_cr") val patCr: Double? = null,
    @Json(name = "eps") val eps: Double? = null,
    @Json(name = "net_worth_cr") val netWorthCr: Double? = null,
    @Json(name = "total_assets_cr") val totalAssetsCr: Double? = null,
    @Json(name = "total_debt_cr") val totalDebtCr: Double? = null,
    @Json(name = "roe_percent") val roePercent: Double? = null,
    @Json(name = "roce_percent") val rocePercent: Double? = null,
    @Json(name = "debt_to_equity") val debtToEquity: Double? = null,
    @Json(name = "pe_ratio") val peRatio: Double? = null
)
