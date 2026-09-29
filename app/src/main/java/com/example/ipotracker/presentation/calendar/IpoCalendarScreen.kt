package com.example.ipotracker.presentation.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ipotracker.utils.DateUtils
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpoCalendarScreen(
    viewModel: IpoCalendarViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("IPO Event Calendar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = PurpleFileBtn.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, PurpleFileBtn.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "SCHEDULE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = PurpleFileBtn,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Event filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf("Bidding Opens", "Bidding Closes", "Allotment Basis", "Stock Listing")
                FilterChip(
                    selected = uiState.selectedEventType == null,
                    onClick = { viewModel.selectEventType(null) },
                    label = { Text("All Events", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryOrange,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(32.dp)
                )
                types.forEach { type ->
                    FilterChip(
                        selected = uiState.selectedEventType == type,
                        onClick = { viewModel.selectEventType(type) },
                        label = { Text(type, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryOrange,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }

            // Events List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.filteredEvents) { event ->
                    CalendarEventCard(
                        event = event,
                        onClick = { onNavigateToDetail(event.ipoId) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarEventCard(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    val (badgeBg, badgeFg, borderCol) = when (event.colorType) {
        "OPEN" -> Triple(MarketGreenLight, MarketGreen, MarketGreenBorder)
        "CLOSE" -> Triple(PrimaryOrangeLight, PrimaryOrange, PrimaryOrangeContainer)
        "ALLOTMENT" -> Triple(SecondaryBlueLight, SecondaryBlue, SecondaryBlueContainer)
        "LISTING" -> Triple(Color(0xFFEDE7F6), Color(0xFF5E35B1), Color(0xFFD1C4E9))
        else -> Triple(ChipGray, NeutralGray, BorderLight)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, borderCol),
                modifier = Modifier.width(62.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dateParts = DateUtils.formatDisplayDate(event.dateStr).split(" ")
                    Text(
                        text = dateParts.getOrNull(0) ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.Bold,
                        color = badgeFg
                    )
                    Text(
                        text = dateParts.getOrNull(1)?.uppercase() ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = badgeFg
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, borderCol)
                ) {
                    Text(
                        text = event.eventType.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = badgeFg,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.ipoName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
