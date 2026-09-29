package com.example.ipotracker.data.remote

import com.example.ipotracker.data.model.*

object MockIpoDataSource {

    val marketIndices = listOf(
        MarketIndex("NIFTY 50", "25,790.95", "+112.40", 0.44, true),
        MarketIndex("SENSEX", "84,299.90", "+356.10", 0.42, true),
        MarketIndex("BANK NIFTY", "53,820.50", "-88.30", -0.16, false),
        MarketIndex("IPO MARKET", "18,945.30", "+184.20", 0.98, true)
    )

    val ipoList: List<IpoItem> = listOf(
        // 1. OPEN MAINBOARD IPO
        IpoItem(
            id = "ipo-001",
            name = "Nexus Solar Technologies Ltd",
            symbol = "SOLARTECH",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.OPEN,
            priceBandMin = 485.0,
            priceBandMax = 510.0,
            lotSize = 29,
            minInvestment = 14790.0,
            issueSizeCr = 1850.0,
            freshIssueCr = 1350.0,
            ofsCr = 500.0,
            openDate = "2026-09-24",
            closeDate = "2026-09-28",
            allotmentDate = "2026-09-29",
            listingDate = "2026-10-03",
            currentGmp = 125.0,
            estimatedListingPrice = 635.0,
            estimatedGainPercent = 24.51,
            lastGmpUpdated = "26-Sep-2026 10:30 AM",
            currentSubscriptionTimes = 6.39,
            qibTimes = 8.24,
            niiTimes = 5.42,
            retailTimes = 4.18,
            description = "Nexus Solar Technologies is an integrated manufacturer of high-efficiency N-type TOPCon solar photovoltaic cells and modules, offering turnkey solar EPC solutions across commercial, industrial and utility-scale installations in India and overseas.",
            sector = "Renewable Energy & Equipment",
            listingExchanges = "BSE, NSE",
            faceValue = 10.0,
            leadManagers = "Kotak Mahindra Capital, Axis Capital, ICICI Securities",
            registrar = "KFin Technologies Ltd",
            promoterHoldingPre = 78.4,
            promoterHoldingPost = 58.2,
            objectsOfIssue = listOf(
                "Funding capital expenditure for expanding module manufacturing capacity by 2.4 GW in Gujarat.",
                "Investment in wholly-owned subsidiary for repayment of certain outstanding borrowings.",
                "Funding working capital requirements of the company.",
                "General corporate purposes."
            ),
            subscriptionDetails = SubscriptionDetails(
                overallTimes = 6.39,
                qibTimes = 8.24,
                niiTimes = 5.42,
                retailTimes = 4.18,
                employeeTimes = 2.15,
                otherTimes = 1.80,
                categoryRows = listOf(
                    SubscriptionRow("Qualified Institutional (QIB)", 5580000, 45979200, 8.24),
                    SubscriptionRow("Non-Institutional (NII / HNI)", 4185000, 22682700, 5.42),
                    SubscriptionRow("  - bNII (> ₹10 Lakh)", 2790000, 16182000, 5.80),
                    SubscriptionRow("  - sNII (₹2L - ₹10L)", 1395000, 6500700, 4.66),
                    SubscriptionRow("Retail Individual (RII)", 9765000, 40817700, 4.18),
                    SubscriptionRow("Employee Reservation", 350000, 752500, 2.15),
                    SubscriptionRow("Other (Shareholders / Anchor)", 200000, 360000, 1.80),
                    SubscriptionRow("Total Issue", 20080000, 110592100, 6.39)
                ),
                dayProgress = listOf(
                    SubscriptionDayProgress("Day 1", "24-Sep-2026", 1.85, 0.45, 2.10, 2.80, 1.05, 0.90),
                    SubscriptionDayProgress("Day 2", "25-Sep-2026", 4.12, 3.20, 4.35, 4.05, 1.70, 1.45),
                    SubscriptionDayProgress("Day 3", "26-Sep-2026", 6.39, 8.24, 5.42, 4.18, 2.15, 1.80)
                ),
                lastUpdated = "26-Sep-2026 17:00 IST (Official BSE/NSE EOD Bidding Report)"
            ),
            gmpHistory = listOf(
                GmpHistoryItem("21-Sep-2026", 80.0, 590.0, 15.69, GmpTrend.POSITIVE),
                GmpHistoryItem("22-Sep-2026", 95.0, 605.0, 18.63, GmpTrend.POSITIVE),
                GmpHistoryItem("23-Sep-2026", 110.0, 620.0, 21.57, GmpTrend.POSITIVE),
                GmpHistoryItem("24-Sep-2026", 120.0, 630.0, 23.53, GmpTrend.POSITIVE),
                GmpHistoryItem("25-Sep-2026", 125.0, 635.0, 24.51, GmpTrend.POSITIVE),
                GmpHistoryItem("26-Sep-2026", 125.0, 635.0, 24.51, GmpTrend.POSITIVE)
            ),
            financials = listOf(
                FinancialYearData("FY2023", 942.5, 118.2, 62.4, 8.40, 412.0, 890.0, 280.0, 15.1, 17.8, 0.68, 60.7),
                FinancialYearData("FY2024", 1385.0, 194.5, 112.8, 14.80, 595.0, 1240.0, 310.0, 19.0, 21.4, 0.52, 34.5),
                FinancialYearData("FY2025", 2120.4, 328.0, 198.5, 24.10, 880.0, 1820.0, 290.0, 22.6, 25.1, 0.33, 21.2),
                FinancialYearData("FY2026E", 2850.0, 470.0, 285.0, 32.50, 1250.0, 2400.0, 210.0, 23.5, 27.2, 0.17, 15.7)
            ),
            importantDates = listOf(
                ImportantDateItem("DRHP Filed", "15-May-2026", DateStatus.COMPLETED, "Approved by SEBI"),
                ImportantDateItem("RHP Filed", "18-Sep-2026", DateStatus.COMPLETED, "Price band announced"),
                ImportantDateItem("IPO Opens", "24-Sep-2026", DateStatus.COMPLETED, "10:00 AM IST"),
                ImportantDateItem("IPO Closes", "28-Sep-2026", DateStatus.ACTIVE, "5:00 PM IST"),
                ImportantDateItem("Basis of Allotment", "29-Sep-2026", DateStatus.UPCOMING, "KFin Technologies"),
                ImportantDateItem("Refund Initiation", "30-Sep-2026", DateStatus.UPCOMING, "Unblocking UPI mandates"),
                ImportantDateItem("Demat Credit", "01-Oct-2026", DateStatus.UPCOMING, "Shares in Demat Account"),
                ImportantDateItem("Listing Date", "03-Oct-2026", DateStatus.UPCOMING, "BSE & NSE")
            ),
            allotmentInfo = AllotmentInfo(
                registrarName = "KFin Technologies Ltd",
                registrarUrl = "https://ris.kfintech.com/ipostatus/",
                allotmentDate = "29-Sep-2026",
                isAvailable = false,
                note = "Allotment will be finalized on 29-Sep-2026. Direct verification will be available on the registrar portal."
            ),
            analysisReport = AnalysisReport(
                businessSummary = "Nexus Solar is a rapidly scaling manufacturer of high-efficiency solar modules with integrated cell lines. The company commands an 8.5% domestic market share in commercial rooftop and utility solar modules.",
                financialSnapshot = "Revenue CAGR of 50.2% over FY23-FY25. Operating margins expanded from 12.5% to 15.5%. Debt-to-Equity reduced from 0.68 to 0.33, demonstrating robust internal cash accruals.",
                valuationSnapshot = "At the upper price band of ₹510, the issue is valued at a P/E multiple of 21.2x (based on FY25 earnings), which compares favorably to industry peers trading at 28x-35x.",
                keyPositives = listOf(
                    "Strong order book of ₹3,400 Cr providing revenue visibility for the next 18 months.",
                    "Backward integration into solar cell manufacturing enhances margins and reduces import dependence.",
                    "Government PLI scheme beneficiary with captive domestic manufacturing advantages."
                ),
                keyRisks = listOf(
                    "Volatile raw material prices (polysilicon and solar wafers) can squeeze margins.",
                    "High customer concentration: top 5 clients account for 44% of total sales.",
                    "Policy and regulatory changes regarding solar subsidies and tariffs."
                ),
                importantChecks = listOf(
                    "Capacity utilization of newly commissioned 2.4 GW facility in Gujarat.",
                    "Trend in international solar module spot prices and domestic anti-dumping duties.",
                    "Execution timeline of utility projects under the current order backlog."
                ),
                isAiGenerated = true,
                generatedDate = "26-Sep-2026"
            )
        ),

        // 2. OPEN MAINBOARD IPO
        IpoItem(
            id = "ipo-002",
            name = "Aura Health Logistics Ltd",
            symbol = "AURAHLTH",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.OPEN,
            priceBandMin = 270.0,
            priceBandMax = 285.0,
            lotSize = 52,
            minInvestment = 14820.0,
            issueSizeCr = 720.0,
            freshIssueCr = 520.0,
            ofsCr = 200.0,
            openDate = "2026-09-25",
            closeDate = "2026-09-29",
            allotmentDate = "2026-09-30",
            listingDate = "2026-10-05",
            currentGmp = 42.0,
            estimatedListingPrice = 327.0,
            estimatedGainPercent = 14.74,
            lastGmpUpdated = "26-Sep-2026 11:15 AM",
            currentSubscriptionTimes = 3.12,
            qibTimes = 2.45,
            niiTimes = 4.10,
            retailTimes = 3.65,
            description = "Aura Health Logistics is a specialized healthcare supply-chain player providing temperature-controlled storage and express distribution for biologics, vaccines, diagnostics, and clinical trial samples across 450+ Indian cities.",
            sector = "Logistics & Healthcare Services",
            listingExchanges = "BSE, NSE",
            faceValue = 5.0,
            leadManagers = "JM Financial, SBI Capital Markets",
            registrar = "Link Intime India Pvt Ltd",
            promoterHoldingPre = 72.1,
            promoterHoldingPost = 54.0,
            objectsOfIssue = listOf(
                "Setting up 6 multi-temperature automated pharmaceutical distribution hubs.",
                "Procurement of 180 refrigerated electric and hybrid transport vehicles.",
                "General corporate purposes."
            ),
            subscriptionDetails = SubscriptionDetails(
                overallTimes = 3.12,
                qibTimes = 2.45,
                niiTimes = 4.10,
                retailTimes = 3.65,
                employeeTimes = 1.40,
                otherTimes = 1.10,
                categoryRows = listOf(
                    SubscriptionRow("Qualified Institutional (QIB)", 2800000, 6860000, 2.45),
                    SubscriptionRow("Non-Institutional (NII / HNI)", 2100000, 8610000, 4.10),
                    SubscriptionRow("  - bNII (> ₹10 Lakh)", 1400000, 6020000, 4.30),
                    SubscriptionRow("  - sNII (₹2L - ₹10L)", 700000, 2590000, 3.70),
                    SubscriptionRow("Retail Individual (RII)", 4900000, 17885000, 3.65),
                    SubscriptionRow("Employee Reservation", 150000, 210000, 1.40),
                    SubscriptionRow("Other (Policyholders / Shareholders)", 100000, 110000, 1.10),
                    SubscriptionRow("Total Issue", 10050000, 31375000, 3.12)
                ),
                dayProgress = listOf(
                    SubscriptionDayProgress("Day 1", "25-Sep-2026", 1.42, 0.20, 1.85, 2.10, 0.65, 0.40),
                    SubscriptionDayProgress("Day 2", "26-Sep-2026", 3.12, 2.45, 4.10, 3.65, 1.40, 1.10)
                ),
                lastUpdated = "26-Sep-2026 17:00 IST (Day 2 Exchange Cumulative Bids)"
            ),
            gmpHistory = listOf(
                GmpHistoryItem("22-Sep-2026", 25.0, 310.0, 8.77, GmpTrend.POSITIVE),
                GmpHistoryItem("23-Sep-2026", 30.0, 315.0, 10.53, GmpTrend.POSITIVE),
                GmpHistoryItem("24-Sep-2026", 38.0, 323.0, 13.33, GmpTrend.POSITIVE),
                GmpHistoryItem("25-Sep-2026", 42.0, 327.0, 14.74, GmpTrend.POSITIVE),
                GmpHistoryItem("26-Sep-2026", 42.0, 327.0, 14.74, GmpTrend.POSITIVE)
            ),
            financials = listOf(
                FinancialYearData("FY2023", 420.0, 65.0, 32.0, 6.20, 180.0, 390.0, 110.0, 17.8, 19.5, 0.61, 46.0),
                FinancialYearData("FY2024", 580.0, 94.0, 51.0, 9.80, 245.0, 520.0, 125.0, 20.8, 22.1, 0.51, 29.1),
                FinancialYearData("FY2025", 810.0, 142.0, 82.0, 15.20, 360.0, 710.0, 130.0, 22.8, 24.6, 0.36, 18.8)
            ),
            importantDates = listOf(
                ImportantDateItem("DRHP Filed", "02-Jun-2026", DateStatus.COMPLETED),
                ImportantDateItem("RHP Filed", "19-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Opens", "25-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Closes", "29-Sep-2026", DateStatus.ACTIVE),
                ImportantDateItem("Basis of Allotment", "30-Sep-2026", DateStatus.UPCOMING),
                ImportantDateItem("Listing Date", "05-Oct-2026", DateStatus.UPCOMING)
            ),
            allotmentInfo = AllotmentInfo(
                registrarName = "Link Intime India Pvt Ltd",
                registrarUrl = "https://linkintime.co.in/initial_offer/public-issues.html",
                allotmentDate = "30-Sep-2026",
                isAvailable = false
            ),
            analysisReport = AnalysisReport(
                businessSummary = "Niche leader in cold-chain bio-pharma logistics with strict GxP compliance and IoT-monitored refrigerated fleets.",
                financialSnapshot = "Three-year revenue CAGR of 38.8% with operating EBITDA margins of 17.5%. Healthy ROE of 22.8%.",
                valuationSnapshot = "P/E of 18.8x based on FY25 EPS, reasonable relative to traditional logistics players due to premium bio-pharma margins.",
                keyPositives = listOf("High entry barriers due to strict regulatory certifications", "Sticky relationship with top 20 global pharmaceutical MNCs"),
                keyRisks = listOf("Capital intensive cold-chain infrastructure requirements", "Vulnerability to fuel and operational vehicle maintenance costs"),
                importantChecks = listOf("Expansion of active distribution hubs", "Volume growth in clinical trial logistics"),
                isAiGenerated = true,
                generatedDate = "26-Sep-2026"
            )
        ),

        // 3. OPEN SME IPO
        IpoItem(
            id = "ipo-003",
            name = "Finovate Micro Systems Ltd",
            symbol = "FINOMICRO",
            category = IpoCategory.SME,
            status = IpoStatus.OPEN,
            priceBandMin = 115.0,
            priceBandMax = 122.0,
            lotSize = 1000,
            minInvestment = 122000.0,
            issueSizeCr = 42.0,
            freshIssueCr = 42.0,
            ofsCr = 0.0,
            openDate = "2026-09-24",
            closeDate = "2026-09-28",
            allotmentDate = "2026-09-29",
            listingDate = "2026-10-03",
            currentGmp = 38.0,
            estimatedListingPrice = 160.0,
            estimatedGainPercent = 31.15,
            lastGmpUpdated = "26-Sep-2026 12:00 PM",
            currentSubscriptionTimes = 14.80,
            qibTimes = 11.20,
            niiTimes = 18.40,
            retailTimes = 16.50,
            description = "Finovate Micro Systems provides soundboxes, portable smart Android POS terminals, and cloud reconciliation software tailored for Tier-2 and Tier-3 rural micro-merchants.",
            sector = "Fintech & Electronic Equipment",
            listingExchanges = "NSE SME",
            faceValue = 10.0,
            leadManagers = "Hem Securities Ltd",
            registrar = "Bigshare Services Pvt Ltd",
            promoterHoldingPre = 85.0,
            promoterHoldingPost = 62.5,
            objectsOfIssue = listOf(
                "Purchase of inventory and hardware components for smart soundboxes.",
                "R&D expenditure for next-gen 5G UPI devices.",
                "General corporate expenses."
            ),
            subscriptionDetails = SubscriptionDetails(
                overallTimes = 14.80,
                qibTimes = 11.20,
                niiTimes = 18.40,
                retailTimes = 16.50,
                employeeTimes = 4.50,
                otherTimes = 3.20,
                categoryRows = listOf(
                    SubscriptionRow("Qualified Institutional (QIB)", 650000, 7280000, 11.20),
                    SubscriptionRow("Non-Institutional (NII / HNI)", 500000, 9200000, 18.40),
                    SubscriptionRow("Retail Individual (RII)", 1150000, 18975000, 16.50),
                    SubscriptionRow("Employee Reservation", 50000, 225000, 4.50),
                    SubscriptionRow("Other (Market Maker / Anchor)", 50000, 160000, 3.20),
                    SubscriptionRow("Total Issue", 2400000, 35840000, 14.80)
                ),
                dayProgress = listOf(
                    SubscriptionDayProgress("Day 1", "24-Sep-2026", 4.20, 2.10, 5.50, 5.20, 1.20, 0.80),
                    SubscriptionDayProgress("Day 2", "25-Sep-2026", 9.80, 6.80, 12.10, 11.40, 2.90, 1.80),
                    SubscriptionDayProgress("Day 3", "26-Sep-2026", 14.80, 11.20, 18.40, 16.50, 4.50, 3.20)
                ),
                lastUpdated = "26-Sep-2026 17:00 IST (Final Day 3 Close - BSE SME)"
            ),
            gmpHistory = listOf(
                GmpHistoryItem("22-Sep-2026", 20.0, 142.0, 16.39, GmpTrend.POSITIVE),
                GmpHistoryItem("24-Sep-2026", 28.0, 150.0, 22.95, GmpTrend.POSITIVE),
                GmpHistoryItem("26-Sep-2026", 38.0, 160.0, 31.15, GmpTrend.POSITIVE)
            ),
            financials = listOf(
                FinancialYearData("FY2023", 28.0, 4.2, 2.1, 3.80, 12.0, 24.0, 4.5, 17.5, 19.2, 0.38, 32.1),
                FinancialYearData("FY2024", 45.0, 7.8, 4.5, 7.20, 19.5, 38.0, 5.1, 23.1, 25.4, 0.26, 16.9),
                FinancialYearData("FY2025", 68.0, 13.5, 8.4, 11.80, 32.0, 55.0, 4.2, 26.3, 29.8, 0.13, 10.3)
            ),
            importantDates = listOf(
                ImportantDateItem("IPO Opens", "24-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Closes", "28-Sep-2026", DateStatus.ACTIVE),
                ImportantDateItem("Basis of Allotment", "29-Sep-2026", DateStatus.UPCOMING),
                ImportantDateItem("Listing Date", "03-Oct-2026", DateStatus.UPCOMING)
            ),
            allotmentInfo = AllotmentInfo(
                registrarName = "Bigshare Services Pvt Ltd",
                registrarUrl = "https://www.bigshareonline.com/ipo_Allotment.html",
                allotmentDate = "29-Sep-2026",
                isAvailable = false
            ),
            analysisReport = AnalysisReport(
                businessSummary = "High-growth SME fintech player riding the rapid surge of digital soundboxes and UPI merchant adoption.",
                financialSnapshot = "Revenue increased from ₹28 Cr in FY23 to ₹68 Cr in FY25. High ROE of 26.3% and virtually negligible debt.",
                valuationSnapshot = "P/E of 10.3x at upper band of ₹122, showing attractive valuation compared to listed peers.",
                keyPositives = listOf("100% fresh issue proceeds directed towards manufacturing scale", "Low debt with high return ratios"),
                keyRisks = listOf("SME illiquidity risk and large lot size (₹1.22 Lakh min application)", "Rapid technological obsolescence of payment hardware"),
                importantChecks = listOf("Retention rate of active monthly merchants", "Component sourcing dependencies"),
                isAiGenerated = true,
                generatedDate = "26-Sep-2026"
            )
        ),

        // 4. UPCOMING MAINBOARD IPO
        IpoItem(
            id = "ipo-004",
            name = "Bharat EV Components Ltd",
            symbol = "BHARATEV",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.UPCOMING,
            priceBandMin = 620.0,
            priceBandMax = 650.0,
            lotSize = 23,
            minInvestment = 14950.0,
            issueSizeCr = 2400.0,
            freshIssueCr = 1800.0,
            ofsCr = 600.0,
            openDate = "2026-10-06",
            closeDate = "2026-10-09",
            allotmentDate = "2026-10-12",
            listingDate = "2026-10-15",
            currentGmp = 180.0,
            estimatedListingPrice = 830.0,
            estimatedGainPercent = 27.69,
            lastGmpUpdated = "26-Sep-2026 09:45 AM",
            currentSubscriptionTimes = 0.0,
            qibTimes = 0.0,
            niiTimes = 0.0,
            retailTimes = 0.0,
            description = "Bharat EV Components is a tier-1 auto electrical equipment supplier manufacturing high-voltage wiring harnesses, BMS controllers, and permanent magnet traction motors for 2W and 3W electric vehicles.",
            sector = "Auto Ancillary - EV Components",
            listingExchanges = "BSE, NSE",
            faceValue = 2.0,
            leadManagers = "Morgan Stanley India, IIFL Securities, Jefferies",
            registrar = "Link Intime India Pvt Ltd",
            promoterHoldingPre = 75.0,
            promoterHoldingPost = 56.4,
            objectsOfIssue = listOf(
                "Establishing a new automated gigafactory in Hosur, Tamil Nadu.",
                "Repayment of borrowings availed by the company.",
                "R&D center for next-gen silicon carbide inverters."
            ),
            gmpHistory = listOf(
                GmpHistoryItem("24-Sep-2026", 140.0, 790.0, 21.54, GmpTrend.POSITIVE),
                GmpHistoryItem("25-Sep-2026", 165.0, 815.0, 25.38, GmpTrend.POSITIVE),
                GmpHistoryItem("26-Sep-2026", 180.0, 830.0, 27.69, GmpTrend.POSITIVE)
            ),
            financials = listOf(
                FinancialYearData("FY2023", 1120.0, 145.0, 78.0, 12.40, 480.0, 920.0, 220.0, 16.3, 18.9, 0.46, 52.4),
                FinancialYearData("FY2024", 1750.0, 255.0, 142.0, 21.80, 680.0, 1340.0, 240.0, 20.9, 23.4, 0.35, 29.8),
                FinancialYearData("FY2025", 2640.0, 420.0, 254.0, 36.20, 1050.0, 1980.0, 180.0, 24.2, 27.8, 0.17, 17.9)
            ),
            importantDates = listOf(
                ImportantDateItem("DRHP Filed", "10-Apr-2026", DateStatus.COMPLETED),
                ImportantDateItem("RHP Filed", "22-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Opens", "2026-10-06", DateStatus.UPCOMING),
                ImportantDateItem("IPO Closes", "2026-10-09", DateStatus.UPCOMING),
                ImportantDateItem("Listing Date", "2026-10-15", DateStatus.UPCOMING)
            ),
            allotmentInfo = AllotmentInfo(
                registrarName = "Link Intime India Pvt Ltd",
                registrarUrl = "https://linkintime.co.in/initial_offer/public-issues.html",
                allotmentDate = "2026-10-12",
                isAvailable = false
            ),
            analysisReport = AnalysisReport(
                businessSummary = "Pure-play electric vehicle ancillary powerhouse benefiting from surging EV penetration in India.",
                financialSnapshot = "Tremendous revenue growth (42% CAGR) with solid expansion in EBITDA margins.",
                valuationSnapshot = "Offered at 17.9x FY25 EPS, an attractive valuation compared to listed peers at 32x.",
                keyPositives = listOf("Exclusive supply contracts with leading OEM manufacturers", "High proportion of fresh issue to fund Hosur facility"),
                keyRisks = listOf("Slowdown in overall EV adoption or changes in government FAME incentives", "Raw material price volatility in copper and rare-earth magnets"),
                importantChecks = listOf("Progress of Hosur factory construction", "Order pipeline from 4-wheeler OEMs"),
                isAiGenerated = true,
                generatedDate = "26-Sep-2026"
            )
        ),

        // 5. UPCOMING MAINBOARD IPO
        IpoItem(
            id = "ipo-005",
            name = "CyberShield InfoSec Ltd",
            symbol = "CYBERSHLD",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.UPCOMING,
            priceBandMin = 340.0,
            priceBandMax = 360.0,
            lotSize = 41,
            minInvestment = 14760.0,
            issueSizeCr = 980.0,
            freshIssueCr = 700.0,
            ofsCr = 280.0,
            openDate = "2026-10-14",
            closeDate = "2026-10-16",
            allotmentDate = "2026-10-19",
            listingDate = "2026-10-22",
            currentGmp = 55.0,
            estimatedListingPrice = 415.0,
            estimatedGainPercent = 15.28,
            lastGmpUpdated = "26-Sep-2026 10:00 AM",
            currentSubscriptionTimes = 0.0,
            qibTimes = 0.0,
            niiTimes = 0.0,
            retailTimes = 0.0,
            description = "CyberShield InfoSec provides enterprise managed cybersecurity, zero-trust cloud security, and AI threat hunting solutions across India, the Middle East, and Southeast Asia.",
            sector = "Information Technology - Cybersecurity",
            listingExchanges = "BSE, NSE",
            faceValue = 5.0,
            leadManagers = "Avendus Capital, Edelweiss Financial",
            registrar = "KFin Technologies Ltd",
            promoterHoldingPre = 69.5,
            promoterHoldingPost = 51.2,
            objectsOfIssue = listOf(
                "Establishing Security Operations Centers (SOC) in Dubai and Singapore.",
                "R&D into Generative AI cyber incident triage.",
                "Strategic cloud technology acquisitions."
            ),
            gmpHistory = listOf(
                GmpHistoryItem("25-Sep-2026", 45.0, 405.0, 12.50, GmpTrend.POSITIVE),
                GmpHistoryItem("26-Sep-2026", 55.0, 415.0, 15.28, GmpTrend.POSITIVE)
            ),
            financials = listOf(
                FinancialYearData("FY2023", 260.0, 52.0, 31.0, 7.80, 140.0, 240.0, 15.0, 22.1, 26.4, 0.11, 46.1),
                FinancialYearData("FY2024", 390.0, 84.0, 54.0, 13.20, 210.0, 330.0, 12.0, 25.7, 30.1, 0.06, 27.3),
                FinancialYearData("FY2025", 560.0, 132.0, 88.0, 20.40, 320.0, 470.0, 8.0, 27.5, 33.2, 0.03, 17.6)
            ),
            importantDates = listOf(
                ImportantDateItem("IPO Opens", "2026-10-14", DateStatus.UPCOMING),
                ImportantDateItem("IPO Closes", "2026-10-16", DateStatus.UPCOMING),
                ImportantDateItem("Listing Date", "2026-10-22", DateStatus.UPCOMING)
            ),
            allotmentInfo = AllotmentInfo(
                registrarName = "KFin Technologies Ltd",
                registrarUrl = "https://ris.kfintech.com/ipostatus/",
                allotmentDate = "2026-10-19"
            )
        ),

        // 6. CLOSED IPO - ALLOTMENT AVAILABLE (Not yet listed)
        IpoItem(
            id = "ipo-006",
            name = "Prime Infra Logistics Ltd",
            symbol = "PRIMEINFRA",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.ALLOTMENT_AVAILABLE,
            allotmentStatus = AllotmentStatus.AVAILABLE,
            listingStatus = ListingStatus.NOT_LISTED,
            priceBandMin = 310.0,
            priceBandMax = 325.0,
            lotSize = 46,
            minInvestment = 14950.0,
            issueSizeCr = 1200.0,
            freshIssueCr = 800.0,
            ofsCr = 400.0,
            openDate = "2026-09-20",
            closeDate = "2026-09-23",
            allotmentDate = "2026-09-26",
            listingDate = "2026-09-30",
            currentGmp = 92.0,
            estimatedListingPrice = 417.0,
            estimatedGainPercent = 28.31,
            lastGmpUpdated = "26-Sep-2026 12:45 PM",
            currentSubscriptionTimes = 28.40,
            qibTimes = 44.20,
            niiTimes = 32.10,
            retailTimes = 14.60,
            description = "Prime Infra Logistics operates multi-modal logistics parks, inland container depots (ICDs), and container rail terminals along India's Dedicated Freight Corridors.",
            sector = "Infrastructure & Logistics",
            listingExchanges = "BSE, NSE",
            faceValue = 10.0,
            leadManagers = "ICICI Securities, BNP Paribas",
            registrar = "Link Intime India Pvt Ltd",
            promoterHoldingPre = 74.0,
            promoterHoldingPost = 55.0,
            subscriptionDetails = SubscriptionDetails(
                overallTimes = 28.40,
                qibTimes = 44.20,
                niiTimes = 32.10,
                retailTimes = 14.60,
                employeeTimes = 5.80,
                otherTimes = 4.10,
                categoryRows = listOf(
                    SubscriptionRow("Qualified Institutional (QIB)", 5000000, 221000000, 44.20),
                    SubscriptionRow("Non-Institutional (NII / HNI)", 3750000, 120375000, 32.10),
                    SubscriptionRow("  - bNII (> ₹10 Lakh)", 2500000, 88250000, 35.30),
                    SubscriptionRow("  - sNII (₹2L - ₹10L)", 1250000, 32125000, 25.70),
                    SubscriptionRow("Retail Individual (RII)", 8750000, 127750000, 14.60),
                    SubscriptionRow("Employee Reservation", 250000, 1450000, 5.80),
                    SubscriptionRow("Other (Shareholders)", 250000, 1025000, 4.10),
                    SubscriptionRow("Total Issue", 18000000, 471600000, 28.40)
                ),
                dayProgress = listOf(
                    SubscriptionDayProgress("Day 1", "20-Sep-2026", 3.80, 1.10, 4.20, 5.40, 1.40, 1.00),
                    SubscriptionDayProgress("Day 2", "21-Sep-2026", 11.50, 8.40, 14.80, 10.20, 3.20, 2.30),
                    SubscriptionDayProgress("Day 3", "23-Sep-2026", 28.40, 44.20, 32.10, 14.60, 5.80, 4.10)
                ),
                lastUpdated = "23-Sep-2026 17:00 IST (Final Issue Close - BSE/NSE)"
            ),
            gmpHistory = listOf(
                GmpHistoryItem("20-Sep-2026", 55.0, 380.0, 16.92, GmpTrend.POSITIVE),
                GmpHistoryItem("22-Sep-2026", 74.0, 399.0, 22.77, GmpTrend.POSITIVE),
                GmpHistoryItem("24-Sep-2026", 88.0, 413.0, 27.08, GmpTrend.POSITIVE),
                GmpHistoryItem("26-Sep-2026", 92.0, 417.0, 28.31, GmpTrend.POSITIVE)
            ),
            importantDates = listOf(
                ImportantDateItem("IPO Opens", "20-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Closes", "23-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("Basis of Allotment", "26-Sep-2026", DateStatus.ACTIVE, "Allotment declared"),
                ImportantDateItem("Listing Date", "30-Sep-2026", DateStatus.UPCOMING)
            ),
            allotmentInfo = AllotmentInfo(
                registrarName = "Link Intime India Pvt Ltd",
                registrarUrl = "https://linkintime.co.in/initial_offer/public-issues.html",
                allotmentDate = "26-Sep-2026",
                isAvailable = true,
                note = "Allotment status has been declared today. Check directly on the Link Intime portal."
            )
        ),

        // 7. CLOSED IPO - ALLOTMENT PENDING (Bidding Closed, Basis in formulation)
        IpoItem(
            id = "ipo-009",
            name = "NxtGen Renewable Fuels Ltd",
            symbol = "NXTGENFUEL",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.ALLOTMENT_PENDING,
            allotmentStatus = AllotmentStatus.PENDING,
            listingStatus = ListingStatus.NOT_LISTED,
            priceBandMin = 142.0,
            priceBandMax = 150.0,
            lotSize = 100,
            minInvestment = 15000.0,
            issueSizeCr = 650.0,
            freshIssueCr = 500.0,
            ofsCr = 150.0,
            openDate = "2026-09-22",
            closeDate = "2026-09-25",
            allotmentDate = "2026-09-29",
            listingDate = "2026-10-04",
            currentGmp = 44.0,
            estimatedListingPrice = 194.0,
            estimatedGainPercent = 29.33,
            lastGmpUpdated = "27-Sep-2026 10:15 AM",
            currentSubscriptionTimes = 19.80,
            qibTimes = 32.40,
            niiTimes = 21.60,
            retailTimes = 12.50,
            description = "NxtGen Renewable Fuels is a biofuels manufacturer converting agricultural and municipal waste into 2G ethanol and compressed bio-gas (CBG).",
            sector = "Renewable Fuels & Clean Energy",
            listingExchanges = "BSE, NSE",
            faceValue = 10.0,
            leadManagers = "SBI Capital Markets, JM Financial",
            registrar = "Bigshare Services Pvt Ltd",
            promoterHoldingPre = 68.5,
            promoterHoldingPost = 51.2,
            importantDates = listOf(
                ImportantDateItem("IPO Opens", "22-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Closes", "25-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("Basis of Allotment", "29-Sep-2026", DateStatus.UPCOMING, "Allotment pending"),
                ImportantDateItem("Listing Date", "04-Oct-2026", DateStatus.UPCOMING)
            ),
            allotmentInfo = AllotmentInfo(
                registrarName = "Bigshare Services Pvt Ltd",
                registrarUrl = "https://www.bigshareonline.com/ipo_Allotment.html",
                allotmentDate = "29-Sep-2026",
                isAvailable = false,
                note = "Allotment status is currently pending and under finalization by registrar Bigshare Services."
            )
        ),

        // 8. RECENTLY LISTED IPO
        IpoItem(
            id = "ipo-007",
            name = "Apex Clean Energy Solutions Ltd",
            symbol = "APEXCLEAN",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.LISTED,
            allotmentStatus = AllotmentStatus.AVAILABLE,
            listingStatus = ListingStatus.LISTED,
            priceBandMin = 195.0,
            priceBandMax = 210.0,
            lotSize = 71,
            minInvestment = 14910.0,
            issueSizeCr = 1400.0,
            freshIssueCr = 1100.0,
            ofsCr = 300.0,
            openDate = "2026-09-08",
            closeDate = "2026-09-11",
            allotmentDate = "2026-09-12",
            listingDate = "2026-09-17",
            currentGmp = 0.0,
            estimatedListingPrice = 285.0,
            estimatedGainPercent = 35.71,
            lastGmpUpdated = "Listed on 17-Sep-2026",
            currentSubscriptionTimes = 34.20,
            qibTimes = 52.10,
            niiTimes = 38.40,
            retailTimes = 18.20,
            listingPrice = 285.0,
            listingGainPercent = 35.71,
            currentMarketPrice = 312.40,
            currentReturnPercent = 48.76,
            liveMarketData = LiveMarketData(
                companyName = "Apex Clean Energy Solutions Ltd",
                symbol = "APEXCLEAN",
                listingPrice = 285.0,
                currentPrice = 312.40,
                change = 27.40,
                changePercent = 9.61,
                volume = 14250890L,
                week52High = 328.0,
                week52Low = 272.50,
                issuePrice = 210.0,
                listingGainLoss = 75.0,
                listingGainLossPercent = 35.71,
                isLive = true,
                lastUpdated = "BSE / NSE Real-time"
            ),
            description = "Apex Clean Energy designs, builds and operates commercial biomass gasification and green hydrogen generation plants across India.",
            sector = "Green Energy & Clean Tech",
            listingExchanges = "BSE, NSE",
            faceValue = 10.0,
            leadManagers = "Axis Capital, Nomura India",
            registrar = "KFin Technologies Ltd",
            promoterHoldingPre = 71.0,
            promoterHoldingPost = 53.8,
            importantDates = listOf(
                ImportantDateItem("IPO Opens", "08-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Closes", "11-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("Listing Date", "17-Sep-2026", DateStatus.COMPLETED, "Listed at ₹285 (+35.7%)")
            )
        ),

        // 9. RECENTLY LISTED IPO
        IpoItem(
            id = "ipo-008",
            name = "Quantum Robotics Automation Ltd",
            symbol = "QUANTROBO",
            category = IpoCategory.MAINBOARD,
            status = IpoStatus.LISTED,
            allotmentStatus = AllotmentStatus.AVAILABLE,
            listingStatus = ListingStatus.LISTED,
            priceBandMin = 515.0,
            priceBandMax = 540.0,
            lotSize = 27,
            minInvestment = 14580.0,
            issueSizeCr = 1650.0,
            freshIssueCr = 1250.0,
            ofsCr = 400.0,
            openDate = "2026-09-02",
            closeDate = "2026-09-05",
            allotmentDate = "2026-09-08",
            listingDate = "2026-09-12",
            currentGmp = 0.0,
            estimatedListingPrice = 710.0,
            estimatedGainPercent = 31.48,
            lastGmpUpdated = "Listed on 12-Sep-2026",
            currentSubscriptionTimes = 41.50,
            qibTimes = 68.30,
            niiTimes = 45.20,
            retailTimes = 19.80,
            listingPrice = 710.0,
            listingGainPercent = 31.48,
            currentMarketPrice = 685.20,
            currentReturnPercent = 26.89,
            liveMarketData = LiveMarketData(
                companyName = "Quantum Robotics Automation Ltd",
                symbol = "QUANTROBO",
                listingPrice = 710.0,
                currentPrice = 685.20,
                change = -24.80,
                changePercent = -3.49,
                volume = 8421000L,
                week52High = 735.0,
                week52Low = 660.0,
                issuePrice = 540.0,
                listingGainLoss = 170.0,
                listingGainLossPercent = 31.48,
                isLive = true,
                lastUpdated = "BSE / NSE Real-time"
            ),
            description = "Quantum Robotics is an industrial automation solution provider building autonomous mobile robots (AMRs) and robotic arms for pharmaceutical and automotive manufacturing lines.",
            sector = "Industrial Automation & Robotics",
            listingExchanges = "BSE, NSE",
            faceValue = 5.0,
            leadManagers = "Kotak Mahindra Capital, Citigroup",
            registrar = "Link Intime India Pvt Ltd",
            importantDates = listOf(
                ImportantDateItem("IPO Opens", "02-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("IPO Closes", "05-Sep-2026", DateStatus.COMPLETED),
                ImportantDateItem("Listing Date", "12-Sep-2026", DateStatus.COMPLETED, "Listed at ₹710 (+31.5%)")
            )
        )
    )
}
