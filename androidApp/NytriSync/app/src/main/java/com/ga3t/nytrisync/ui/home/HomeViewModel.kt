package com.ga3t.nytrisync.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ga3t.nytrisync.data.model.MainPageResponse
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.CalorieRepository
import com.ga3t.nytrisync.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val data: MainPageResponse? = null,
    val requireOnboarding: Boolean = false,
    val greeting: String = greetingByTime()
) {
    companion object {
        private fun greetingByTime(): String {
            val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (h) {
                in 5..11 -> "Good Morning"
                in 12..16 -> "Good Afternoon"
                in 17..21 -> "Good Evening"
                else -> "Good Night"
            }
        }
    }
}

class HomeViewModel(
    private val calorieRepo: CalorieRepository,
    private val detailsRepo: UserDetailsRepository
) : ViewModel() {

    var ui by mutableStateOf(HomeUiState())
        private set

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        ui = ui.copy(loading = true, error = null, requireOnboarding = false)
        val existsRes = detailsRepo.exists()
        if (existsRes.isFailure) {
            ui = ui.copy(loading = false, error = existsRes.exceptionOrNull()?.message ?: "Check failed")
            return@launch
        }
        val exists = existsRes.getOrNull() == true
        if (!exists) {
            ui = ui.copy(loading = false, requireOnboarding = true, error = "Please complete your profile")
            return@launch
        }

        val mainRes = calorieRepo.getMainPage()
        ui = ui.copy(loading = false)
        mainRes.onSuccess { data -> ui = ui.copy(data = data, error = null) }
            .onFailure { e -> ui = ui.copy(error = e.message ?: "Load failed") }
    }

    fun clearOnboardingFlag() { ui = ui.copy(requireOnboarding = false) }

    fun addWater(amountMl: Int) = viewModelScope.launch {
        if (amountMl <= 0) return@launch
        val date = java.time.LocalDate.now().toString()
        val res = calorieRepo.addWater(date, java.math.BigDecimal(amountMl))
        res.onSuccess {
            refresh()
        }.onFailure { e ->
            ui = ui.copy(error = e.message ?: "Failed to add water")
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val calorieRepo = CalorieRepository(RetrofitProvider.calorieApi)
                val detailsRepo = UserDetailsRepository(RetrofitProvider.userDetailsApi)
                return HomeViewModel(calorieRepo, detailsRepo) as T
            }
        }
    }
}