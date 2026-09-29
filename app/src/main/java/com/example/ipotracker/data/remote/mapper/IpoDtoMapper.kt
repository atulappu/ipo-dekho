package com.example.ipotracker.data.remote.mapper

import com.example.ipotracker.data.model.*
import com.example.ipotracker.data.remote.dto.*

object IpoDtoMapper {

    fun mapIpoDtoToDomain(dto: IpoDto): IpoItem {
        val category = parseCategory(dto.category)
        val status = parseStatus(dto.status)
        val maxPrice = dto.priceBandMax ?: dto.priceBandMin ?: 100.0
        val minPrice = dto.priceBandMin ?: maxPrice
        val lot = dto.lotSize ?: 1
        val minInvest = dto.minInvestment ?: (maxPrice * lot)

        return IpoItem(
            id = dto.id ?: dto.symbol?.lowercase() ?: "ipo_${System.currentTimeMillis()}",
            name = dto.name ?: "Unknown IPO",
            symbol = dto.symbol ?: (dto.name?.take(6)?.uppercase() ?: "IPO"),
            category = category,
            status = status,
            priceBandMin = minPrice,
            priceBandMax = maxPrice,
            lotSize = lot,
            minInvestment = minInvest,
            issueSizeCr = dto.issueSizeCr ?: 0.0,
            freshIssueCr = dto.freshIssueCr ?: 0.0,
            ofsCr = dto.ofsCr ?: 0.0,
            openDate = dto.openDate ?: "",
            closeDate = dto.closeDate ?: "",
            allotmentDate = dto.allotmentDate ?: "",
            listingDate = dto.listingDate ?: "",
            currentGmp = dto.currentGmp ?: 0.0,
            estimatedListingPrice = dto.estimatedListingPrice ?: (maxPrice + (dto.currentGmp ?: 0.0)),
            estimatedGainPercent = dto.estimatedGainPercent ?: if (maxPrice > 0) ((dto.currentGmp ?: 0.0) / maxPrice * 100.0) else 0.0,
            lastGmpUpdated = dto.lastGmpUpdated ?: "Recent",
            currentSubscriptionTimes = dto.currentSubscriptionTimes ?: 0.0,
            qibTimes = dto.qibTimes ?: 0.0,
            niiTimes = dto.niiTimes ?: 0.0,
            retailTimes = dto.retailTimes ?: 0.0,
            listingPrice = dto.listingPrice,
            listingGainPercent = dto.listingGainPercent,
            currentMarketPrice = dto.currentMarketPrice,
            currentReturnPercent = dto.currentReturnPercent,
            description = dto.description ?: "Public offering in Indian equities market.",
            sector = dto.sector ?: "Diversified",
            listingExchanges = dto.listingExchanges ?: "BSE, NSE",
            faceValue = dto.faceValue ?: 10.0,
            leadManagers = dto.leadManagers ?: "Lead Managers",
            registrar = dto.registrar ?: "Registrar",
            promoterHoldingPre = dto.promoterHoldingPre ?: 0.0,
            promoterHoldingPost = dto.promoterHoldingPost ?: 0.0,
            objectsOfIssue = dto.objectsOfIssue ?: emptyList(),
            subscriptionDetails = dto.subscriptionDetails?.let { mapSubscriptionDetailsToDomain(it) },
            gmpHistory = dto.gmpHistory?.map { mapGmpHistoryItemToDomain(it) } ?: emptyList(),
            financials = dto.financials?.map { mapFinancialYearDataToDomain(it) } ?: emptyList(),
            importantDates = dto.importantDates?.map { mapImportantDateItemToDomain(it) } ?: emptyList(),
            allotmentInfo = dto.allotmentInfo?.let { mapAllotmentInfoToDomain(it) },
            analysisReport = dto.analysisReport?.let { mapAnalysisReportToDomain(it) },
            isWatchlisted = false,
            isDemoData = false
        )
    }

    fun mapMarketIndexDtoToDomain(dto: MarketIndexDto): MarketIndex {
        return MarketIndex(
            name = dto.name ?: "NIFTY",
            value = dto.value ?: "0.00",
            change = dto.change ?: "0.00",
            percentChange = dto.percentChange ?: 0.0,
            isPositive = dto.isPositive ?: ((dto.percentChange ?: 0.0) >= 0.0)
        )
    }

    fun mapSubscriptionDetailsToDomain(dto: SubscriptionDetailsDto): SubscriptionDetails {
        return SubscriptionDetails(
            overallTimes = dto.overallTimes ?: 0.0,
            qibTimes = dto.qibTimes ?: 0.0,
            niiTimes = dto.niiTimes ?: 0.0,
            retailTimes = dto.retailTimes ?: 0.0,
            employeeTimes = dto.employeeTimes ?: 0.0,
            otherTimes = dto.otherTimes ?: 0.0,
            categoryRows = dto.categoryRows?.map {
                SubscriptionRow(
                    category = it.category ?: "",
                    offeredShares = it.offeredShares ?: 0L,
                    appliedShares = it.appliedShares ?: 0L,
                    times = it.times ?: 0.0
                )
            } ?: emptyList(),
            dayProgress = dto.dayProgress?.map {
                SubscriptionDayProgress(
                    dayLabel = it.dayLabel ?: "",
                    date = it.date ?: "",
                    overallTimes = it.overallTimes ?: 0.0,
                    qibTimes = it.qibTimes ?: 0.0,
                    niiTimes = it.niiTimes ?: 0.0,
                    retailTimes = it.retailTimes ?: 0.0,
                    employeeTimes = it.employeeTimes ?: 0.0,
                    otherTimes = it.otherTimes ?: 0.0
                )
            } ?: emptyList(),
            lastUpdated = dto.lastUpdated ?: ""
        )
    }

