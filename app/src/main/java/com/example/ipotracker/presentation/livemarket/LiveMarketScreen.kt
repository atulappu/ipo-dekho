package com.example.ipotracker.presentation.livemarket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.LiveMarketData
import com.example.ipotracker.domain.repository.IpoRepository
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMarketScreen(
    repository: IpoRepository,
    ipoId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var ipo by remember { mutableStateOf<IpoItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(ipoId) {
        repository.getIpoById(ipoId).collect { item ->
            ipo = item
            isLoading = false
            isRefreshing = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = ipo?.name ?: "Live Market Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = if (ipo != null) "${ipo?.symbol} • BSE / NSE Live" else "Real-time Listing Feed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("live_market_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isRefreshing = true
                            coroutineScope.launch {
                                repository.refreshData()
                                isRefreshing = false
                            }
                        },
                        modifier = Modifier.testTag("live_market_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Live Data",
                            tint = if (isRefreshing) PrimaryOrange else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else {
            val currentIpo = ipo
            val liveData = currentIpo?.liveMarketData

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Live Status Badge Bar
                item {
                    Surface(
                        color = if (liveData != null && liveData.isLive) MarketGreenLight else ChipGray,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (liveData != null && liveData.isLive) MarketGreenBorder else BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (liveData != null && liveData.isLive) MarketGreen else NeutralGray)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (liveData != null && liveData.isLive) "LIVE MARKET FEED ACTIVE" else "MARKET STATUS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (liveData != null && liveData.isLive) MarketGreen else NeutralGray,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = liveData?.lastUpdated ?: "Official BSE/NSE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (liveData != null) {
                    // 1. Primary Live Price Card
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderLight),
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = liveData.companyName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "NSE / BSE: ${liveData.symbol}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = "CURRENT PRICE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = CurrencyFormatter.formatRupee(liveData.currentPrice),
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    val isGain = liveData.change >= 0
                                    Surface(
                                        color = if (isGain) MarketGreenLight else MarketRedLight,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, if (isGain) MarketGreenBorder else MarketRedDark)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${if (isGain) "+" else ""}${CurrencyFormatter.formatRupee(liveData.change)} (${if (isGain) "+" else ""}${"%.2f".format(liveData.changePercent)}%)",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isGain) MarketGreen else MarketRed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Comprehensive Listing & Market Stats Grid
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderLight),
                            shadowElevation = 0.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Market & Listing Statistics",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                StatRow("Listing Price", CurrencyFormatter.formatRupee(liveData.listingPrice))
                                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                                StatRow("Issue Price", CurrencyFormatter.formatRupee(liveData.issuePrice))
                                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

                                val isListingGain = liveData.listingGainLoss >= 0
                                StatRow(
                                    label = "Listing Gain / Loss",
                                    value = "${if (isListingGain) "+" else ""}${CurrencyFormatter.formatRupee(liveData.listingGainLoss)} (+${"%.2f".format(liveData.listingGainLossPercent)}%)",
                                    valueColor = if (isListingGain) MarketGreen else MarketRed
                                )
                                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

                                StatRow("Traded Volume", "%,d shares".format(liveData.volume))
                                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                                StatRow("52 Week High", CurrencyFormatter.formatRupee(liveData.week52High))
                                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                                StatRow("52 Week Low", CurrencyFormatter.formatRupee(liveData.week52Low))
                            }
                        }
                    }
                } else {
                    // Fallback when live data is unavailable (Do not fabricate prices)
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(ChipGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = NeutralGray,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Live market data is currently unavailable.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Official exchange quotes from BSE and NSE become active once market hours open on the listing date.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Disclaimer
                item {
                    Text(
                        text = "* Info is indicative, not investment advice. Market feeds are sourced from stock exchange public dissemination systems.",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // CTA Button to Full IPO Details
                if (currentIpo != null) {
                    item {
                        OutlinedButton(
                            onClick = { onNavigateToDetail(currentIpo.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("live_market_view_details_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, PrimaryOrange)
                        ) {
                            Icon(Icons.Default.ShowChart, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VIEW COMPLETE IPO ANALYSIS",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrange,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
