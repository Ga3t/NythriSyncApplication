package com.ga3t.nytrisync.data.remote
import com.ga3t.nytrisync.data.model.FoodDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
interface ProductApi {
    @GET("product/findbybarcode/{barcode}")
    suspend fun findByBarcode(@Path("barcode") barcode: String): Response<FoodDataResponse>
}