package com.example.ipotracker.presentation.calculator

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

data class CalculatorUiState(
    val ipos: List<IpoItem> = emptyList(),
    val selectedIpo: IpoItem? = null,
    val ipoPrice: Double = 510.0,
    val lotSize: Int = 29,
    val numberOfLots: Int = 1,
    val gmp: Double = 125.0,
    val totalShares: Long = 29,
    val investmentAmount: Double = 14790.0,
    val estimatedListingPrice: Double = 635.0,
    val estimatedGainPerShare: Double = 125.0,
    val estimatedGainPerLot: Double = 3625.0,
    val estimatedTotalGain: Double = 3625.0,
    val estimatedGainPercent: Double = 24.51
)

class IpoCalculatorViewModel(
    private val repository: IpoRepository,
    initialIpoId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllIpos().collect { list ->
                val matchingIpo = if (initialIpoId != null) {
                    list.find { it.id == initialIpoId }
                } else {
                    list.firstOrNull { it.currentGmp > 0 } ?: list.firstOrNull()
                }

                _uiState.update { current ->
                    current.copy(
                        ipos = list,
                        selectedIpo = matchingIpo ?: current.selectedIpo
                    )
                }

                if (matchingIpo != null) {
                    selectIpo(matchingIpo)
                } else {
                    recalculate()
                }
            }
        }
    }

    fun selectIpo(ipo: IpoItem) {
        val price = if (ipo.priceBandMax > 0) ipo.priceBandMax else ipo.priceBandMin
        val lot = if (ipo.lotSize > 0) ipo.lotSize else 1
        val gmpVal = if (ipo.currentGmp > 0) ipo.currentGmp else 0.0

        _uiState.update {
            it.copy(
                selectedIpo = ipo,
                ipoPrice = price,
                lotSize = lot,
                gmp = gmpVal,
                numberOfLots = 1
            )
        }
        recalculate()
    }

    fun updatePrice(price: Double) {
        _uiState.update { it.copy(ipoPrice = price.coerceAtLeast(1.0)) }
        recalculate()
    }

    fun updateLotSize(lotSize: Int) {
        _uiState.update { it.copy(lotSize = lotSize.coerceAtLeast(1)) }
        recalculate()
    }

    fun updateNumberOfLots(lots: Int) {
        _uiState.update { it.copy(numberOfLots = lots.coerceAtLeast(1)) }
        recalculate()
    }

    fun updateGmp(gmp: Double) {
        _uiState.update { it.copy(gmp = gmp) }
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val totalShares = state.lotSize.toLong() * state.numberOfLots.toLong()
        val investment = state.ipoPrice * totalShares
        val listingPrice = state.ipoPrice + state.gmp
        val gainPerShare = state.gmp
        val gainPerLot = state.gmp * state.lotSize
        val totalGain = gainPerShare * totalShares
        val gainPercent = if (state.ipoPrice > 0) (gainPerShare / state.ipoPrice) * 100 else 0.0

        _uiState.update {
            it.copy(
                totalShares = totalShares,
                investmentAmount = investment,
                estimatedListingPrice = listingPrice,
                estimatedGainPerShare = gainPerShare,
                estimatedGainPerLot = gainPerLot,
                estimatedTotalGain = totalGain,
                estimatedGainPercent = gainPercent
            )
        }
    }

    companion object {
        fun provideFactory(repository: IpoRepository, initialIpoId: String? = null): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IpoCalculatorViewModel(repository, initialIpoId) as T
                }
            }
    }
}
