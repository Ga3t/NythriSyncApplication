package com.ga3t.nytrisync.data.repository

import com.ga3t.nytrisync.data.local.TokenStorage
import com.ga3t.nytrisync.data.model.AuthResponseDto
import com.ga3t.nytrisync.data.model.LoginDto
import com.ga3t.nytrisync.data.model.RegistrationDto
import com.ga3t.nytrisync.data.remote.AuthApi

class AuthRepository(private val api: AuthApi) {

    suspend fun register(username: String, email: String, password: String): Result<String> {
        return try {
            val resp = api.registration(RegistrationDto(username = username, password = password, email = email))
            if (resp.isSuccessful) {
                Result.success(resp.body().orEmpty().ifBlank { "Registration successful" })
            } else {
                Result.failure(IllegalStateException(resp.errorBody()?.string().orEmpty()
                    .ifBlank { "Registration error (${resp.code()})" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(login: String, password: String): Result<AuthResponseDto> {
        return try {
            val resp = api.login(LoginDto(login = login, password = password))
            if (resp.isSuccessful) {
                val dto = resp.body()!!
                TokenStorage.save(dto.jwtToken, dto.refreshToken)
                Result.success(dto)
            } else {
                Result.failure(IllegalStateException(resp.errorBody()?.string().orEmpty()
                    .ifBlank { "Login error (${resp.code()})" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        TokenStorage.clear()
    }
}