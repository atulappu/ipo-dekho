package com.example.ipotracker.presentation.home

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.data.model.*
import com.example.ipotracker.presentation.components.*
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ipotracker.utils.DateUtils
import com.example.ipotracker.presentation.ipo.IpoSortOption
import com.example.ipotracker.utils.applyIpoSorting
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToIpoList: (String?) -> Unit,
    onNavigateToCalculator: (String?) -> Unit,
    onNavigateToAllotment: (String?) -> Unit,
    onNavigateToComparison: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSubscription: (String?) -> Unit,
    onNavigateToApplicationInfo: (String) -> Unit = {},
    onNavigateToLiveMarket: (String) -> Unit = {},
    onNavigateToAiChat: (String?) -> Unit = {},
    onNavigateToVoiceLive: (String?) -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Tab state: 0 = OPEN, 1 = UPCOMING, 2 = CLOSED
    var selectedTopTab by remember { mutableStateOf(0) }
    var filterMainboard by remember { mutableStateOf(true) }
    var filterSme by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(IpoSortOption.DEFAULT) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                // Top App Bar
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "IPO",
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryOrange
                                )
                                Text(
                                    text = "Dekho",
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                    fontWeight = FontWeight.Black,
                                    color = SecondaryBlue
                                )
                            }
                            Text(
                                text = "Track. Compare. Understand IPOs.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    actions = {
                        // Subscription pill button (honest financial data, no fake live pulse)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PrimaryOrangeLight,
                            border = BorderStroke(1.dp, PrimaryOrangeContainer),
                            modifier = Modifier
                                .clickable { onNavigateToSubscription(null) }
                                .padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Leaderboard,
                                    contentDescription = null,
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Subscription",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryOrangeDark
                                )
                            }
                        }


                        IconButton(
                            onClick = onNavigateToSearch,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = onNavigateToCalendar,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendar", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Top Segment Tabs: OPEN | UPCOMING | CLOSED
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TopCategoryTab(
                        title = "OPEN",
                        count = uiState.openIpos.size,
                        color = MarketGreen,
                        isSelected = selectedTopTab == 0,
                        onClick = { selectedTopTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TopCategoryTab(
                        title = "UPCOMING",
                        count = uiState.upcomingIpos.size,
                        color = PrimaryOrange,
                        isSelected = selectedTopTab == 1,
                        onClick = { selectedTopTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    TopCategoryTab(
                        title = "CLOSED",
                        count = uiState.closedIpos.size,
                        color = NeutralGray,
                        isSelected = selectedTopTab == 2,
                        onClick = { selectedTopTab = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                // Market Type Multi-Select Filter & Sort Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MarketTypeFilter(
                        isMainboardSelected = filterMainboard,
                        isSmeSelected = filterSme,
                        onMarketTypeChanged = { mainboard, sme ->
                            filterMainboard = mainboard
                            filterSme = sme
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

                    // Professional "Sort By ▼" Menu Control
                    IpoSortDropdown(
                        selectedOption = selectedSortOption,
                        onSortOptionSelected = { selectedSortOption = it }
                    )
                }
            }
        }
    ) { innerPadding ->
        val rawList = when (selectedTopTab) {
            0 -> uiState.openIpos
            1 -> uiState.upcomingIpos
            else -> uiState.closedIpos
        }

        val filteredList = rawList
            .filter { ipo ->
                (filterMainboard && ipo.category == IpoCategory.MAINBOARD) ||
                (filterSme && ipo.category == IpoCategory.SME)
            }
            .applyIpoSorting(selectedSortOption)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Market Indices Bar
            item {
                MarketSummaryBar(indices = uiState.marketIndices)
            }

            // Quick Tools Section
            item {
                QuickToolsSection(
                    onCalculatorClick = { onNavigateToCalculator(null) },
                    onAllotmentClick = { onNavigateToAllotment(null) },
                    onComparisonClick = onNavigateToComparison,
                    onCalendarClick = onNavigateToCalendar
                )
            }

            // Section Header
            item {
                SectionHeader(
                    title = when (selectedTopTab) {
                        0 -> "Live & Open IPOs (${filteredList.size})"
                        1 -> "Upcoming IPO Pipeline (${filteredList.size})"
                        else -> "Recently Closed IPOs (${filteredList.size})"
                    },
                    actionLabel = "View All",
                    onActionClick = {
                        val status = when (selectedTopTab) {
                            0 -> "OPEN"
                            1 -> "UPCOMING"
                            else -> "CLOSED"
                        }
                        onNavigateToIpoList(status)
                    }
                )
            }

            // IPO Cards
            if (filteredList.isEmpty()) {
                item {
                    EmptyState(
                        title = "No IPOs Found",
                        message = "No IPOs match the active category filter.",
                        actionLabel = "Reset Filters",
                        onActionClick = {
                            filterMainboard = true
                            filterSme = true
                        }
                    )
                }
            } else {
                items(filteredList, key = { it.id }) { ipo ->
                    IpoCard(
                        ipo = ipo,
                        onIpoClick = onNavigateToDetail,
                        onWatchlistToggle = { viewModel.toggleWatchlist(it) },
                        onApplyClick = onNavigateToApplicationInfo,
                        onAllotmentClick = { onNavigateToAllotment(it) },
                        onWatchLiveClick = { onNavigateToLiveMarket(it) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Last Synced: ${uiState.lastUpdated}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "* Grey Market Premium (GMP) is unofficial market sentiment. It is subject to volatility and should not be considered investment advice.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = NeutralGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryFilterChip(
    label: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        color = if (isChecked) PrimaryOrangeLight else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (isChecked) PrimaryOrange else BorderLight),
        modifier = Modifier.clickable { onToggle(!isChecked) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (isChecked) PrimaryOrange else NeutralGray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                color = if (isChecked) PrimaryOrangeDark else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TopCategoryTab(
    title: String,
    count: Int,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    color = if (isSelected) color.copy(alpha = 0.15f) else ChipGray,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) color else NeutralGray,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(3.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
        } else {
            Box(modifier = Modifier.height(3.dp))
        }
    }
}

@Composable
fun QuickToolsSection(
    onCalculatorClick: () -> Unit,
    onAllotmentClick: () -> Unit,
    onComparisonClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickToolButton(
            title = "Calculator",
            icon = Icons.Default.Calculate,
            color = PrimaryOrange,
            onClick = onCalculatorClick,
            modifier = Modifier.weight(1f)
        )
        QuickToolButton(
            title = "Allotment",
            icon = Icons.Default.FactCheck,
            color = SecondaryBlue,
            onClick = onAllotmentClick,
            modifier = Modifier.weight(1f)
        )
        QuickToolButton(
            title = "Compare",
            icon = Icons.Default.CompareArrows,
            color = MarketGreen,
            onClick = onComparisonClick,
            modifier = Modifier.weight(1f)
        )
        QuickToolButton(
            title = "Calendar",
            icon = Icons.Default.CalendarMonth,
            color = PurpleFileBtn,
            onClick = onCalendarClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickToolButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 0.5.dp,
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
