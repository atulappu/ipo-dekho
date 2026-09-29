package com.example.ipotracker.presentation.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.presentation.detail.DetailKeyValueRow
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpoCalculatorScreen(
    viewModel: IpoCalculatorViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var expandedIpoMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("IPO Profit Calculator", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = PrimaryOrangeLight,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, PrimaryOrangeContainer)
                        ) {
                            Text(
                                text = "CALC",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrangeDark,
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
            // Select IPO Preset
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SELECT IPO (PRE-FILL METRICS)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box {
                            OutlinedButton(
                                onClick = { expandedIpoMenu = true },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiState.selectedIpo?.name ?: "Custom Calculation",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryBlue)
                                }
                            }

                            DropdownMenu(
                                expanded = expandedIpoMenu,
                                onDismissRequest = { expandedIpoMenu = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                uiState.ipos.forEach { ipo ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(ipo.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                                Text(
                                                    "₹${ipo.priceBandMax.toInt()} • Lot: ${ipo.lotSize} • GMP: ₹${ipo.currentGmp.toInt()}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectIpo(ipo)
                                            expandedIpoMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Calculation Inputs
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "INVESTMENT INPUTS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // IPO Price & Lot Size
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = uiState.ipoPrice.toInt().toString(),
                                onValueChange = { str -> str.toDoubleOrNull()?.let { viewModel.updatePrice(it) } },
                                label = { Text("Price (₹)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryOrange,
                                    unfocusedBorderColor = BorderLight
                                )
                            )
                            OutlinedTextField(
                                value = uiState.lotSize.toString(),
                                onValueChange = { str -> str.toIntOrNull()?.let { viewModel.updateLotSize(it) } },
                                label = { Text("Lot Size (sh)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryOrange,
                                    unfocusedBorderColor = BorderLight
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Lots Stepper & GMP
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Number of Lots", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                        .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 2.dp, vertical = 1.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.updateNumberOfLots(uiState.numberOfLots - 1) },
                                        enabled = uiState.numberOfLots > 1,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrement", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${uiState.numberOfLots}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.updateNumberOfLots(uiState.numberOfLots + 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increment", modifier = Modifier.size(16.dp), tint = PrimaryOrange)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = uiState.gmp.toInt().toString(),
                                onValueChange = { str -> str.toDoubleOrNull()?.let { viewModel.updateGmp(it) } },
                                label = { Text("GMP (₹/sh)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryOrange,
                                    unfocusedBorderColor = BorderLight
                                )
                            )
                        }
                    }
                }
            }

            // Results Summary
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(15.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MarketGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ESTIMATED RETURNS BREAKDOWN",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Profit Hero Display
                        Surface(
                            color = MarketGreenLight,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MarketGreenBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Estimated Net Profit",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Medium,
                                    color = MarketGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = CurrencyFormatter.formatRupee(uiState.estimatedTotalGain),
                                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MarketGreen
                                )
                                Text(
                                    text = "+${"%.2f".format(uiState.estimatedGainPercent)}% Expected Return",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MarketGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DetailKeyValueRow("Total Shares Applied", "${uiState.totalShares} Shares", isAlt = false)
                        DetailKeyValueRow("Total Investment", CurrencyFormatter.formatRupee(uiState.investmentAmount), isAlt = true)
                        DetailKeyValueRow("Estimated Listing Price", "₹${uiState.estimatedListingPrice.toInt()}/-", isAlt = false)
                        DetailKeyValueRow("Estimated Total Value", CurrencyFormatter.formatRupee(uiState.investmentAmount + uiState.estimatedTotalGain), isAlt = true)
                    }
                }
            }
        }
    }
}
