package com.example.ipotracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.MarketIndex
import com.example.ipotracker.domain.repository.IpoRepository
import com.example.ipotracker.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val marketIndices: List<MarketIndex> = emptyList(),
    val openIpos: List<IpoItem> = emptyList(),
    val upcomingIpos: List<IpoItem> = emptyList(),
    val closedIpos: List<IpoItem> = emptyList(),
    val listedIpos: List<IpoItem> = emptyList(),
    val lastUpdated: String = DateUtils.formatLastUpdated(),
    val error: String? = null
)

class HomeViewModel(private val repository: IpoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            combine(
                repository.getMarketSummary(),
                repository.getOpenIpos(),
                repository.getUpcomingIpos(),
                repository.getClosedIpos(),
                repository.getListedIpos()
            ) { indices, open, upcoming, closed, listed ->
                HomeUiState(
                    isLoading = false,
                    marketIndices = indices,
                    openIpos = open,
                    upcomingIpos = upcoming,
                    closedIpos = closed,
                    listedIpos = listed,
                    lastUpdated = DateUtils.formatLastUpdated()
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Failed to load data") }
            }.collect { newState ->
                _uiState.value = newState
            }
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
                    return HomeViewModel(repository) as T
                }
            }
    }
}
