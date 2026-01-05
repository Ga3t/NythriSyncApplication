package com.ga3t.nytrisync.data.repository

import com.ga3t.nytrisync.data.model.ReportResponse
import com.ga3t.nytrisync.data.remote.AnalyseApi

class AnalyseRepository(private val api: AnalyseApi) {
    suspend fun week(): Result<ReportResponse> = runCatching {
        val r = api.reportForWeek()
        if (r.isSuccessful) r.body()!! else error(r.errorBody()?.string() ?: "Week report error ${r.code()}")
    }

    suspend fun range(startDate: String, endDate: String): Result<ReportResponse> = runCatching {
        val r = api.reportInRange(startDate, endDate)
        if (r.isSuccessful) r.body()!! else error(r.errorBody()?.string() ?: "Range report error ${r.code()}")
    }
}