package com.example.ipotracker.presentation.comparison

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ipotracker.utils.DateUtils
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpoComparisonScreen(
    viewModel: IpoComparisonViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selected = uiState.selectedIpos

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("IPO Comparison", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MarketGreenLight,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, MarketGreenBorder)
                        ) {
                            Text(
                                text = "COMPARE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = MarketGreen,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
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
            // IPO Selectors (Chips)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SELECT IPOS TO COMPARE (2 TO 4)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.allIpos, key = { it.id }) { ipo ->
                                val isChosen = selected.any { it.id == ipo.id }
                                FilterChip(
                                    selected = isChosen,
                                    onClick = { viewModel.toggleIpoSelection(ipo) },
                                    label = { Text(ipo.name.take(16), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    leadingIcon = if (isChosen) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryOrange,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.height(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Comparison Table (Horizontal Scrollable)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SIDE-BY-SIDE METRICS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val scrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState)
                        ) {
                            Column {
                                // Table Header
                                Row(
                                    modifier = Modifier
                                        .background(TableHeaderBg, RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Parameter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), modifier = Modifier.width(130.dp))
                                    selected.forEach { ipo ->
                                        Text(
                                            text = ipo.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = PrimaryOrange,
                                            modifier = Modifier.width(125.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(color = TableBorder)

                                // Metrics
                                FinancialTableRow("Category", selected.map { it.category.name }, isAlt = false)
                                FinancialTableRow("Status", selected.map { it.status.name }, isAlt = true)
                                FinancialTableRow("Price Band", selected.map { "₹${it.priceBandMin.toInt()} - ₹${it.priceBandMax.toInt()}" }, isAlt = false)
                                FinancialTableRow("Lot Size", selected.map { "${it.lotSize} shares" }, isAlt = true)
                                FinancialTableRow("Min Investment", selected.map { CurrencyFormatter.formatRupee(it.minInvestment) }, isAlt = false)
                                FinancialTableRow("Issue Size", selected.map { CurrencyFormatter.formatCrores(it.issueSizeCr) }, isAlt = true)
                                FinancialTableRow("Fresh Issue", selected.map { CurrencyFormatter.formatCrores(it.freshIssueCr) }, isAlt = false)
                                FinancialTableRow("OFS", selected.map { CurrencyFormatter.formatCrores(it.ofsCr) }, isAlt = true)
                                FinancialTableRow("GMP", selected.map { if (it.currentGmp > 0) "₹${it.currentGmp.toInt()}" else "N/A" }, isAlt = false)
                                FinancialTableRow("Est. Listing Price", selected.map { if (it.currentGmp > 0) "₹${it.estimatedListingPrice.toInt()}" else "N/A" }, isAlt = true)
                                FinancialTableRow("Est. Gain %", selected.map { if (it.currentGmp > 0) CurrencyFormatter.formatPercent(it.estimatedGainPercent) else "N/A" }, isAlt = false)
                                FinancialTableRow("Total Subscription", selected.map { if (it.currentSubscriptionTimes > 0) CurrencyFormatter.formatSubscription(it.currentSubscriptionTimes) else "N/A" }, isAlt = true)
                                FinancialTableRow("QIB Subscribed", selected.map { if (it.qibTimes > 0) CurrencyFormatter.formatSubscription(it.qibTimes) else "N/A" }, isAlt = false)
                                FinancialTableRow("NII Subscribed", selected.map { if (it.niiTimes > 0) CurrencyFormatter.formatSubscription(it.niiTimes) else "N/A" }, isAlt = true)
                                FinancialTableRow("Retail Subscribed", selected.map { if (it.retailTimes > 0) CurrencyFormatter.formatSubscription(it.retailTimes) else "N/A" }, isAlt = false)
                                FinancialTableRow("Issue Dates", selected.map { "${DateUtils.formatDisplayDate(it.openDate)} - ${DateUtils.formatDisplayDate(it.closeDate)}" }, isAlt = true)
                                FinancialTableRow("Listing Date", selected.map { DateUtils.formatDisplayDate(it.listingDate) }, isAlt = false)
                                FinancialTableRow("Sector", selected.map { it.sector }, isAlt = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialTableRow(label: String, values: List<String>, isAlt: Boolean = false) {
    val bg = if (isAlt) TableRowAlt else MaterialTheme.colorScheme.surface
    Row(
        modifier = Modifier
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(130.dp))
        values.forEach { v ->
            Text(v, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), fontWeight = FontWeight.SemiBold, modifier = Modifier.width(125.dp))
        }
    }
    HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
}
