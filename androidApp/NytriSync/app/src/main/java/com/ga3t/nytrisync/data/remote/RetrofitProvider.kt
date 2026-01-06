package com.ga3t.nytrisync.data.remote
import com.ga3t.nytrisync.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
object RetrofitProvider {
    val userDetailsApi: UserDetailsApi by lazy {
        retrofit().create(UserDetailsApi::class.java)
    }
    val api: AuthApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor())
            .authenticator(TokenAuthenticator())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
    val calorieApi: CalorieApi by lazy {
        retrofit().create(CalorieApi::class.java)
    }
    val analyseApi: AnalyseApi by lazy {
        retrofit().create(AnalyseApi::class.java)
    }
    val foodSecretApi: FoodSecretApi by lazy {
        retrofit().create(FoodSecretApi::class.java) }
    val productApi: ProductApi by lazy {
        retrofit().create(ProductApi::class.java) }
    private fun retrofit(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor())
            .authenticator(TokenAuthenticator())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}