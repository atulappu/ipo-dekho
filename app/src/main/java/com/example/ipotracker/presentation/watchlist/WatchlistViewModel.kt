package com.example.ipotracker.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WatchlistUiState(
    val watchlistIpos: List<IpoItem> = emptyList(),
    val isLoading: Boolean = false
)

class WatchlistViewModel(private val repository: IpoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState(isLoading = true))
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    init {
        loadWatchlist()
    }

    private fun loadWatchlist() {
        viewModelScope.launch {
            repository.getWatchlist().collect { list ->
                _uiState.value = WatchlistUiState(
                    watchlistIpos = list,
                    isLoading = false
                )
            }
        }
    }

    fun removeFromWatchlist(ipoId: String) {
        viewModelScope.launch {
            repository.toggleWatchlist(ipoId)
        }
    }

    companion object {
        fun provideFactory(repository: IpoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WatchlistViewModel(repository) as T
                }
            }
    }
}
