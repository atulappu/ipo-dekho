package com.example.ipotracker.presentation.allotment

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

data class AllotmentUiState(
    val ipos: List<IpoItem> = emptyList(),
    val selectedIpo: IpoItem? = null,
    val panNumber: String = "",
    val applicationNumber: String = "",
    val registrarName: String = "",
    val registrarUrl: String = "https://ris.kfintech.com/ipostatus/",
    val isAllotmentOut: Boolean = false,
    val infoMessage: String? = null
)

class AllotmentViewModel(
    private val repository: IpoRepository,
    initialIpoId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllotmentUiState())
    val uiState: StateFlow<AllotmentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllIpos().collect { list ->
                val ipo = if (initialIpoId != null) {
                    list.find { it.id == initialIpoId }
                } else {
                    list.find { it.allotmentInfo?.isAvailable == true } ?: list.firstOrNull()
                }

                _uiState.update { current ->
                    current.copy(
                        ipos = list,
                        selectedIpo = ipo,
                        registrarName = ipo?.allotmentInfo?.registrarName ?: ipo?.registrar ?: "Official Registrar",
                        registrarUrl = ipo?.allotmentInfo?.registrarUrl ?: "https://ris.kfintech.com/ipostatus/",
                        isAllotmentOut = ipo?.allotmentInfo?.isAvailable == true
                    )
                }
            }
        }
    }

    fun selectIpo(ipo: IpoItem) {
        _uiState.update {
            it.copy(
                selectedIpo = ipo,
                registrarName = ipo.allotmentInfo?.registrarName ?: ipo.registrar,
                registrarUrl = ipo.allotmentInfo?.registrarUrl ?: "https://ris.kfintech.com/ipostatus/",
                isAllotmentOut = ipo.allotmentInfo?.isAvailable == true
            )
        }
    }

    fun updatePan(pan: String) {
        _uiState.update { it.copy(panNumber = pan.uppercase().take(10)) }
    }

    fun updateApplicationNumber(appNum: String) {
        _uiState.update { it.copy(applicationNumber = appNum) }
    }

    companion object {
        fun provideFactory(repository: IpoRepository, initialIpoId: String? = null): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AllotmentViewModel(repository, initialIpoId) as T
                }
            }
    }
}
