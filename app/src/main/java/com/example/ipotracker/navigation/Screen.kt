package com.example.ipotracker.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object IpoList : Screen("ipo_list?status={status}") {
        fun createRoute(status: String? = null) = if (status != null) "ipo_list?status=$status" else "ipo_list"
    }
    object Gmp : Screen("gmp")
    object Watchlist : Screen("watchlist")
    object More : Screen("more")

    object IpoDetail : Screen("ipo_detail/{ipoId}") {
        fun createRoute(ipoId: String) = "ipo_detail/$ipoId"
    }
    object Calculator : Screen("calculator?ipoId={ipoId}") {
        fun createRoute(ipoId: String? = null) = if (ipoId != null) "calculator?ipoId=$ipoId" else "calculator"
    }
    object Allotment : Screen("allotment?ipoId={ipoId}") {
        fun createRoute(ipoId: String? = null) = if (ipoId != null) "allotment?ipoId=$ipoId" else "allotment"
    }
    object Comparison : Screen("comparison")
    object Calendar : Screen("calendar")
    object Search : Screen("search")
    object ApplicationInfo : Screen("application_info/{ipoId}") {
        fun createRoute(ipoId: String) = "application_info/$ipoId"
    }
    object Disclaimer : Screen("disclaimer")
    object DataSources : Screen("data_sources")
    object Settings : Screen("settings")
    object Subscription : Screen("subscription?ipoId={ipoId}") {
        fun createRoute(ipoId: String? = null) = if (ipoId != null) "subscription?ipoId=$ipoId" else "subscription"
    }
    object LiveMarket : Screen("live_market/{ipoId}") {
        fun createRoute(ipoId: String) = "live_market/$ipoId"
    }
    object AiChat : Screen("ai_chat?initialQuery={initialQuery}") {
        fun createRoute(initialQuery: String? = null) = if (initialQuery != null) "ai_chat?initialQuery=$initialQuery" else "ai_chat"
    }
    object VoiceLive : Screen("voice_live?initialTopic={initialTopic}") {
        fun createRoute(initialTopic: String? = null) = if (initialTopic != null) "voice_live?initialTopic=$initialTopic" else "voice_live"
    }
    object Auth : Screen("auth")
}
