package com.example.ipotracker.presentation.ipo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.ipotracker.utils.applyIpoSorting

import com.example.ipotracker.utils.DateUtils

enum class IpoSortOption(val displayName: String) {
    DEFAULT("Default"),
    GMP_HIGH_TO_LOW("GMP – High to Low"),
    CLOSE_DATE("Close Date"),
    NAME_A_TO_Z("Name – A to Z"),
    NAME_Z_TO_A("Name – Z to A")
}

data class IpoListUiState(
    val allIpos: List<IpoItem> = emptyList(),
    val filteredIpos: List<IpoItem> = emptyList(),
    val selectedStatus: IpoStatus? = null,
    val isMainboardSelected: Boolean = true,
    val isSmeSelected: Boolean = false,
    val searchQuery: String = "",
    val sortOption: IpoSortOption = IpoSortOption.DEFAULT,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String? = null,
    val syncErrorMessage: String? = null
)

class IpoListViewModel(
    private val repository: IpoRepository,
    initialStatus: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        IpoListUiState(
            isLoading = true,
            selectedStatus = initialStatus?.let {
                try { IpoStatus.valueOf(it.uppercase()) } catch (e: Exception) { null }
            }
        )
    )
    val uiState: StateFlow<IpoListUiState> = _uiState.asStateFlow()

    init {
        loadIpos()
    }

    private fun loadIpos() {
        viewModelScope.launch {
            repository.getAllIpos().collect { ipos ->
                _uiState.update { currentState ->
                    val filtered = applyFilters(
                        ipos = ipos,
                        status = currentState.selectedStatus,
                        isMainboard = currentState.isMainboardSelected,
                        isSme = currentState.isSmeSelected,
                        query = currentState.searchQuery,
                        sort = currentState.sortOption
                    )
                    currentState.copy(
                        allIpos = ipos,
                        filteredIpos = filtered,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectStatus(status: IpoStatus?) {
        _uiState.update { currentState ->
            val newStatus = if (currentState.selectedStatus == status) null else status
            val filtered = applyFilters(
                ipos = currentState.allIpos,
                status = newStatus,
                isMainboard = currentState.isMainboardSelected,
                isSme = currentState.isSmeSelected,
                query = currentState.searchQuery,
                sort = currentState.sortOption
            )
            currentState.copy(selectedStatus = newStatus, filteredIpos = filtered)
        }
    }

    fun setMarketType(mainboard: Boolean, sme: Boolean) {
        // Enforce rule: never allow both false
        if (!mainboard && !sme) return
        _uiState.update { currentState ->
            val filtered = applyFilters(
                ipos = currentState.allIpos,
                status = currentState.selectedStatus,
                isMainboard = mainboard,
                isSme = sme,
                query = currentState.searchQuery,
                sort = currentState.sortOption
            )
            currentState.copy(
                isMainboardSelected = mainboard,
                isSmeSelected = sme,
                filteredIpos = filtered
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            val filtered = applyFilters(
                ipos = currentState.allIpos,
                status = currentState.selectedStatus,
                isMainboard = currentState.isMainboardSelected,
                isSme = currentState.isSmeSelected,
                query = query,
                sort = currentState.sortOption
            )
            currentState.copy(searchQuery = query, filteredIpos = filtered)
        }
    }

    fun setSortOption(sort: IpoSortOption) {
        _uiState.update { currentState ->
            val filtered = applyFilters(
                ipos = currentState.allIpos,
                status = currentState.selectedStatus,
                isMainboard = currentState.isMainboardSelected,
                isSme = currentState.isSmeSelected,
                query = currentState.searchQuery,
                sort = sort
            )
            currentState.copy(sortOption = sort, filteredIpos = filtered)
        }
    }

    fun toggleWatchlist(ipoId: String) {
        viewModelScope.launch {
            repository.toggleWatchlist(ipoId)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, syncErrorMessage = null) }
            val result = repository.refreshData()
            _uiState.update { currentState ->
                currentState.copy(
                    isRefreshing = false,
                    lastSyncTime = DateUtils.formatDisplayDateTime(),
                    syncErrorMessage = if (result.isFailure) result.exceptionOrNull()?.message ?: "Failed to sync IPO data" else null
                )
            }
        }
    }

    fun clearSyncError() {
        _uiState.update { it.copy(syncErrorMessage = null) }
    }

    private fun applyFilters(
        ipos: List<IpoItem>,
        status: IpoStatus?,
        isMainboard: Boolean,
        isSme: Boolean,
        query: String,
        sort: IpoSortOption
    ): List<IpoItem> {
        var result = ipos

        if (status != null) {
            result = result.filter { it.status == status }
        }

        // Market Type filter:
        // Mainboard = ON, SME = OFF -> only Mainboard
        // Mainboard = OFF, SME = ON -> only SME
        // Mainboard = ON, SME = ON -> both
        if (isMainboard && !isSme) {
            result = result.filter { it.category == IpoCategory.MAINBOARD }
        } else if (!isMainboard && isSme) {
            result = result.filter { it.category == IpoCategory.SME }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.name.lowercase().contains(q) ||
                it.symbol.lowercase().contains(q) ||
                it.sector.lowercase().contains(q)
            }
        }

        return result.applyIpoSorting(sort)
    }

    companion object {
        fun provideFactory(repository: IpoRepository, initialStatus: String? = null): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IpoListViewModel(repository, initialStatus) as T
                }
            }
    }
}
