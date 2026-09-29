package com.example.ipotracker.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val searchResults: List<IpoItem> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false
)

@OptIn(FlowPreview::class)
class SearchViewModel(private val repository: IpoRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Observe recent searches
        viewModelScope.launch {
            repository.getRecentSearches().collect { recents ->
                _uiState.update { it.copy(recentSearches = recents) }
            }
        }

        // Debounced search
        viewModelScope.launch {
            _query
                .debounce(250)
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isBlank()) flowOf(emptyList())
                    else repository.searchIpos(q)
                }
                .collect { results ->
                    _uiState.update {
                        it.copy(
                            searchResults = results,
                            isSearching = false
                        )
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        _uiState.update { it.copy(query = newQuery, isSearching = newQuery.isNotBlank()) }
    }

    fun selectQuery(selectedQuery: String) {
        onQueryChange(selectedQuery)
    }

    fun submitSearch(finalQuery: String) {
        if (finalQuery.isNotBlank()) {
            viewModelScope.launch {
                repository.saveSearchQuery(finalQuery)
            }
        }
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            repository.deleteSearchQuery(query)
        }
    }

    fun clearAllRecentSearches() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    fun toggleWatchlist(ipoId: String) {
        viewModelScope.launch {
            repository.toggleWatchlist(ipoId)
        }
    }

    companion object {
        fun provideFactory(repository: IpoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(repository) as T
                }
            }
    }
}
