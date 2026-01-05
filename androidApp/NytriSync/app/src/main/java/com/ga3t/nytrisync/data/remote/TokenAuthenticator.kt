package com.ga3t.nytrisync.data.remote

import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Route
import okhttp3.Response as OkHttpResponse
import okhttp3.logging.HttpLoggingInterceptor
import android.util.Log
import com.google.gson.Gson
import com.ga3t.nytrisync.BuildConfig
import com.ga3t.nytrisync.data.local.TokenStorage
import com.ga3t.nytrisync.data.model.AuthResponseDto
import com.ga3t.nytrisync.utils.SessionEvents

class TokenAuthenticator : Authenticator {

    private val gson = Gson()
    private val refreshClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor { msg -> Log.d("HTTP-REFRESH", msg) }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    @Synchronized
    override fun authenticate(route: Route?, response: OkHttpResponse): Request? {
        Log.d("AUTH", "authenticate for ${response.request.url} code=${response.code}")

        if (responseCount(response) >= 2) return null

        val refresh = TokenStorage.getRefresh() ?: run {
            Log.d("AUTH", "no refresh token in storage")
            return null
        }

        val currentJwt = TokenStorage.getJwt()
        val reqJwt = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()
        if (!currentJwt.isNullOrBlank() && currentJwt != reqJwt) {
            Log.d("AUTH", "jwt rotated, retry with current")
            return response.request.newBuilder()
                .header("Authorization", "Bearer $currentJwt")
                .build()
        }

        val base = BuildConfig.BASE_URL.trimEnd('/')
        val url = "$base/auth/refreshtoken"

        val refreshRequest = Request.Builder()
            .url(url)
            .get()
            .header("Cookie", "refresh_token=$refresh")
            .build()

        val rr = try {
            refreshClient.newCall(refreshRequest).execute()
        } catch (e: Exception) {
            Log.e("AUTH", "refresh call failed", e)
            return null
        }

        rr.use {
            if (!it.isSuccessful) {
                Log.d("AUTH", "refresh failed code=${it.code}")
                TokenStorage.clear()
                SessionEvents.fireLogout()
                return null
            }
            val dto = runCatching {
                gson.fromJson(it.body?.string().orEmpty(), AuthResponseDto::class.java)
            }.getOrNull()

            if (dto == null) {
                TokenStorage.clear()
                SessionEvents.fireLogout()
                return null
            }

            Log.d("AUTH", "refresh success, saving tokens")
            TokenStorage.save(dto.jwtToken, dto.refreshToken)

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${dto.jwtToken}")
                .build()
        }
    }

    private fun responseCount(response: OkHttpResponse): Int {
        var r: OkHttpResponse? = response
        var count = 1
        while (r?.priorResponse != null) {
            count++
            r = r.priorResponse
        }
        return count
    }
}