    fun mapGmpHistoryItemToDomain(dto: GmpHistoryItemDto): GmpHistoryItem {
        val trend = when (dto.trend?.uppercase()) {
            "POSITIVE" -> GmpTrend.POSITIVE
            "NEGATIVE" -> GmpTrend.NEGATIVE
            "NEUTRAL" -> GmpTrend.NEUTRAL
            else -> if ((dto.gmp ?: 0.0) > 0) GmpTrend.POSITIVE else GmpTrend.NEUTRAL
        }
        return GmpHistoryItem(
            date = dto.date ?: "",
            gmp = dto.gmp ?: 0.0,
            estimatedListingPrice = dto.estimatedListingPrice ?: 0.0,
            estimatedGainPercent = dto.estimatedGainPercent ?: 0.0,
            trend = trend
        )
    }

    fun mapFinancialYearDataToDomain(dto: FinancialYearDataDto): FinancialYearData {
        return FinancialYearData(
            fiscalYear = dto.fiscalYear ?: "",
            revenueCr = dto.revenueCr ?: 0.0,
            ebitdaCr = dto.ebitdaCr ?: 0.0,
            patCr = dto.patCr ?: 0.0,
            eps = dto.eps ?: 0.0,
            netWorthCr = dto.netWorthCr ?: 0.0,
            totalAssetsCr = dto.totalAssetsCr ?: 0.0,
            totalDebtCr = dto.totalDebtCr ?: 0.0,
            roePercent = dto.roePercent ?: 0.0,
            rocePercent = dto.rocePercent ?: 0.0,
            debtToEquity = dto.debtToEquity ?: 0.0,
            peRatio = dto.peRatio ?: 0.0
        )
    }

    fun mapImportantDateItemToDomain(dto: ImportantDateItemDto): ImportantDateItem {
        val status = when (dto.status?.uppercase()) {
            "COMPLETED" -> DateStatus.COMPLETED
            "ACTIVE" -> DateStatus.ACTIVE
            else -> DateStatus.UPCOMING
        }
        return ImportantDateItem(
            title = dto.title ?: "",
            dateStr = dto.dateStr ?: "",
            status = status,
            note = dto.note ?: ""
        )
    }

    fun mapAllotmentInfoToDomain(dto: AllotmentInfoDto): AllotmentInfo {
        return AllotmentInfo(
            registrarName = dto.registrarName ?: "Registrar",
            registrarUrl = dto.registrarUrl ?: "https://ris.kfintech.com/",
            bseUrl = dto.bseUrl ?: "https://www.bseindia.com/investors/appli_check.aspx",
            nseUrl = dto.nseUrl ?: "https://www.nseindia.com/products/content/equities/ipos/ipo_login.htm",
            allotmentDate = dto.allotmentDate ?: "",
            isAvailable = dto.isAvailable ?: false,
            note = dto.note ?: "Allotment status is published directly by the registrar."
        )
    }

    fun mapAnalysisReportToDomain(dto: AnalysisReportDto): AnalysisReport {
        return AnalysisReport(
            businessSummary = dto.businessSummary ?: "",
            financialSnapshot = dto.financialSnapshot ?: "",
            valuationSnapshot = dto.valuationSnapshot ?: "",
            keyPositives = dto.keyPositives ?: emptyList(),
            keyRisks = dto.keyRisks ?: emptyList(),
            importantChecks = dto.importantChecks ?: emptyList(),
            isAiGenerated = dto.isAiGenerated ?: false,
            generatedDate = dto.generatedDate ?: "",
            disclaimer = dto.disclaimer ?: "Educational purposes only."
        )
    }

    private fun parseCategory(catStr: String?): IpoCategory {
        return when (catStr?.trim()?.uppercase()) {
            "SME", "SME_IPO", "SME IPO" -> IpoCategory.SME
            else -> IpoCategory.MAINBOARD
        }
    }

    private fun parseStatus(statusStr: String?): IpoStatus {
        return when (statusStr?.trim()?.uppercase()) {
            "OPEN", "LIVE", "ACTIVE" -> IpoStatus.OPEN
            "UPCOMING", "PLANNED", "ANNOUNCED" -> IpoStatus.UPCOMING
            "CLOSED", "ALLOTMENT", "BIDDING_CLOSED" -> IpoStatus.CLOSED
            "LISTED", "TRADING" -> IpoStatus.LISTED
            else -> IpoStatus.OPEN
        }
    }
}
