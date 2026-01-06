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
data class RegistrationUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
class RegistrationViewModel(
    private val repo: AuthRepository
) : ViewModel() {
    var uiState by mutableStateOf(RegistrationUiState())
        private set
    fun onUsernameChange(v: String) { uiState = uiState.copy(username = v, error = null) }
    fun onEmailChange(v: String) { uiState = uiState.copy(email = v, error = null) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v, error = null) }
    fun submit(onAutoLoginSuccess: () -> Unit) {
        val u = uiState.username.trim()
        val e = uiState.email.trim()
        val p = uiState.password
        if (u.isBlank()) { uiState = uiState.copy(error = "Please enter username"); return }
        if (e.isBlank() || !e.contains("@")) { uiState = uiState.copy(error = "Please enter a valid email"); return }
        if (p.isBlank()) { uiState = uiState.copy(error = "Please enter password"); return }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val reg: Result<String> = repo.register(u, e, p)
            if (reg.isFailure) {
                uiState = uiState.copy(isLoading = false, error = reg.exceptionOrNull()?.message ?: "Registration error")
                return@launch
            }
            val loginRes: Result<AuthResponseDto> = repo.login(u, p)
            uiState = uiState.copy(isLoading = false)
            loginRes.fold(
                onSuccess = { onAutoLoginSuccess() },
                onFailure = { err -> uiState = uiState.copy(error = err.message ?: "Auto-login error") }
            )
        }
    }
    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = com.ga3t.nytrisync.data.repository.AuthRepository(RetrofitProvider.api)
                return RegistrationViewModel(repo) as T
            }
        }
    }
}