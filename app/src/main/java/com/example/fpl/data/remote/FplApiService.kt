package com.example.fpl.data.remote

import com.example.fpl.data.model.BootstrapStaticResponse
import com.example.fpl.data.model.ElementSummaryResponse
import com.example.fpl.data.model.FixtureDto
import com.example.fpl.data.model.ManagerEntryDto
import com.example.fpl.data.model.ManagerPicksResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FplApiService {

    @GET("bootstrap-static/")
    suspend fun getBootstrapStatic(): BootstrapStaticResponse

    @GET("element-summary/{elementId}/")
    suspend fun getPlayerSummary(
        @Path("elementId") elementId: Int
    ): ElementSummaryResponse

    @GET("fixtures/")
    suspend fun getFixturesByEvent(
        @Query("event") eventId: Int
    ): List<FixtureDto>

    @GET("entry/{teamId}/")
    suspend fun getManagerEntry(
        @Path("teamId") teamId: Int
    ): ManagerEntryDto

    @GET("entry/{teamId}/event/{eventId}/picks/")
    suspend fun getManagerPicks(
        @Path("teamId") teamId: Int,
        @Path("eventId") eventId: Int
    ): ManagerPicksResponse

    companion object {
        private const val BASE_URL = "https://fantasy.premierleague.com/api/"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"

        fun create(): FplApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FplApiService::class.java)
        }
    }
}
