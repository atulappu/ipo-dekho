package com.example.ipotracker.data.remote

/**
 * Data source strategy for the IPO Tracker application.
 */
enum class DataSourceType {
    MOCK,
    API
}

/**
 * Centralized Configuration for Data Architecture, Network Endpoints, and Environment.
 */
object IpoConfig {
    /**
     * Single configuration switch: DATA_SOURCE = MOCK / API
     * Kept as MOCK by default for reliable development and offline testing.
     * When switched to API, all IPO screens consume data from remote REST endpoints.
     */
    var DATA_SOURCE: DataSourceType = DataSourceType.MOCK

    /**
     * Preset backend endpoints:
     * - URL_ASPNETCORE_EMULATOR: Points to ASP.NET Core running on host machine via Android Emulator (10.0.2.2:5000)
     * - URL_PRODUCTION_API: Production cloud endpoint
     */
    const val URL_ASPNETCORE_EMULATOR: String = "http://10.0.2.2:5000/api/v1/"
    const val URL_PRODUCTION_API: String = "https://api.ipodekho.in/api/v1/"

    /**
     * Active remote API Base URL. Defaulted to ASP.NET Core emulator address.
     */
    var API_BASE_URL: String = URL_ASPNETCORE_EMULATOR

    /**
     * HTTP connection & read timeouts in seconds.
     */
    const val NETWORK_TIMEOUT_SECONDS: Long = 30L

    /**
     * Returns true if currently running in Mock development mode.
     */
    val isMockMode: Boolean
        get() = DATA_SOURCE == DataSourceType.MOCK

    /**
     * Legacy compatibility flag.
     */
    val isDemoMode: Boolean
        get() = DATA_SOURCE == DataSourceType.MOCK

    const val ENVIRONMENT: String = "DEVELOPMENT"

    /**
     * Securely retrieves the API key at runtime via BuildConfig or Environment.
     * NEVER hardcoded in source code or committed to version control.
     */
    fun getApiKey(): String {
        return try {
            val buildConfigClass = Class.forName("com.aistudio.ipotracker.indiaq.BuildConfig")
            val field = buildConfigClass.getField("IPO_API_KEY")
            (field.get(null) as? String)?.trim() ?: ""
        } catch (e: Exception) {
            System.getenv("IPO_API_KEY")?.trim() ?: ""
        }
    }
}
