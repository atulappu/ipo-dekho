package com.example.ipotracker.presentation.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SubscriptionUiState(
    val ipos: List<IpoItem> = emptyList(),
    val filteredIpos: List<IpoItem> = emptyList(),
    val selectedIpo: IpoItem? = null,
    val categoryFilter: IpoCategory? = null,
    val useCompactShares: Boolean = true,
    val selectedTab: Int = 0, // 0 = Subscription Overview, 1 = Day 1-3 Trend, 2 = Category Multiples
    val isLoading: Boolean = false
)

class SubscriptionViewModel(
    private val repository: IpoRepository,
    initialIpoId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState(isLoading = true))
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    init {
        loadData(initialIpoId)
    }

    private fun loadData(initialIpoId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAllIpos().collect { list ->
                // Sort IPOs: Open first, then with subscription details, then Closed
                val sorted = list.sortedWith(
                    compareByDescending<IpoItem> { it.status == IpoStatus.OPEN }
                        .thenByDescending { it.subscriptionDetails != null }
                        .thenByDescending { it.currentSubscriptionTimes }
                )

                val selected = if (initialIpoId != null) {
                    sorted.find { it.id == initialIpoId } ?: sorted.firstOrNull()
                } else {
                    sorted.firstOrNull { it.status == IpoStatus.OPEN && it.subscriptionDetails != null }
                        ?: sorted.firstOrNull { it.subscriptionDetails != null }
                        ?: sorted.firstOrNull()
                }

                _uiState.update { current ->
                    val filtered = applyFilter(sorted, current.categoryFilter)
                    current.copy(
                        ipos = sorted,
                        filteredIpos = filtered,
                        selectedIpo = selected,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectIpo(ipoId: String) {
        val target = _uiState.value.ipos.find { it.id == ipoId }
        if (target != null) {
            _uiState.update { it.copy(selectedIpo = target) }
        }
    }

    fun setCategoryFilter(category: IpoCategory?) {
        _uiState.update { current ->
            val filtered = applyFilter(current.ipos, category)
            val currentSelected = current.selectedIpo
            val newSelected = if (currentSelected != null && filtered.any { it.id == currentSelected.id }) {
                currentSelected
            } else {
                filtered.firstOrNull()
            }
            current.copy(
                categoryFilter = category,
                filteredIpos = filtered,
                selectedIpo = newSelected
            )
        }
    }

    fun toggleShareDisplayMode() {
        _uiState.update { it.copy(useCompactShares = !it.useCompactShares) }
    }

    fun setSelectedTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    private fun applyFilter(list: List<IpoItem>, category: IpoCategory?): List<IpoItem> {
        return if (category == null) list else list.filter { it.category == category }
    }

    companion object {
        fun provideFactory(
            repository: IpoRepository,
            initialIpoId: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SubscriptionViewModel(repository, initialIpoId) as T
            }
        }
    }
}
