package com.example.ipotracker.presentation.comparison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.domain.repository.IpoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComparisonUiState(
    val allIpos: List<IpoItem> = emptyList(),
    val selectedIpos: List<IpoItem> = emptyList()
)

class IpoComparisonViewModel(private val repository: IpoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllIpos().collect { list ->
                _uiState.update { current ->
                    val defaultSelection = if (current.selectedIpos.isEmpty()) {
                        list.take(3)
                    } else current.selectedIpos

                    current.copy(
                        allIpos = list,
                        selectedIpos = defaultSelection
                    )
                }
            }
        }
    }

    fun toggleIpoSelection(ipo: IpoItem) {
        _uiState.update { current ->
            val list = current.selectedIpos.toMutableList()
            if (list.any { it.id == ipo.id }) {
                if (list.size > 2) {
                    list.removeAll { it.id == ipo.id }
                }
            } else {
                if (list.size < 4) {
                    list.add(ipo)
                }
            }
            current.copy(selectedIpos = list)
        }
    }

    companion object {
        fun provideFactory(repository: IpoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IpoComparisonViewModel(repository) as T
                }
            }
    }
}
