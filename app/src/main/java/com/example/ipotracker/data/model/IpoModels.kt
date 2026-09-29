package com.example.ipotracker.data.model

enum class IpoCategory {
    MAINBOARD,
    SME
}

enum class IpoStatus {
    OPEN,
    UPCOMING,
    CLOSED,
    ALLOTMENT_PENDING,
    ALLOTMENT_AVAILABLE,
    LISTED;

    val isClosed: Boolean
        get() = this == CLOSED || this == ALLOTMENT_PENDING || this == ALLOTMENT_AVAILABLE || this == LISTED
}

enum class AllotmentStatus {
    PENDING,
    AVAILABLE
}

enum class ListingStatus {
    NOT_LISTED,
    LISTED
}

enum class IpoLifecycleState {
    OPEN,
    CLOSED_ALLOTMENT_PENDING,
    ALLOTMENT_AVAILABLE,
    LISTED
}

data class LiveMarketData(
    val companyName: String,
    val symbol: String,
    val listingPrice: Double,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val week52High: Double,
    val week52Low: Double,
    val issuePrice: Double,
    val listingGainLoss: Double,
    val listingGainLossPercent: Double,
    val isLive: Boolean = true,
    val lastUpdated: String = ""
)

enum class DateStatus {
    COMPLETED,
    ACTIVE,
    UPCOMING
}

enum class GmpTrend {
    POSITIVE,
    NEUTRAL,
    NEGATIVE,
    UNAVAILABLE
}

data class MarketIndex(
    val name: String,
    val value: String,
    val change: String,
    val percentChange: Double,
    val isPositive: Boolean
)

data class SubscriptionRow(
    val category: String,
    val offeredShares: Long,
    val appliedShares: Long,
    val times: Double
)

data class SubscriptionDayProgress(
    val dayLabel: String,
    val date: String,
    val overallTimes: Double,
    val qibTimes: Double,
    val niiTimes: Double,
    val retailTimes: Double,
    val employeeTimes: Double = 0.0,
    val otherTimes: Double = 0.0
)

data class SubscriptionDetails(
    val overallTimes: Double,
    val qibTimes: Double,
    val niiTimes: Double,
    val retailTimes: Double,
    val employeeTimes: Double = 0.0,
    val otherTimes: Double = 0.0,
    val categoryRows: List<SubscriptionRow> = emptyList(),
    val dayProgress: List<SubscriptionDayProgress> = emptyList(),
    val lastUpdated: String = ""
)

data class GmpHistoryItem(
    val date: String,
    val gmp: Double,
    val estimatedListingPrice: Double,
    val estimatedGainPercent: Double,
    val trend: GmpTrend
)

data class FinancialYearData(
    val fiscalYear: String,
    val revenueCr: Double,
    val ebitdaCr: Double,
    val patCr: Double,
    val eps: Double,
    val netWorthCr: Double,
    val totalAssetsCr: Double,
    val totalDebtCr: Double,
    val roePercent: Double,
    val rocePercent: Double,
    val debtToEquity: Double,
    val peRatio: Double
)

data class ImportantDateItem(
    val title: String,
    val dateStr: String,
    val status: DateStatus,
    val note: String = ""
)

data class AllotmentInfo(
    val registrarName: String,
    val registrarUrl: String,
    val bseUrl: String = "https://www.bseindia.com/investors/appli_check.aspx",
    val nseUrl: String = "https://www.nseindia.com/products/content/equities/ipos/ipo_login.htm",
    val allotmentDate: String,
    val isAvailable: Boolean = false,
    val note: String = "Allotment status is published directly by the registrar."
)

data class AnalysisReport(
    val businessSummary: String,
    val financialSnapshot: String,
    val valuationSnapshot: String,
    val keyPositives: List<String>,
    val keyRisks: List<String>,
    val importantChecks: List<String>,
    val isAiGenerated: Boolean = true,
    val generatedDate: String = "",
    val disclaimer: String = "This analysis is for educational and informational purposes only. It does not constitute investment advice or a recommendation to buy or sell."
)

data class IpoItem(
    val id: String,
    val name: String,
    val symbol: String,
    val category: IpoCategory,
    val status: IpoStatus,
    val priceBandMin: Double,
    val priceBandMax: Double,
    val lotSize: Int,
    val minInvestment: Double = 0.0,
    val issueSizeCr: Double = 0.0,
    val freshIssueCr: Double = 0.0,
    val ofsCr: Double = 0.0,
    val openDate: String = "",
    val closeDate: String = "",
    val allotmentDate: String = "",
    val listingDate: String = "",
    val currentGmp: Double = 0.0,
    val estimatedListingPrice: Double = 0.0,
    val estimatedGainPercent: Double = 0.0,
    val lastGmpUpdated: String = "",
    val currentSubscriptionTimes: Double = 0.0,
    val qibTimes: Double = 0.0,
    val niiTimes: Double = 0.0,
    val retailTimes: Double = 0.0,
    val listingPrice: Double? = null,
    val listingGainPercent: Double? = null,
    val currentMarketPrice: Double? = null,
    val currentReturnPercent: Double? = null,
    val description: String = "",
    val sector: String = "",
    val listingExchanges: String = "BSE, NSE",
    val faceValue: Double = 10.0,
    val leadManagers: String = "",
    val registrar: String = "",
    val promoterHoldingPre: Double = 0.0,
    val promoterHoldingPost: Double = 0.0,
    val objectsOfIssue: List<String> = emptyList(),
    val subscriptionDetails: SubscriptionDetails? = null,
    val gmpHistory: List<GmpHistoryItem> = emptyList(),
    val financials: List<FinancialYearData> = emptyList(),
    val importantDates: List<ImportantDateItem> = emptyList(),
    val allotmentInfo: AllotmentInfo? = null,
    val analysisReport: AnalysisReport? = null,
    val isWatchlisted: Boolean = false,
    val isDemoData: Boolean = true,
    val allotmentStatus: AllotmentStatus = AllotmentStatus.PENDING,
    val listingStatus: ListingStatus = ListingStatus.NOT_LISTED,
    val liveMarketData: LiveMarketData? = null
) {
    val lifecycleState: IpoLifecycleState
        get() {
            if (listingStatus == ListingStatus.LISTED || status == IpoStatus.LISTED) {
                return IpoLifecycleState.LISTED
            }
            if (status == IpoStatus.ALLOTMENT_AVAILABLE || allotmentStatus == AllotmentStatus.AVAILABLE || allotmentInfo?.isAvailable == true) {
                return IpoLifecycleState.ALLOTMENT_AVAILABLE
            }
            if (status == IpoStatus.CLOSED || status == IpoStatus.ALLOTMENT_PENDING) {
                return IpoLifecycleState.CLOSED_ALLOTMENT_PENDING
            }
            if (status == IpoStatus.OPEN) {
                return IpoLifecycleState.OPEN
            }
            return IpoLifecycleState.CLOSED_ALLOTMENT_PENDING
        }
}
