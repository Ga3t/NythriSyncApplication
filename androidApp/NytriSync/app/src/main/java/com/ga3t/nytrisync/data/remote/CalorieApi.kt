package com.ga3t.nytrisync.data.remote
import com.ga3t.nytrisync.data.model.CalendarResponse
import com.ga3t.nytrisync.data.model.MainPageResponse
import com.ga3t.nytrisync.data.model.MealByDateResponse
import com.ga3t.nytrisync.data.model.MealDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.math.BigDecimal
interface CalorieApi {
    @GET("calapp/mainpage")
    suspend fun mainPage(): Response<MainPageResponse>
    @GET("calapp/showmeal")
    suspend fun showMeal(
        @Query("date") date: String,
        @Query("MealType") mealType: String
    ): Response<MealByDateResponse>
    @POST("calapp/savemeal")
    suspend fun saveMeal(
        @Query("MealType") mealType: String,
        @Query("DateTime") dateTime: String,
        @Body body: MealDto
    ): Response<String>
    @POST("calapp/addwater")
    suspend fun addWater(
        @Query("Date") date: String,
        @Query("Water-To-Add") water: BigDecimal
    ): Response<BigDecimal>
    @GET("calapp/calendar")
    suspend fun getCalendar(
        @Query("year") year: Int
    ): Response<CalendarResponse>
    @GET("calapp/pageByDate")
    suspend fun getPageByDate(
        @Query("Date") date: String
    ): Response<MainPageResponse>
}