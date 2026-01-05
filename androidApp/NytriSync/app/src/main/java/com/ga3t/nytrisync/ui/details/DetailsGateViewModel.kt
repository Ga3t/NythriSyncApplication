package com.ga3t.nytrisync.ui.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch

data class DetailsGateState(
    val loading: Boolean = true,
    val exists: Boolean? = null,
    val error: String? = null
)

class DetailsGateViewModel(
    private val repo: UserDetailsRepository
) : ViewModel() {

    var state by mutableStateOf(DetailsGateState())
        private set

    init { check() }

    fun check() = viewModelScope.launch {
        state = state.copy(loading = true, error = null)
        val res = repo.exists()
        state = state.copy(loading = false)
        res.onSuccess { exists -> state = state.copy(exists = exists) }
            .onFailure { e -> state = state.copy(error = e.message ?: "Error") }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = UserDetailsRepository(RetrofitProvider.userDetailsApi)
                return DetailsGateViewModel(repo) as T
            }
        }
    }
}