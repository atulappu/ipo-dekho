package com.example.ipotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalysisReportDto(
    @Json(name = "business_summary") val businessSummary: String? = null,
    @Json(name = "financial_snapshot") val financialSnapshot: String? = null,
    @Json(name = "valuation_snapshot") val valuationSnapshot: String? = null,
    @Json(name = "key_positives") val keyPositives: List<String>? = null,
    @Json(name = "key_risks") val keyRisks: List<String>? = null,
    @Json(name = "important_checks") val importantChecks: List<String>? = null,
    @Json(name = "is_ai_generated") val isAiGenerated: Boolean? = null,
    @Json(name = "generated_date") val generatedDate: String? = null,
    @Json(name = "disclaimer") val disclaimer: String? = null
)
