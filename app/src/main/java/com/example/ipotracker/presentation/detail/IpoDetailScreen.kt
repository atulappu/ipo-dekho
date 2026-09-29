package com.example.ipotracker.presentation.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.data.model.*
import com.example.ipotracker.presentation.components.*
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ipotracker.utils.DateUtils
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpoDetailScreen(
    viewModel: IpoDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCalculator: (String) -> Unit,
    onNavigateToApplicationInfo: (String) -> Unit,
    onNavigateToSubscription: ((String) -> Unit)? = null,
    onNavigateToAllotment: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val ipo = uiState.ipo

    // Collapsible section states for information density without clutter
    var isFinancialsExpanded by remember { mutableStateOf(false) }
    var isDatesExpanded by remember { mutableStateOf(true) }
    var isRisksExpanded by remember { mutableStateOf(false) }
    var isObjectsExpanded by remember { mutableStateOf(false) }
    var isGmpChartExpanded by remember { mutableStateOf(false) }

    if (ipo == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("IPO Details", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = PrimaryOrange)
                else Text(uiState.error ?: "IPO details not found", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    val isMainboard = ipo.category == IpoCategory.MAINBOARD
    val isBiddingOpen = ipo.status == IpoStatus.OPEN
    val retailLotGain = (ipo.currentGmp * ipo.lotSize).coerceAtLeast(0.0)
    val hniLotGain = (ipo.currentGmp * ipo.lotSize * 14).coerceAtLeast(0.0)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = ipo.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${ipo.symbol} • ${ipo.listingExchanges} • ${if (isMainboard) "Mainboard" else "SME"}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Watchlist toggle
                    IconButton(
                        onClick = { viewModel.toggleWatchlist() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (ipo.isWatchlisted) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (ipo.isWatchlisted) PrimaryOrange else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Share button
                    Surface(
                        color = SecondaryBlue,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { shareIpo(context, ipo) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. TOP HERO: Company Header, Status & Key Value Strip
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Sector and Category badges
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = if (isMainboard) SecondaryBlueLight else PrimaryOrangeLight,
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, if (isMainboard) SecondaryBlueContainer else PrimaryOrangeContainer)
                                ) {
                                    Text(
                                        text = if (isMainboard) "MAINBOARD" else "SME IPO",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMainboard) SecondaryBlueDark else PrimaryOrangeDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    color = ChipGray,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = ipo.sector,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Status Tag
                            Surface(
                                color = when (ipo.status) {
                                    IpoStatus.OPEN -> MarketGreenLight
                                    IpoStatus.UPCOMING -> PrimaryOrangeLight
                                    IpoStatus.CLOSED, IpoStatus.ALLOTMENT_PENDING -> ChipGray
                                    IpoStatus.ALLOTMENT_AVAILABLE -> MarketGreenLight
                                    IpoStatus.LISTED -> SecondaryBlueLight
                                },
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(
                                    1.dp,
                                    when (ipo.status) {
                                        IpoStatus.OPEN -> MarketGreenBorder
                                        IpoStatus.UPCOMING -> PrimaryOrangeContainer
                                        IpoStatus.CLOSED, IpoStatus.ALLOTMENT_PENDING -> BorderLight
                                        IpoStatus.ALLOTMENT_AVAILABLE -> MarketGreenBorder
                                        IpoStatus.LISTED -> SecondaryBlueContainer
                                    }
                                )
                            ) {
                                Text(
                                    text = when (ipo.status) {
                                        IpoStatus.OPEN -> "OPEN NOW"
                                        IpoStatus.UPCOMING -> "UPCOMING"
                                        IpoStatus.CLOSED -> "CLOSED"
                                        IpoStatus.ALLOTMENT_PENDING -> "WAITING"
                                        IpoStatus.ALLOTMENT_AVAILABLE -> "ALLOTMENT OUT"
                                        IpoStatus.LISTED -> "LISTED"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = when (ipo.status) {
                                        IpoStatus.OPEN -> MarketGreen
                                        IpoStatus.UPCOMING -> PrimaryOrange
                                        IpoStatus.CLOSED, IpoStatus.ALLOTMENT_PENDING -> NeutralGray
                                        IpoStatus.ALLOTMENT_AVAILABLE -> MarketGreen
                                        IpoStatus.LISTED -> SecondaryBlue
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Compact Core Metrics Row (Price, Min Inv, Lot, Issue Size)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TableHeaderBg, RoundedCornerShape(6.dp))
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Price Band", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${ipo.priceBandMin.toInt()} - ₹${ipo.priceBandMax.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lot Size", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${ipo.lotSize} Shares", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Min. Investment", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(CurrencyFormatter.formatRupee(ipo.minInvestment), style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold, color = PrimaryOrange)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Issue Size", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(CurrencyFormatter.formatCrores(ipo.issueSizeCr), style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. PRIMARY ACTION BAR: Form Print / App Info + Quick Tools
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onNavigateToApplicationInfo(ipo.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "IPO FORM PRINT / BID INFO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = { onNavigateToCalculator(ipo.id) },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = SecondaryBlue, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CALCULATOR", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = SecondaryBlue)
                    }
                }
            }

            // 3. GREY MARKET PREMIUM (GMP) & ESTIMATED LISTING CARD
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (ipo.currentGmp > 0) MarketGreen else NeutralGray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Grey Market Premium (GMP)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Updated: ${ipo.lastGmpUpdated}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // GMP Highlight Numbers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = if (ipo.currentGmp > 0) MarketGreenLight else ChipGray,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, if (ipo.currentGmp > 0) MarketGreenBorder else BorderLight),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Current GMP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = if (ipo.currentGmp > 0) "+₹${ipo.currentGmp.toInt()}" else "₹0",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = if (ipo.currentGmp > 0) MarketGreen else NeutralGray
                                    )
                                    Text(
                                        text = if (ipo.currentGmp > 0) CurrencyFormatter.formatPercent(ipo.estimatedGainPercent) else "0.0%",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (ipo.currentGmp > 0) MarketGreen else NeutralGray
                                    )
                                }
                            }

                            Surface(
                                color = ChipGray,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Est. Listing Price", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "₹${"%.1f".format(ipo.estimatedListingPrice)}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Base: ₹${ipo.priceBandMax.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                color = ChipGray,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Retail Lot Gain*", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = if (retailLotGain > 0) CurrencyFormatter.formatRupee(retailLotGain) else "--",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = if (retailLotGain > 0) MarketGreen else NeutralGray
                                    )
                                    Text(
                                        text = "sHNI: ${if (hniLotGain > 0) CurrencyFormatter.formatRupee(hniLotGain) else "--"}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Collapsible GMP Trend Chart
                        if (ipo.gmpHistory.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isGmpChartExpanded = !isGmpChartExpanded }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isGmpChartExpanded) "Hide GMP Trend Chart" else "Show GMP History Trend (${ipo.gmpHistory.size} entries)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    color = SecondaryBlue
                                )
                                Icon(
                                    imageVector = if (isGmpChartExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = SecondaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            AnimatedVisibility(visible = isGmpChartExpanded) {
                                Column {
                                    GmpTrendChart(history = ipo.gmpHistory)
                                }
                            }
                        }
                    }
                }
            }

            // 4. SUBSCRIPTION SNAPSHOT & DAY-WISE TREND
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = SecondaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Subscription Status",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                color = if (ipo.currentSubscriptionTimes >= 1.0) MarketGreenLight else PrimaryOrangeLight,
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, if (ipo.currentSubscriptionTimes >= 1.0) MarketGreenBorder else PrimaryOrangeContainer)
                            ) {
                                Text(
                                    text = "${"%.2f".format(ipo.currentSubscriptionTimes)}x Total",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (ipo.currentSubscriptionTimes >= 1.0) MarketGreen else PrimaryOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Quota Multipliers Strip
                        val subs = ipo.subscriptionDetails
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CompactCategoryBadge("QIB", ipo.qibTimes, SecondaryBlue)
                            CompactCategoryBadge("NII / HNI", ipo.niiTimes, Color(0xFF7C3AED))
                            CompactCategoryBadge("Retail", ipo.retailTimes, MarketGreen)
                            if (subs != null && subs.employeeTimes > 0) {
                                CompactCategoryBadge("Employee", subs.employeeTimes, Color(0xFFD97706))
                            }
                            if (subs != null && subs.otherTimes > 0) {
                                CompactCategoryBadge("Other", subs.otherTimes, Color(0xFF0D9488))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category breakdown table
                        if (subs != null && subs.categoryRows.isNotEmpty()) {
                            SubscriptionTableView(subscriptionDetails = subs, useCompactShares = true)
                        } else {
                            Text(
                                text = "Subscription bids are tallied by exchange clearing at 5:00 PM IST on bidding days.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = NeutralGray
                            )
                        }

                        // Day 1 / 2 / 3 progression table
                        if (subs != null && subs.dayProgress.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Day 1 / Day 2 / Day 3 Trend (times)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            DayWiseSubscriptionTableView(dayProgress = subs.dayProgress)
                        }

                        // Full breakdown link
                        if (onNavigateToSubscription != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { onNavigateToSubscription(ipo.id) },
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryOrange)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OPEN COMPLETE SUBSCRIPTION & TRENDS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryOrangeDark
                                )
                            }
                        }
                    }
                }
            }

            // 5. IMPORTANT DATES TIMELINE
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        CollapsibleHeader(
                            title = "Important Dates & Timetable",
                            isExpanded = isDatesExpanded,
                            onToggle = { isDatesExpanded = !isDatesExpanded }
                        )

                        AnimatedVisibility(visible = isDatesExpanded) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                if (ipo.importantDates.isNotEmpty()) {
                                    ipo.importantDates.forEachIndexed { index, dateItem ->
                                        ImportantDateTimelineRow(
                                            item = dateItem,
                                            isLast = index == ipo.importantDates.size - 1
                                        )
                                    }
                                } else {
                                    // Fallback to core dates
                                    DetailKeyValueRow("IPO Open Date", DateUtils.formatDisplayDate(ipo.openDate), isAlt = false)
                                    DetailKeyValueRow("IPO Close Date", DateUtils.formatDisplayDate(ipo.closeDate), isAlt = true)
                                    DetailKeyValueRow("Basis of Allotment", DateUtils.formatDisplayDate(ipo.allotmentDate), isAlt = false)
                                    DetailKeyValueRow("Listing Date", DateUtils.formatDisplayDate(ipo.listingDate), isAlt = true)
                                }
                            }
                        }
                    }
                }
            }

            // 6. ABOUT IPO & ISSUE SPECIFICATIONS
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(SecondaryBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Issue Specifications & Capital Structure",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        DetailKeyValueRow("Total Issue Size", CurrencyFormatter.formatCrores(ipo.issueSizeCr), isAlt = false)
                        DetailKeyValueRow("Fresh Issue Portion", CurrencyFormatter.formatCrores(ipo.freshIssueCr), isAlt = true)
                        DetailKeyValueRow("Offer for Sale (OFS)", CurrencyFormatter.formatCrores(ipo.ofsCr), isAlt = false)
                        DetailKeyValueRow("Face Value", "₹${ipo.faceValue.toInt()} per share", isAlt = true)
                        DetailKeyValueRow("Price Band", "₹${ipo.priceBandMin.toInt()} - ₹${ipo.priceBandMax.toInt()}", isAlt = false)
                        DetailKeyValueRow("Lot Size", "${ipo.lotSize} shares", isAlt = true)
                        DetailKeyValueRow("Listing Exchanges", ipo.listingExchanges, isAlt = false)
                        DetailKeyValueRow("Issue Type", "${ipo.category.name} Book Building IPO", isAlt = true)
                    }
                }
            }

            // 7. LOTS DISTRIBUTION TABLE
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Lot(s) Distribution & Bidding Limits",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Application limits for Retail, sHNI, and bHNI",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LotsDistributionTable(ipo = ipo)
                    }
                }
            }

            // 8. FINANCIALS SECTION (Expandable table + visual chart)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        CollapsibleHeader(
                            title = "Financial Performance (₹ in Cr)",
                            isExpanded = isFinancialsExpanded,
                            onToggle = { isFinancialsExpanded = !isFinancialsExpanded }
                        )

                        AnimatedVisibility(visible = isFinancialsExpanded) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                if (ipo.financials.isNotEmpty()) {
                                    // Multi-year comparison table
                                    FinancialsCompactTable(financials = ipo.financials)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    FinancialsBarChart(financials = ipo.financials)
                                } else {
                                    Text(
                                        text = "Audited financials will be published in the RHP document.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = NeutralGray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 9. COMPANY OVERVIEW & PROMOTER HOLDING
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Company Overview",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ipo.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )

                        if (ipo.promoterHoldingPre > 0 || ipo.promoterHoldingPost > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Promoter Holding (Pre-Issue)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${ipo.promoterHoldingPre}%", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Promoter Holding (Post-Issue)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${ipo.promoterHoldingPost}%", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold, color = PrimaryOrange)
                                }
                            }
                        }
                    }
                }
            }

            // 10. OBJECTS OF THE ISSUE (Expandable)
            if (ipo.objectsOfIssue.isNotEmpty()) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        shadowElevation = 0.5.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            CollapsibleHeader(
                                title = "Objects of the Issue",
                                isExpanded = isObjectsExpanded,
                                onToggle = { isObjectsExpanded = !isObjectsExpanded }
                            )

                            AnimatedVisibility(visible = isObjectsExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    ipo.objectsOfIssue.forEachIndexed { idx, obj ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "${idx + 1}. ",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryOrange
                                            )
                                            Text(
                                                text = obj,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 11. STRENGTH & RISK FACTORS (Expandable)
            if (ipo.analysisReport != null) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        shadowElevation = 0.5.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            CollapsibleHeader(
                                title = "Key Strengths & Risk Factors",
                                isExpanded = isRisksExpanded,
                                onToggle = { isRisksExpanded = !isRisksExpanded }
                            )

                            AnimatedVisibility(visible = isRisksExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    // Strengths
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MarketGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Key Positives", style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold, color = MarketGreen)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ipo.analysisReport.keyPositives.forEach { s ->
                                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                            Text("• ", color = MarketGreen, fontWeight = FontWeight.Bold)
                                            Text(s, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Risks
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = MarketRed, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Key Risks & Watchpoints", style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold, color = MarketRed)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ipo.analysisReport.keyRisks.forEach { r ->
                                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                            Text("• ", color = MarketRed, fontWeight = FontWeight.Bold)
                                            Text(r, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 12. REGISTRAR, LEAD MANAGERS & ALLOTMENT PORTAL
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Registrar & Allotment Verification",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DetailKeyValueRow("Registrar", ipo.registrar, isAlt = false)
                        val regUrl = ipo.allotmentInfo?.registrarUrl ?: "https://ris.kfintech.com/ipostatus/"
                        DetailKeyValueRow("Registrar Portal", regUrl, isAlt = true)
                        DetailKeyValueRow("Lead Manager(s)", ipo.leadManagers, isAlt = false)
                        DetailKeyValueRow("Allotment Date", DateUtils.formatDisplayDate(ipo.allotmentDate), isAlt = true)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Allotment Portal Launch Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (onNavigateToAllotment != null) onNavigateToAllotment(ipo.id)
                                    else {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(regUrl))
                                        context.startActivity(intent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CHECK ALLOTMENT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bseindia.com/investors/appli_check.aspx"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(36.dp)
                            ) {
                                Text("BSE PORTAL", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                            }
                        }
                    }
                }
            }

            // 13. LISTING INFORMATION & POST-LISTING DATA
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Listing Information",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DetailKeyValueRow("Listing Exchanges", ipo.listingExchanges, isAlt = false)
                        DetailKeyValueRow("Listing Date", DateUtils.formatDisplayDate(ipo.listingDate), isAlt = true)

                        if (ipo.listingPrice != null && ipo.listingPrice > 0) {
                            DetailKeyValueRow("Actual Listing Price", "₹${"%.2f".format(ipo.listingPrice)}", isAlt = false)
                            DetailKeyValueRow(
                                "Listing Gain",
                                "${CurrencyFormatter.formatPercent(ipo.listingGainPercent ?: 0.0)}",
                                isAlt = true
                            )
                        } else {
                            DetailKeyValueRow("Expected Listing Price", "₹${"%.1f".format(ipo.estimatedListingPrice)}", isAlt = false)
                            DetailKeyValueRow("Expected Listing Gain", "${CurrencyFormatter.formatPercent(ipo.estimatedGainPercent)} (Based on GMP)", isAlt = true)
                        }
                    }
                }
            }

            // 14. STATUTORY FINANCIAL DISCLAIMER
            item {
                Surface(
                    color = ChipGray,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Disclaimer & Risk Disclosure",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All IPO data, GMP figures, subscription multiples, and financial snapshots are compiled for educational purposes from BSE, NSE, and SEBI filings. GMP is an unofficial market indicator and is not guaranteed by stock exchanges. Consult a SEBI registered investment advisor before bidding.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Compact Category Multiplier Badge
 */
@Composable
private fun CompactCategoryBadge(label: String, times: Double, color: Color) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = Modifier.widthIn(min = 76.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${"%.2f".format(times)}x",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Collapsible Section Header
 */
@Composable
private fun CollapsibleHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Important Date Timeline Row
 */
@Composable
private fun ImportantDateTimelineRow(
    item: ImportantDateItem,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(
                    when (item.status) {
                        DateStatus.COMPLETED -> MarketGreen
                        DateStatus.ACTIVE -> PrimaryOrange
                        DateStatus.UPCOMING -> NeutralGray
                    }
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            fontWeight = if (item.status == DateStatus.ACTIVE) FontWeight.Bold else FontWeight.Medium,
            color = if (item.status == DateStatus.ACTIVE) PrimaryOrange else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = item.dateStr,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(6.dp))
        Surface(
            color = when (item.status) {
                DateStatus.COMPLETED -> MarketGreenLight
                DateStatus.ACTIVE -> PrimaryOrangeLight
                DateStatus.UPCOMING -> ChipGray
            },
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.widthIn(min = 60.dp)
        ) {
            Text(
                text = item.status.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                fontWeight = FontWeight.Bold,
                color = when (item.status) {
                    DateStatus.COMPLETED -> MarketGreen
                    DateStatus.ACTIVE -> PrimaryOrange
                    DateStatus.UPCOMING -> NeutralGray
                },
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                textAlign = TextAlign.Center
            )
        }
    }
    if (!isLast) {
        HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
    }
}

/**
 * Compact Financial Table (FY23 - FY26)
 */
@Composable
private fun FinancialsCompactTable(financials: List<FinancialYearData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, TableBorder, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TableHeaderBg)
                .padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Period", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Revenue", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            Text("PAT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            Text("Net Worth", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
        }
        HorizontalDivider(color = TableBorder)

        financials.forEachIndexed { idx, f ->
            val bg = if (idx % 2 == 1) TableRowAlt else MaterialTheme.colorScheme.surface
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bg)
                    .padding(vertical = 5.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(f.fiscalYear, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Text("₹${"%.1f".format(f.revenueCr)}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                Text("₹${"%.1f".format(f.patCr)}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), fontWeight = FontWeight.Bold, color = MarketGreen, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                Text("₹${"%.1f".format(f.netWorthCr)}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
            }
            if (idx < financials.size - 1) {
                HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

/**
 * Reusable Lots Distribution Table
 */
@Composable
fun LotsDistributionTable(ipo: IpoItem) {
    val minShares = ipo.lotSize
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, TableBorder, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TableHeaderBg)
                .padding(vertical = 7.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
            Text("Retail", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            Text("sHNI (2L-10L)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
            Text("bHNI (>10L)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
        }
        HorizontalDivider(color = TableBorder)

        LotRow("Shares", "$minShares", "${minShares * 14}", "${minShares * 68}", isAlt = false)
        HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
        LotRow("Amount", CurrencyFormatter.formatRupee(ipo.minInvestment), CurrencyFormatter.formatRupee(ipo.minInvestment * 14), CurrencyFormatter.formatRupee(ipo.minInvestment * 68), isAlt = true)
        HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
        LotRow("Lots", "1 Lot", "14 Lots", "68 Lots", isAlt = false, isBold = true)
    }
}

@Composable
private fun LotRow(
    category: String,
    col1: String,
    col2: String,
    col3: String,
    isAlt: Boolean,
    isBold: Boolean = false
) {
    val bg = if (isAlt) TableRowAlt else MaterialTheme.colorScheme.surface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(vertical = 6.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.2f)
        )
        Text(col1, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text(col2, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
        Text(col3, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
    }
}

@Composable
fun DetailKeyValueRow(key: String, value: String, isAlt: Boolean = false) {
    val bg = if (isAlt) TableRowAlt else MaterialTheme.colorScheme.surface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = key, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
