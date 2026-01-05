package com.ga3t.nytrisync.data.remote

import com.ga3t.nytrisync.data.model.AuthResponseDto
import com.ga3t.nytrisync.data.model.LoginDto
import com.ga3t.nytrisync.data.model.RegistrationDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/registration")
    suspend fun registration(@Body body: RegistrationDto): Response<String>

    @POST("auth/login")
    suspend fun login(@Body body: LoginDto): Response<AuthResponseDto>

    @GET("auth/refreshtoken")
    suspend fun refreshToken(@Header("Cookie") cookie: String): Response<AuthResponseDto>
}