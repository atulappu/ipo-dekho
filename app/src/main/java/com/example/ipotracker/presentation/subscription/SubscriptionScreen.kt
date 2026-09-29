package com.example.ipotracker.presentation.subscription

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.presentation.components.CategorySubscriptionBarChart
import com.example.ipotracker.presentation.components.DayWiseProgressionTrendChart
import com.example.ipotracker.presentation.components.DayWiseSubscriptionTableView
import com.example.ipotracker.presentation.components.SubscriptionTableView
import com.example.ipotracker.presentation.components.shareIpo
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ipotracker.utils.DateUtils
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "IPO Subscription",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "BSE & NSE Cumulative Bids",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    uiState.selectedIpo?.let { ipo ->
                        IconButton(
                            onClick = { shareIpo(context, ipo) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.ipos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else if (uiState.ipos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active IPO subscription data available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray
                )
            }
        } else {
            val selectedIpo = uiState.selectedIpo ?: uiState.filteredIpos.firstOrNull() ?: uiState.ipos.first()
            val subs = selectedIpo.subscriptionDetails

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Transparent Official Exchange Notice (No fake live labels)
                item {
                    OfficialExchangeNoticeBanner(lastUpdated = subs?.lastUpdated ?: "")
                }

                // 2. Category Filter & IPO Selector Carousel
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Category Filters: All, Mainboard, SME
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterPill(
                                title = "All (${uiState.ipos.size})",
                                isSelected = uiState.categoryFilter == null,
                                onClick = { viewModel.setCategoryFilter(null) }
                            )
                            FilterPill(
                                title = "Mainboard (${uiState.ipos.count { it.category == IpoCategory.MAINBOARD }})",
                                isSelected = uiState.categoryFilter == IpoCategory.MAINBOARD,
                                onClick = { viewModel.setCategoryFilter(IpoCategory.MAINBOARD) }
                            )
                            FilterPill(
                                title = "SME (${uiState.ipos.count { it.category == IpoCategory.SME }})",
                                isSelected = uiState.categoryFilter == IpoCategory.SME,
                                onClick = { viewModel.setCategoryFilter(IpoCategory.SME) }
                            )
                        }

                        // Horizontal Scrollable IPO Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.filteredIpos) { ipo ->
                                val isSelected = ipo.id == selectedIpo.id
                                val isMainboard = ipo.category == IpoCategory.MAINBOARD

                                Surface(
                                    color = if (isSelected) {
                                        if (isMainboard) SecondaryBlueLight else PrimaryOrangeLight
                                    } else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) {
                                            if (isMainboard) SecondaryBlue else PrimaryOrange
                                        } else BorderLight
                                    ),
                                    modifier = Modifier.clickable { viewModel.selectIpo(ipo.id) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = ipo.name,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) {
                                                    if (isMainboard) SecondaryBlueDark else PrimaryOrangeDark
                                                } else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = if (isMainboard) "Mainboard" else "SME",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "•",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                    color = NeutralGray
                                                )
                                                Text(
                                                    text = "${"%.2f".format(ipo.currentSubscriptionTimes)}x",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (ipo.currentSubscriptionTimes >= 1.0) MarketGreen else NeutralGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Selected IPO Hero Overview Card
                item {
                    SelectedIpoHeroCard(
                        ipo = selectedIpo,
                        onViewDetail = { onNavigateToDetail(selectedIpo.id) }
                    )
                }

                // 4. Key Category Multipliers Strip (Overall, QIB, NII/HNI, Retail, Employee, Other)
                item {
                    CategoryMultipliersStrip(subs = subs)
                }

                // 5. View Switcher Tabs: Table & Breakdown | Day 1-3 Trends | Category Multiples
                item {
                    TabRow(
                        selectedTabIndex = uiState.selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = PrimaryOrange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = uiState.selectedTab == 0,
                            onClick = { viewModel.setSelectedTab(0) },
                            text = {
                                Text(
                                    text = "Tables",
                                    fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        )
                        Tab(
                            selected = uiState.selectedTab == 1,
                            onClick = { viewModel.setSelectedTab(1) },
                            text = {
                                Text(
                                    text = "Day 1-3 Trend",
                                    fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        )
                        Tab(
                            selected = uiState.selectedTab == 2,
                            onClick = { viewModel.setSelectedTab(2) },
                            text = {
                                Text(
                                    text = "Charts",
                                    fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                // 6. Tab Content Display
                when (uiState.selectedTab) {
                    0 -> {
                        // Section A: Main Category-wise Subscription Table
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Category-Wise Subscription",
                                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "BSE & NSE Cumulative Bids",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Toggle for compact shares (Lakhs/Cr vs Full counts)
                                        Surface(
                                            color = ChipGray,
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, BorderLight),
                                            modifier = Modifier.clickable { viewModel.toggleShareDisplayMode() }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Tune,
                                                    contentDescription = null,
                                                    tint = PrimaryOrange,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = if (uiState.useCompactShares) "Units: L/Cr" else "Units: Full",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryOrangeDark
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (subs != null && subs.categoryRows.isNotEmpty()) {
                                        SubscriptionTableView(
                                            subscriptionDetails = subs,
                                            useCompactShares = uiState.useCompactShares
                                        )
                                    } else {
                                        Text(
                                            text = "Official subscription data will be published upon exchange bidding commencement.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NeutralGray,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Section B: Day-wise Subscription Progression Table
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Day-Wise Subscription Progression (times)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Day 1, Day 2, and Day 3 bidding progress",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    DayWiseSubscriptionTableView(
                                        dayProgress = subs?.dayProgress ?: emptyList()
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Day 1-3 Trend Chart & Table Combined View
                        item {
                            if (subs != null && subs.dayProgress.isNotEmpty()) {
                                DayWiseProgressionTrendChart(days = subs.dayProgress)
                            } else {
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, BorderLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Day-wise bidding progression data will be available at 5:00 PM IST on bidding days.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeutralGray,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Day 1 → Day 2 → Day 3 Breakdown Table",
                                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    DayWiseSubscriptionTableView(
                                        dayProgress = subs?.dayProgress ?: emptyList()
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // Category Visual Multiples Charts
                        item {
                            if (subs != null) {
                                CategorySubscriptionBarChart(subscriptionDetails = subs)
                            }
                        }

                        item {
                            if (subs != null && subs.dayProgress.isNotEmpty()) {
                                DayWiseProgressionTrendChart(days = subs.dayProgress)
                            }
                        }
                    }
                }

                // 7. Official Exchange Guidelines & Disclosure Note
                item {
                    ExchangeGuidelineCard()
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * Clean Official Exchange Notice Banner
 * Complies with requirement: Do not use fake live data labels as real data
 */
@Composable
private fun OfficialExchangeNoticeBanner(
    lastUpdated: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SecondaryBlueLight,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SecondaryBlueContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = SecondaryBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Exchange Reported Bidding Data",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.5.sp),
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBlueDark
                )
                Text(
                    text = if (lastUpdated.isNotEmpty()) {
                        "Last updated: $lastUpdated • BSE & NSE Consolidated"
                    } else {
                        "Official updates published each bidding day at 5:00 PM IST."
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Filter Pill
 */
@Composable
private fun FilterPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) PrimaryOrange else ChipGray,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) PrimaryOrange else BorderLight),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

/**
 * Selected IPO Hero Card with Overall Subscription and Key Stats
 */
@Composable
private fun SelectedIpoHeroCard(
    ipo: IpoItem,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMainboard = ipo.category == IpoCategory.MAINBOARD
    val isOverSubscribed = ipo.currentSubscriptionTimes >= 1.0

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 0.5.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Name, Category Pill, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ipo.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${ipo.symbol} • ${ipo.listingExchanges}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = if (ipo.status == IpoStatus.OPEN) MarketGreenLight else ChipGray,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, if (ipo.status == IpoStatus.OPEN) MarketGreenBorder else BorderLight)
                ) {
                    Text(
                        text = when (ipo.status) {
                            IpoStatus.OPEN -> "BIDDING OPEN"
                            IpoStatus.UPCOMING -> "UPCOMING"
                            IpoStatus.CLOSED -> "BIDDING CLOSED"
                            IpoStatus.ALLOTMENT_PENDING -> "ALLOTMENT PENDING"
                            IpoStatus.ALLOTMENT_AVAILABLE -> "ALLOTMENT OUT"
                            IpoStatus.LISTED -> "LISTED"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (ipo.status == IpoStatus.OPEN || ipo.status == IpoStatus.ALLOTMENT_AVAILABLE) MarketGreen else NeutralGray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Metric Box: Overall Subscription Multiplier
            Surface(
                color = if (isOverSubscribed) MarketGreenLight else PrimaryOrangeLight,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, if (isOverSubscribed) MarketGreenBorder else PrimaryOrangeContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "OVERALL SUBSCRIPTION",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${"%.2f".format(ipo.currentSubscriptionTimes)}x",
                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                                fontWeight = FontWeight.Bold,
                                color = if (isOverSubscribed) MarketGreen else PrimaryOrange
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOverSubscribed) {
                                    "(${"%.0f".format(ipo.currentSubscriptionTimes * 100)}% Booked)"
                                } else {
                                    "(${"%.0f".format(ipo.currentSubscriptionTimes * 100)}% of Quota)"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }

                    Surface(
                        color = if (isOverSubscribed) MarketGreen else PrimaryOrange,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isOverSubscribed) "OVERSUBSCRIBED" else "FILLING UP",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Issue Summary Numbers (Offered Shares, Issue Size, Price Band)
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Price Band", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${ipo.priceBandMin.toInt()} - ₹${ipo.priceBandMax.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Issue Size", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CurrencyFormatter.formatCrores(ipo.issueSizeCr), style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Lot Size", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${ipo.lotSize} Shares", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Link to Full IPO Detail
            OutlinedButton(
                onClick = onViewDetail,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "VIEW FULL IPO PROSPECTUS & GMP",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBlue
                )
            }
        }
    }
}

/**
 * Category Multipliers Strip (Overall, QIB, NII/HNI, Retail, Employee, Other)
 */
@Composable
private fun CategoryMultipliersStrip(
    subs: com.example.ipotracker.data.model.SubscriptionDetails?,
    modifier: Modifier = Modifier
) {
    if (subs == null) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "CATEGORY MULTIPLIERS (TIMES)",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryPillCard("Overall", subs.overallTimes, PrimaryOrange)
            CategoryPillCard("QIB", subs.qibTimes, SecondaryBlue)
            CategoryPillCard("NII / HNI", subs.niiTimes, Color(0xFF7C3AED))
            CategoryPillCard("Retail", subs.retailTimes, MarketGreen)
            if (subs.employeeTimes > 0) {
                CategoryPillCard("Employee", subs.employeeTimes, Color(0xFFD97706))
            }
            if (subs.otherTimes > 0) {
                CategoryPillCard("Other", subs.otherTimes, Color(0xFF0D9488))
            }
        }
    }
}

@Composable
private fun CategoryPillCard(
    label: String,
    times: Double,
    color: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier.widthIn(min = 90.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${"%.2f".format(times)}x",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Educational & Regulatory Guidance Card
 */
@Composable
private fun ExchangeGuidelineCard(modifier: Modifier = Modifier) {
    Surface(
        color = ChipGray,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = NeutralGray,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BSE & NSE Exchange Bidding Rules",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.5.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• Bidding window is open from 10:00 AM to 5:00 PM IST on working days.\n" +
                        "• Consolidated tallies are published after exchange clearing processing at 5:00 PM.\n" +
                        "• Retail quota: min 35% in Book Building; QIB quota: max 50%; NII quota: min 15%.\n" +
                        "• UPI mandate approvals must be submitted before 5:00 PM on closing day.\n" +
                        "• Official data sourced from exchange end-of-day cumulative bid books.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}
