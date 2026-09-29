package com.example.ipotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AllotmentInfoDto(
    @Json(name = "registrar_name") val registrarName: String? = null,
    @Json(name = "registrar_url") val registrarUrl: String? = null,
    @Json(name = "bse_url") val bseUrl: String? = null,
    @Json(name = "nse_url") val nseUrl: String? = null,
    @Json(name = "allotment_date") val allotmentDate: String? = null,
    @Json(name = "is_available") val isAvailable: Boolean? = null,
    @Json(name = "note") val note: String? = null
)
