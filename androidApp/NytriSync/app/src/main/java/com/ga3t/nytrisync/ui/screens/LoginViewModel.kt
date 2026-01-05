package com.ga3t.nytrisync.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ga3t.nytrisync.data.model.AuthResponseDto
import com.ga3t.nytrisync.data.remote.RetrofitProvider
import com.ga3t.nytrisync.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onLoginChange(v: String) { uiState = uiState.copy(login = v, error = null) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v, error = null) }

    fun submit(onSuccess: () -> Unit) {
        if (uiState.login.isBlank()) { uiState = uiState.copy(error = "Please enter username"); return }
        if (uiState.password.isBlank()) { uiState = uiState.copy(error = "Please enter password"); return }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val res: Result<AuthResponseDto> = repo.login(uiState.login.trim(), uiState.password)

            uiState = uiState.copy(isLoading = false)

            res.fold(
                onSuccess = { _: AuthResponseDto ->
                    onSuccess()
                },
                onFailure = { e: Throwable ->
                    uiState = uiState.copy(error = e.message ?: "Login error")
                }
            )
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = AuthRepository(RetrofitProvider.api)
                return LoginViewModel(repo) as T
            }
        }
    }
}