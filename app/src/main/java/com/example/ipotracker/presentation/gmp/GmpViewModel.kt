package com.example.ipotracker.presentation.gmp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GmpUiState(
    val ipos: List<IpoItem> = emptyList(),
    val filteredIpos: List<IpoItem> = emptyList(),
    val isMainboardSelected: Boolean = true,
    val isSmeSelected: Boolean = false,
    val selectedIpoIdForChart: String? = null,
    val isLoading: Boolean = false
)

class GmpViewModel(private val repository: IpoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(GmpUiState(isLoading = true))
    val uiState: StateFlow<GmpUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAllIpos().collect { list ->
                // Filter IPOs that have GMP data or are open/upcoming
                val gmpList = list.filter { it.gmpHistory.isNotEmpty() || it.currentGmp > 0 }
                    .sortedByDescending { it.currentGmp }

                _uiState.update { current ->
                    val filtered = applyMarketFilter(
                        list = gmpList,
                        isMainboard = current.isMainboardSelected,
                        isSme = current.isSmeSelected
                    )

                    current.copy(
                        ipos = gmpList,
                        filteredIpos = filtered,
                        selectedIpoIdForChart = current.selectedIpoIdForChart ?: gmpList.firstOrNull()?.id,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setMarketType(mainboard: Boolean, sme: Boolean) {
        if (!mainboard && !sme) return
        _uiState.update { current ->
            val filtered = applyMarketFilter(
                list = current.ipos,
                isMainboard = mainboard,
                isSme = sme
            )
            current.copy(
                isMainboardSelected = mainboard,
                isSmeSelected = sme,
                filteredIpos = filtered
            )
        }
    }

    private fun applyMarketFilter(list: List<IpoItem>, isMainboard: Boolean, isSme: Boolean): List<IpoItem> {
        return if (isMainboard && !isSme) {
            list.filter { it.category == IpoCategory.MAINBOARD }
        } else if (!isMainboard && isSme) {
            list.filter { it.category == IpoCategory.SME }
        } else {
            list
        }
    }

    fun selectIpoForChart(ipoId: String) {
        _uiState.update { it.copy(selectedIpoIdForChart = ipoId) }
    }

    companion object {
        fun provideFactory(repository: IpoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GmpViewModel(repository) as T
                }
            }
    }
}
