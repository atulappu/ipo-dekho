package com.example.ipotracker.presentation.gmp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.presentation.components.CategoryBadge
import com.example.ipotracker.presentation.components.GmpTrendChart
import com.example.ipotracker.presentation.components.StatusBadge
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ui.theme.*

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ipotracker.presentation.components.MarketTypeFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GmpScreen(
    viewModel: GmpViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCalculator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedChartIpo = uiState.ipos.find { it.id == uiState.selectedIpoIdForChart }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Grey Market Premium",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = PrimaryOrangeLight,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, PrimaryOrangeContainer)
                        ) {
                            Text(
                                text = "GMP",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrangeDark,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Unofficial GMP Disclaimer Banner
            item {
                Surface(
                    color = PrimaryOrangeLight,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryOrangeContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Disclaimer: GMP is unofficial market sentiment. It is NOT guaranteed listing price or assured profit. Always conduct fundamental research before applying.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = PrimaryOrangeDark,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Market Type Filter row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MarketTypeFilter(
                        isMainboardSelected = uiState.isMainboardSelected,
                        isSmeSelected = uiState.isSmeSelected,
                        onMarketTypeChanged = { mainboard, sme ->
                            viewModel.setMarketType(mainboard, sme)
                        },
                        onValidationWarning = { message ->
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )

                    Text(
                        text = "${uiState.filteredIpos.size} GMPs",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Selected IPO GMP History Chart
            if (selectedChartIpo != null && selectedChartIpo.gmpHistory.isNotEmpty()) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        shadowElevation = 0.5.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "GMP Trend: ${selectedChartIpo.name}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Current: ₹${selectedChartIpo.currentGmp.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MarketGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            GmpTrendChart(history = selectedChartIpo.gmpHistory)
                        }
                    }
                }
            }

            // List of GMP Cards
            items(uiState.filteredIpos, key = { it.id }) { ipo ->
                GmpCard(
                    ipo = ipo,
                    isSelectedForChart = ipo.id == uiState.selectedIpoIdForChart,
                    onSelectForChart = { viewModel.selectIpoForChart(ipo.id) },
                    onIpoClick = { onNavigateToDetail(ipo.id) },
                    onCalculatorClick = { onNavigateToCalculator(ipo.id) }
                )
            }
        }
    }
}

@Composable
fun GmpCard(
    ipo: IpoItem,
    isSelectedForChart: Boolean,
    onSelectForChart: () -> Unit,
    onIpoClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val retailLotsGain = (ipo.currentGmp * ipo.lotSize).coerceAtLeast(0.0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onIpoClick() }
            .testTag("gmp_card_${ipo.id}"),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isSelectedForChart) PrimaryOrange else BorderLight),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ipo.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.5.sp),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadge(category = ipo.category)
                        StatusBadge(status = ipo.status)
                    }
                }
                Text(
                    text = ipo.lastGmpUpdated,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Metrics Grid (4 columns, responsive on 360dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("IPO PRICE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${ipo.priceBandMax.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("GMP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (ipo.currentGmp > 0) "₹${ipo.currentGmp.toInt()}" else "N/A",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (ipo.currentGmp > 0) MarketGreen else NeutralGray
                    )
                }

                Column(modifier = Modifier.weight(1.1f)) {
                    Text("EST. LISTING", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (ipo.currentGmp > 0) "₹${ipo.estimatedListingPrice.toInt()}" else "₹${ipo.priceBandMax.toInt()}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("EST. GAIN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (ipo.currentGmp > 0) "+${"%.1f".format(ipo.estimatedGainPercent)}%" else "--",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (ipo.currentGmp > 0) MarketGreen else NeutralGray
                    )
                }
            }

            if (retailLotsGain > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Est. Profit (1 Lot):",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.formatRupee(retailLotsGain),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold,
                        color = MarketGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons: Trend, Calculator, Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (ipo.gmpHistory.isNotEmpty()) {
                    TextButton(
                        onClick = onSelectForChart,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isSelectedForChart) "Viewing" else "Trend",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    onClick = onCalculatorClick,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("CALCULATOR", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = onIpoClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("DETAILS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
