package com.example.ipotracker.presentation.calendar

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

data class CalendarEvent(
    val ipoId: String,
    val ipoName: String,
    val eventType: String, // "Bidding Opens", "Bidding Closes", "Allotment Date", "Listing Date"
    val dateStr: String,
    val colorType: String // "OPEN", "CLOSE", "ALLOTMENT", "LISTING"
)

data class CalendarUiState(
    val events: List<CalendarEvent> = emptyList(),
    val filteredEvents: List<CalendarEvent> = emptyList(),
    val selectedEventType: String? = null
)

class IpoCalendarViewModel(private val repository: IpoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            repository.getAllIpos().collect { list ->
                val allEvents = mutableListOf<CalendarEvent>()
                list.forEach { ipo ->
                    if (ipo.openDate.isNotBlank()) {
                        allEvents.add(CalendarEvent(ipo.id, ipo.name, "Bidding Opens", ipo.openDate, "OPEN"))
                    }
                    if (ipo.closeDate.isNotBlank()) {
                        allEvents.add(CalendarEvent(ipo.id, ipo.name, "Bidding Closes", ipo.closeDate, "CLOSE"))
                    }
                    if (ipo.allotmentDate.isNotBlank()) {
                        allEvents.add(CalendarEvent(ipo.id, ipo.name, "Allotment Basis", ipo.allotmentDate, "ALLOTMENT"))
                    }
                    if (ipo.listingDate.isNotBlank()) {
                        allEvents.add(CalendarEvent(ipo.id, ipo.name, "Stock Listing", ipo.listingDate, "LISTING"))
                    }
                }
                val sorted = allEvents.sortedBy { it.dateStr }

                _uiState.update { current ->
                    current.copy(
                        events = sorted,
                        filteredEvents = if (current.selectedEventType != null) {
                            sorted.filter { it.eventType == current.selectedEventType }
                        } else sorted
                    )
                }
            }
        }
    }

    fun selectEventType(type: String?) {
        _uiState.update { current ->
            val newType = if (current.selectedEventType == type) null else type
            current.copy(
                selectedEventType = newType,
                filteredEvents = if (newType != null) {
                    current.events.filter { it.eventType == newType }
                } else current.events
            )
        }
    }

    companion object {
        fun provideFactory(repository: IpoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IpoCalendarViewModel(repository) as T
                }
            }
    }
}
