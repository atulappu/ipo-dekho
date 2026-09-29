package com.example.ipotracker.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ipotracker.presentation.components.EmptyState
import com.example.ipotracker.presentation.components.IpoCard
import com.example.ui.theme.PrimaryOrange
import com.example.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToApplicationInfo: (String) -> Unit = {},
    onNavigateToAllotment: (String) -> Unit = {},
    onNavigateToLiveMarket: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text("Search by company, symbol or sector...", fontSize = 13.sp) },
                        singleLine = true,
                        trailingIcon = {
                            if (uiState.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            viewModel.submitSearch(uiState.query)
                            keyboardController?.hide()
                        }),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Recent Searches Chips when query is empty
            if (uiState.query.isEmpty() && uiState.recentSearches.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT SEARCHES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = { viewModel.clearAllRecentSearches() },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Clear All", fontSize = 11.sp, color = PrimaryOrange)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.recentSearches) { queryItem ->
                            InputChip(
                                selected = false,
                                onClick = { viewModel.selectQuery(queryItem) },
                                label = { Text(queryItem) },
                                leadingIcon = { Icon(Icons.Outlined.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { viewModel.deleteRecentSearch(queryItem) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(12.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Results List
            if (uiState.query.isNotBlank() && uiState.searchResults.isEmpty() && !uiState.isSearching) {
                EmptyState(
                    title = "No Matches Found",
                    message = "We couldn't find any IPO matching \"${uiState.query}\". Check the spelling or search by sector."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.searchResults, key = { it.id }) { ipo ->
                        IpoCard(
                            ipo = ipo,
                            onIpoClick = { id ->
                                viewModel.submitSearch(uiState.query)
                                onNavigateToDetail(id)
                            },
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
