package com.ga3t.nytrisync.data.remote
import com.ga3t.nytrisync.data.model.ReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
interface AnalyseApi {
    @GET("analyse/reportforweek")
    suspend fun reportForWeek(): Response<ReportResponse>
    @GET("analyse/reports")
    suspend fun reportInRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<ReportResponse>
}