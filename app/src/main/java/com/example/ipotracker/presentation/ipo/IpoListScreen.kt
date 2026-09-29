package com.example.ipotracker.presentation.ipo

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.presentation.components.EmptyState
import com.example.ipotracker.presentation.components.IpoCard
import com.example.ui.theme.PrimaryOrange
import com.example.ui.theme.SecondaryBlue

import kotlinx.coroutines.launch
import com.example.ipotracker.presentation.components.MarketTypeFilter
import com.example.ipotracker.presentation.components.IpoSortDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IpoListScreen(
    viewModel: IpoListViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCalculator: ((String) -> Unit)? = null,
    onNavigateToApplicationInfo: (String) -> Unit = {},
    onNavigateToAllotment: ((String) -> Unit)? = null,
    onNavigateToLiveMarket: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.lastSyncTime) {
        if (uiState.lastSyncTime != null) {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = "IPO statuses and data refreshed successfully",
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(uiState.syncErrorMessage) {
        uiState.syncErrorMessage?.let { error ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSyncError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All IPOs",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        enabled = !uiState.isRefreshing,
                        modifier = Modifier.testTag("refresh_ipos_button")
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryOrange
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh IPOs",
                                tint = MaterialTheme.colorScheme.onSurface
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search by company, symbol or sector...", fontSize = 12.5.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(15.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .height(48.dp)
                    .testTag("ipo_search_input")
            )

            // Status Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedStatus == null,
                    onClick = { viewModel.selectStatus(null) },
                    label = { Text("All Status", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryOrange,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(30.dp)
                )
                FilterChip(
                    selected = uiState.selectedStatus == IpoStatus.OPEN,
                    onClick = { viewModel.selectStatus(IpoStatus.OPEN) },
                    label = { Text("Open Now", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryOrange,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(30.dp)
                )
                FilterChip(
                    selected = uiState.selectedStatus == IpoStatus.UPCOMING,
                    onClick = { viewModel.selectStatus(IpoStatus.UPCOMING) },
                    label = { Text("Upcoming", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryOrange,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(30.dp)
                )
                FilterChip(
                    selected = uiState.selectedStatus == IpoStatus.CLOSED,
                    onClick = { viewModel.selectStatus(IpoStatus.CLOSED) },
                    label = { Text("Closed", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryOrange,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(30.dp)
                )
                FilterChip(
                    selected = uiState.selectedStatus == IpoStatus.LISTED,
                    onClick = { viewModel.selectStatus(IpoStatus.LISTED) },
                    label = { Text("Listed", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryOrange,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(30.dp)
                )
            }

            // Market Type Filter (Mainboard / SME) & Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
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

                IpoSortDropdown(
                    selectedOption = uiState.sortOption,
                    onSortOptionSelected = { viewModel.setSortOption(it) }
                )
            }

            // Results Count & Active Sort Label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${uiState.filteredIpos.size} IPOs",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (uiState.lastSyncTime != null) {
                        Text(
                            text = " • Synced",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                if (uiState.sortOption != IpoSortOption.DEFAULT) {
                    Text(
                        text = "Sorted by: ${uiState.sortOption.displayName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242) // DARK GRAY ONLY
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Pull-To-Refresh container for IPO listing
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refreshData() },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("ipo_pull_to_refresh_box"),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = rememberPullToRefreshState(),
                        isRefreshing = uiState.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        color = PrimaryOrange
                    )
                }
            ) {
                if (uiState.filteredIpos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = "No IPOs Found",
                            message = "Try clearing filters, searching for another company, or pull down to refresh.",
                            actionLabel = "Clear Filters",
                            onActionClick = {
                                viewModel.selectStatus(null)
                                viewModel.setMarketType(mainboard = true, sme = true)
                                viewModel.setSortOption(IpoSortOption.DEFAULT)
                                viewModel.updateSearchQuery("")
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("ipo_lazy_column"),
                        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredIpos, key = { it.id }) { ipo ->
                            IpoCard(
                                ipo = ipo,
                                onIpoClick = onNavigateToDetail,
                                onWatchlistToggle = { viewModel.toggleWatchlist(it) },
                                onApplyClick = onNavigateToApplicationInfo,
                                onAllotmentClick = onNavigateToAllotment,
                                onWatchLiveClick = onNavigateToLiveMarket
                            )
                        }
                    }
                }
            }
        }
    }
}
