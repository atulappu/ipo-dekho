package com.example.ipotracker.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class IpoDetailUiState(
    val ipo: IpoItem? = null,
    val selectedTab: Int = 0,
    val showSharesInSubscription: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

class IpoDetailViewModel(
    private val repository: IpoRepository,
    private val ipoId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(IpoDetailUiState(isLoading = true))
    val uiState: StateFlow<IpoDetailUiState> = _uiState.asStateFlow()

    init {
        loadIpo()
    }

    private fun loadIpo() {
        viewModelScope.launch {
            repository.getIpoById(ipoId).collect { item ->
                if (item != null) {
                    _uiState.update { it.copy(ipo = item, isLoading = false, error = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "IPO not found") }
                }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun toggleSubscriptionDisplayUnit() {
        _uiState.update { it.copy(showSharesInSubscription = !it.showSharesInSubscription) }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            repository.toggleWatchlist(ipoId)
        }
    }

    companion object {
        fun provideFactory(repository: IpoRepository, ipoId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IpoDetailViewModel(repository, ipoId) as T
                }
            }
    }
}
