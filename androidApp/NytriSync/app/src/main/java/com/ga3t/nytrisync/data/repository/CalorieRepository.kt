package com.ga3t.nytrisync.data.repository

import com.ga3t.nytrisync.data.model.CalendarResponse
import com.ga3t.nytrisync.data.model.MainPageResponse
import com.ga3t.nytrisync.data.model.MealByDateResponse
import com.ga3t.nytrisync.data.model.MealDto
import com.ga3t.nytrisync.data.remote.CalorieApi
import java.math.BigDecimal

class CalorieRepository(private val api: CalorieApi) {

    suspend fun getMainPage(): Result<MainPageResponse> = try {
        val resp = api.mainPage()
        if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
        else Result.failure(IllegalStateException("Main page failed (${resp.code()})"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getMealByDate(date: String, mealType: String): Result<MealByDateResponse> = try {
        val resp = api.showMeal(date = date, mealType = mealType)
        if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
        else Result.failure(IllegalStateException("Show meal failed (${resp.code()})"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveMeal(date: String, mealType: String, dto: MealDto): Result<String> = try {
        val resp = api.saveMeal(mealType = mealType, dateTime = date, body = dto)
        if (resp.isSuccessful) Result.success(resp.body().orEmpty())
        else Result.failure(IllegalStateException(resp.errorBody()?.string().orEmpty()
            .ifBlank { "Save failed (${resp.code()})" }))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addWater(date: String, amountMl: BigDecimal): Result<BigDecimal> = try {
        val resp = api.addWater(date, amountMl)
        if (resp.isSuccessful) {
            Result.success(resp.body() ?: BigDecimal.ZERO)
        } else {
            Result.failure(IllegalStateException(resp.errorBody()?.string().orEmpty().ifBlank { "Add water error (${resp.code()})" }))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCalendar(year: Int): Result<List<CalendarResponse.CaloryDays>> {
        return try {
            val response = api.getCalendar(year)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.calendar)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getPageByDate(date: String): Result<MainPageResponse> {
        return try {
            val response = api.getPageByDate(date)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}