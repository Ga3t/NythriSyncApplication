package com.ga3t.nytrisync.data.repository
import com.ga3t.nytrisync.data.local.TokenStorage
import com.ga3t.nytrisync.data.model.UpdateUserDetailsDto
import com.ga3t.nytrisync.data.model.UserDetailsDto
import com.ga3t.nytrisync.data.model.UserDetailsResponse
import com.ga3t.nytrisync.data.model.UserInfoResponse
import com.ga3t.nytrisync.data.remote.UserDetailsApi
import java.math.BigDecimal
class UserDetailsRepository(private val api: UserDetailsApi) {
    suspend fun exists(): Result<Boolean> = try {
        val resp = api.userDetailsExists()
        if (resp.isSuccessful) Result.success(resp.body() == true)
        else Result.failure(IllegalStateException("Exists check failed (${resp.code()})"))
    } catch (e: Exception) {
        Result.failure(e)
    }
    suspend fun setDetails(dto: UserDetailsDto): Result<UserDetailsResponse> = try {
        val resp = api.setUserDetails(dto)
        if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
        else Result.failure(IllegalStateException(resp.errorBody()?.string().orEmpty()
            .ifBlank { "Set details failed (${resp.code()})" }))
    } catch (e: Exception) {
        Result.failure(e)
    }
    suspend fun userInfo(): Result<UserInfoResponse> = runCatching {
        val r = api.userInfo()
        if (r.isSuccessful) r.body()!! else error(r.errorBody()?.string() ?: "User info error ${r.code()}")
    }
    suspend fun setNewWeighing(newWeight: BigDecimal): Result<BigDecimal> = runCatching {
        val r = api.setNewWeighing(newWeight)
        if (r.isSuccessful) r.body()!! else error(r.errorBody()?.string() ?: "New weighing error ${r.code()}")
    }
    suspend fun updateUserDetails(dto: UpdateUserDetailsDto): Result<String> = runCatching {
        val r = api.updateUserDetails(dto)
        if (r.isSuccessful) (r.body() ?: "OK") else error(r.errorBody()?.string() ?: "Update error ${r.code()}")
    }
}