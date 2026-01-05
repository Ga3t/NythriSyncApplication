package com.ga3t.nytrisync.ui.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ga3t.nytrisync.data.model.CalendarResponse
import com.ga3t.nytrisync.data.repository.CalorieRepository
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(private val repository: CalorieRepository) : ViewModel() {

    var uiState by mutableStateOf(CalendarUiState())
        private set

    // Состояние текущего выбранного года
    var selectedYear by mutableIntStateOf(LocalDate.now().year)
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = null)
            val result = repository.getCalendar(selectedYear)
            result.onSuccess { list ->
                uiState = uiState.copy(loading = false, calendarData = list)
            }.onFailure {
                uiState = uiState.copy(loading = false, error = it.message)
            }
        }
    }

    fun nextYear() {
        selectedYear++
        loadData()
    }

    fun prevYear() {
        selectedYear--
        loadData()
    }

    data class CalendarUiState(
        val loading: Boolean = false,
        val error: String? = null,
        val calendarData: List<CalendarResponse.CaloryDays> = emptyList()
    )

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CalendarViewModel(CalorieRepository(RetrofitProvider.calorieApi)) as T
            }
        }
    }
}