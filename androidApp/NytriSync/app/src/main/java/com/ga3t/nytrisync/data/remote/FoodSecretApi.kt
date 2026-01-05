package com.ga3t.nytrisync.data.remote

import com.ga3t.nytrisync.data.model.FoodResponse
import com.ga3t.nytrisync.data.model.FoodSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodSecretApi {
    @GET("foodsecret/search")
    suspend fun search(
        @Query("search_expression") query: String,
        @Query("page") page: Int = 0
    ): Response<FoodSearchResponse>

    @GET("foodsecret/details")
    suspend fun details(
        @Query("id") id: String
    ): Response<FoodResponse>
}