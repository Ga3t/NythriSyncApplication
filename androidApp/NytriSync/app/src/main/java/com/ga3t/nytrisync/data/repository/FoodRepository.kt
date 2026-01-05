package com.ga3t.nytrisync.data.repository

import com.ga3t.nytrisync.data.model.FoodDataResponse
import com.ga3t.nytrisync.data.model.FoodResponse
import com.ga3t.nytrisync.data.model.FoodSearchResponse
import com.ga3t.nytrisync.data.remote.FoodSecretApi
import com.ga3t.nytrisync.data.remote.ProductApi

class FoodRepository(
    private val foodApi: FoodSecretApi,
    private val productApi: ProductApi
) {
    suspend fun search(query: String, page: Int): Result<FoodSearchResponse> = try {
        val resp = foodApi.search(query, page)
        if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
        else Result.failure(IllegalStateException("Search failed (${resp.code()})"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun details(id: String): Result<FoodResponse> = try {
        val resp = foodApi.details(id)
        if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
        else Result.failure(IllegalStateException("Details failed (${resp.code()})"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun byBarcode(barcode: String): Result<FoodDataResponse> = try {
        val resp = productApi.findByBarcode(barcode)
        if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
        else Result.failure(IllegalStateException("Barcode failed (${resp.code()})"))
    } catch (e: Exception) { Result.failure(e) }
}