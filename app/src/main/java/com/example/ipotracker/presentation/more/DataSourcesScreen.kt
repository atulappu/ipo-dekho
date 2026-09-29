package com.example.ipotracker.presentation.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSourcesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Official Data Sources", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "DATA TRANSPARENCY & PROVENANCE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                DataSourceCard(
                    title = "BSE & NSE India",
                    category = "Official Stock Exchanges",
                    description = "Subscription numbers, live bidding tallies, category-wise demand, price bands and listing day tick-by-tick discovery are synchronized with exchange reports."
                )
            }

            item {
                DataSourceCard(
                    title = "SEBI Registered Registrars",
                    category = "Share Transfer Agents",
                    description = "Allotment status and basis of allotment reports originate from registered registrars including Link Intime India, KFin Technologies, and Bigshare Services."
                )
            }

            item {
                DataSourceCard(
                    title = "Statutory Filings (DRHP & RHP)",
                    category = "Prospectus & Financials",
                    description = "Financial statements, objects of the issue, promoter shareholding, and risk disclosures are sourced verbatim from Red Herring Prospectus (RHP) filings filed with SEBI."
                )
            }

            item {
                DataSourceCard(
                    title = "Grey Market Intelligence",
                    category = "Market Sentiment (Unofficial)",
                    description = "Grey Market Premium (GMP) data is aggregated from active market trading circles and broker desks. GMP does not represent regulated exchange quotations."
                )
            }
        }
    }
}

@Composable
fun DataSourceCard(
    title: String,
    category: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(category.uppercase(), style = MaterialTheme.typography.labelSmall, color = SecondaryBlue, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
        }
    }
}
