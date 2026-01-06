package com.ga3t.nytrisync.data.remote
import com.ga3t.nytrisync.data.model.UpdateUserDetailsDto
import com.ga3t.nytrisync.data.model.UserDetailsDto
import com.ga3t.nytrisync.data.model.UserDetailsResponse
import com.ga3t.nytrisync.data.model.UserInfoResponse
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal
interface UserDetailsApi {
    @GET("userdetails/userdetailsexists")
    suspend fun userDetailsExists(): Response<Boolean>
    @POST("userdetails/setuserdetails")
    suspend fun setUserDetails(
        @Body body: UserDetailsDto): Response<UserDetailsResponse>
    @GET("userdetails/info")
    suspend fun userInfo(): Response<UserInfoResponse>
    @GET("userdetails/newWeighing")
    suspend fun setNewWeighing(@Query("new_weight") newWeight: BigDecimal): Response<BigDecimal>
    @POST("userdetails/updateuserdetails")
    suspend fun updateUserDetails(@Body dto: UpdateUserDetailsDto): Response<String>
}