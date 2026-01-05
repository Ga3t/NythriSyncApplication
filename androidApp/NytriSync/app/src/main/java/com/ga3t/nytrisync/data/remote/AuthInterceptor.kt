package com.ga3t.nytrisync.data.remote


import okhttp3.Interceptor
import okhttp3.Response as OkHttpResponse
import com.ga3t.nytrisync.data.local.TokenStorage

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): OkHttpResponse {
        val req = chain.request()
        val path = req.url.encodedPath
        if (path.endsWith("/auth/login") ||
            path.endsWith("/auth/registration") ||
            path.endsWith("/auth/refreshtoken")
        ) {
            return chain.proceed(req)
        }
        val jwt = TokenStorage.getJwt()
        val newReq = if (!jwt.isNullOrBlank()) {
            req.newBuilder().header("Authorization", "Bearer $jwt").build()
        } else req
        return chain.proceed(newReq)
    }
}