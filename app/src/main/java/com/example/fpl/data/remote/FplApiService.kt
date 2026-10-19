package com.example.fpl.data.remote

import com.example.fpl.data.model.BootstrapStaticResponse
import com.example.fpl.data.model.ElementSummaryResponse
import com.example.fpl.data.model.FixtureDto
import com.example.fpl.data.model.LeagueStandingsResponse
import com.example.fpl.data.model.ManagerEntryDto
import com.example.fpl.data.model.ManagerPicksResponse
import com.example.fpl.data.model.MeResponse
import com.example.fpl.data.model.TransferRequestPayload
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

class SessionCookieJar : CookieJar {
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val list = cookieStore.getOrPut(url.host) { mutableListOf() }
        cookies.forEach { newCookie ->
            list.removeAll { it.name == newCookie.name }
            list.add(newCookie)
        }
        // Share Premier League auth cookies across subdomains
        if (url.host.contains("premierleague.com")) {
            val shared = cookieStore.getOrPut("premierleague.com") { mutableListOf() }
            cookies.forEach { newCookie ->
                shared.removeAll { it.name == newCookie.name }
                shared.add(newCookie)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val result = mutableListOf<Cookie>()
        cookieStore[url.host]?.let { result.addAll(it) }
        if (url.host.contains("premierleague.com")) {
            cookieStore["premierleague.com"]?.forEach { sharedCookie ->
                if (result.none { it.name == sharedCookie.name }) {
                    result.add(sharedCookie)
                }
            }
        }
        return result
    }

    fun hasSessionCookies(): Boolean {
        return cookieStore.values.flatten().any {
            it.name.contains("pl_profile", ignoreCase = true) ||
                it.name.contains("session", ignoreCase = true)
        }
    }

    fun clear() {
        cookieStore.clear()
    }
}

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

    @GET("leagues-classic/{leagueId}/standings/")
    suspend fun getClassicLeagueStandings(
        @Path("leagueId") leagueId: Int,
        @Query("page_standings") page: Int = 1
    ): LeagueStandingsResponse

    @FormUrlEncoded
    @POST
    suspend fun loginPremierLeague(
        @Url loginUrl: String = "https://users.premierleague.com/accounts/login/",
        @Field("login") email: String,
        @Field("password") password: String,
        @Field("app") app: String = "plfpl-web",
        @Field("redirect_uri") redirectUri: String = "https://fantasy.premierleague.com/a/login"
    ): Response<ResponseBody>

    @GET("me/")
    suspend fun getAuthenticatedUser(): MeResponse

    @POST("transfers/")
    suspend fun postTransfers(
        @Body payload: TransferRequestPayload
    ): Response<ResponseBody>

    companion object {
        private const val BASE_URL = "https://fantasy.premierleague.com/api/"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"

        val cookieJar = SessionCookieJar()

        fun create(): FplApiService {
            val client = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "application/json, text/plain, */*")
                        .header("Origin", "https://fantasy.premierleague.com")
                        .header("Referer", "https://fantasy.premierleague.com/")
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
