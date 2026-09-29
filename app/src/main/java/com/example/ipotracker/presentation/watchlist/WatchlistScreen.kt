package com.example.ipotracker.presentation.watchlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.presentation.components.EmptyState
import com.example.ipotracker.presentation.components.IpoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToApplicationInfo: (String) -> Unit = {},
    onNavigateToAllotment: (String) -> Unit = {},
    onNavigateToLiveMarket: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Watchlist (${uiState.watchlistIpos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (uiState.watchlistIpos.isEmpty()) {
            EmptyState(
                title = "Your Watchlist is Empty",
                message = "Track upcoming, live, and listed IPOs by tapping the bookmark icon on any card.",
                actionLabel = "Explore IPOs",
                onActionClick = onNavigateToExplore,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.watchlistIpos, key = { it.id }) { ipo ->
                    IpoCard(
                        ipo = ipo,
                        onIpoClick = onNavigateToDetail,
                        onWatchlistToggle = { viewModel.removeFromWatchlist(it) },
                        onApplyClick = onNavigateToApplicationInfo,
                        onAllotmentClick = onNavigateToAllotment,
                        onWatchLiveClick = onNavigateToLiveMarket
                    )
                }
            }
        }
    }
}
