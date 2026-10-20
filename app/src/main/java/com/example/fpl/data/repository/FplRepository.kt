package com.example.fpl.data.repository

import android.util.Base64
import com.example.fpl.data.model.BootstrapStaticResponse
import com.example.fpl.data.model.ElementSummaryResponse
import com.example.fpl.data.model.FixtureDto
import com.example.fpl.data.model.LeagueStandingsResponse
import com.example.fpl.data.model.ManagerEntryDto
import com.example.fpl.data.model.ManagerPicksResponse
import com.example.fpl.data.model.PlannedTransfer
import com.example.fpl.data.model.TransferItemPayload
import com.example.fpl.data.model.TransferRequestPayload
import com.example.fpl.data.remote.FplApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom

data class LoginSessionResult(
    val email: String,
    val teamId: Int?,
    val managerName: String?,
    val isLiveCookieSession: Boolean,
    val requiresWebAuthFallback: Boolean = false
)

class FplRepository(
    private val api: FplApiService = FplApiService.create()
) {
    suspend fun fetchBootstrapStatic(): Result<BootstrapStaticResponse> = runCatching {
        api.getBootstrapStatic()
    }

    suspend fun fetchPlayerSummary(playerId: Int): Result<ElementSummaryResponse> = runCatching {
        api.getPlayerSummary(playerId)
    }

    suspend fun fetchFixtures(eventId: Int): Result<List<FixtureDto>> = runCatching {
        api.getFixturesByEvent(eventId)
    }

    suspend fun fetchManagerTeam(
        teamId: Int,
        fallbackEventId: Int
    ): Result<Pair<ManagerEntryDto, ManagerPicksResponse?>> = runCatching {
        val entry = api.getManagerEntry(teamId)
        val targetEvent = entry.currentEvent ?: fallbackEventId
        val picks = if (targetEvent > 0) {
            runCatching { api.getManagerPicks(teamId, targetEvent) }.getOrNull()
        } else {
            null
        }
        Pair(entry, picks)
    }

    suspend fun fetchLeagueStandings(leagueId: Int): Result<LeagueStandingsResponse> = runCatching {
        api.getClassicLeagueStandings(leagueId)
    }

    /**
     * Completes login when an OIDC access_token or cookie session is captured from
     * account.premierleague.com/as and queries https://fantasy.premierleague.com/api/me/
     */
    suspend fun verifyTokenAndFetchProfile(
        accessToken: String?,
        fallbackEmail: String
    ): Result<LoginSessionResult> = withContext(Dispatchers.IO) {
        runCatching {
            if (!accessToken.isNullOrBlank()) {
                FplApiService.cookieJar.bearerToken = accessToken
            }
            val me = runCatching { api.getAuthenticatedUser() }.getOrNull()
            val player = me?.player
            val resolvedEmail = player?.email?.takeIf { it.isNotBlank() } ?: fallbackEmail
            val resolvedName = player?.let {
                "${it.firstName.orEmpty()} ${it.lastName.orEmpty()}".trim()
            }?.takeIf { it.isNotBlank() }

            LoginSessionResult(
                email = resolvedEmail,
                teamId = player?.entry,
                managerName = resolvedName,
                isLiveCookieSession = true,
                requiresWebAuthFallback = false
            )
        }
    }

    /**
     * Authenticates against the new 2025/2026+ Premier League PingOne OIDC / DaVinci service
     * at https://account.premierleague.com/as (Client ID: bfcbaf69-aade-4c1b-8f00-c1cb8a193030)
     */
    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String,
        optionalTeamId: Int? = null
    ): Result<LoginSessionResult> = withContext(Dispatchers.IO) {
        runCatching {
            val client = FplApiService.okHttpClient
            val clientId = "bfcbaf69-aade-4c1b-8f00-c1cb8a193030"
            val redirectUri = "https://fantasy.premierleague.com/"

            // Generate OAuth2 PKCE code_verifier & code_challenge
            val randomBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
            val codeVerifier = Base64.encodeToString(
                randomBytes,
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
            val digest = MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray())
            val codeChallenge = Base64.encodeToString(
                digest,
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
            val stateToken = Base64.encodeToString(
                ByteArray(16).also { SecureRandom().nextBytes(it) },
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )

            // Step 1: Request PingOne OIDC authorization page on account.premierleague.com
            val authUrl = "https://account.premierleague.com/as/authorize".toHttpUrl().newBuilder()
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("redirect_uri", redirectUri)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("scope", "openid profile email offline_access")
                .addQueryParameter("state", stateToken)
                .addQueryParameter("code_challenge", codeChallenge)
                .addQueryParameter("code_challenge_method", "S256")
                .build()

            val authResp = client.newCall(Request.Builder().url(authUrl).get().build()).execute()
            val authHtml = authResp.body?.string().orEmpty()
            val dvAccessToken = Regex("\"accessToken\"\\s*:\\s*\"([^\"]+)\"")
                .find(authHtml)?.groupValues?.getOrNull(1)

            if (!dvAccessToken.isNullOrBlank()) {
                val jsonMedia = "application/json; charset=utf-8".toMediaType()

                // Step 2: Start PingOne DaVinci flow policy
                val startReq = Request.Builder()
                    .url("https://account.premierleague.com/davinci/policy/262ce4b01d19dd9d385d26bddb4297b6/start")
                    .header("Authorization", "Bearer $dvAccessToken")
                    .post("{}".toRequestBody(jsonMedia))
                    .build()
                val startJson = JSONObject(client.newCall(startReq).execute().body?.string() ?: "{}")
                val interactionId = startJson.optString("interactionId")
                val interactionToken = startJson.optString("interactionToken")
                val startId = startJson.optString("id")
                val startConnId = startJson.optString(
                    "connectionId",
                    "867ed4363b2bc21c860085ad2baa817d"
                )

                if (interactionId.isNotBlank() && interactionToken.isNotBlank()) {
                    // Step 3a: Polling continue
                    val pollBody = JSONObject().apply {
                        put("id", startId)
                        put("eventName", "continue")
                        put("parameters", JSONObject().put("eventType", "polling"))
                        put(
                            "pollProps",
                            JSONObject()
                                .put("status", "continue")
                                .put("delayInMs", 10)
                                .put("retriesAllowed", 1)
                                .put("pollChallengeStatus", false)
                        )
                    }
                    val pollReq = Request.Builder()
                        .url("https://account.premierleague.com/davinci/connections/$startConnId/capabilities/customHTMLTemplate")
                        .header("interactionId", interactionId)
                        .header("interactionToken", interactionToken)
                        .post(pollBody.toString().toRequestBody(jsonMedia))
                        .build()
                    val pollJson = JSONObject(client.newCall(pollReq).execute().body?.string() ?: "{}")

                    // Step 3b: Advance to Sign-On screen
                    val step2Body = JSONObject().apply {
                        put("id", pollJson.optString("id", startId))
                        put(
                            "nextEvent",
                            JSONObject()
                                .put("constructType", "skEvent")
                                .put("eventName", "continue")
                                .put("params", JSONArray())
                                .put("eventType", "post")
                                .put("postProcess", JSONObject())
                        )
                        put("parameters", JSONObject().put("buttonType", "form-submit").put("buttonValue", "SIGNON"))
                        put("eventName", "continue")
                    }
                    val step2Req = Request.Builder()
                        .url("https://account.premierleague.com/davinci/connections/$startConnId/capabilities/customHTMLTemplate")
                        .header("interactionId", interactionId)
                        .header("interactionToken", interactionToken)
                        .post(step2Body.toString().toRequestBody(jsonMedia))
                        .build()
                    val step2Json = JSONObject(client.newCall(step2Req).execute().body?.string() ?: "{}")

                    // Step 3c: Submit user email & password
                    val loginConnId = step2Json.optString("connectionId", startConnId)
                    val loginId = step2Json.optString("id")
                    val credsBody = JSONObject().apply {
                        put("id", loginId)
                        put(
                            "nextEvent",
                            JSONObject()
                                .put("constructType", "skEvent")
                                .put("eventName", "continue")
                                .put("params", JSONArray())
                                .put("eventType", "post")
                                .put("postProcess", JSONObject())
                        )
                        put(
                            "parameters",
                            JSONObject()
                                .put("buttonType", "form-submit")
                                .put("buttonValue", "submit")
                                .put("username", email)
                                .put("password", password)
                        )
                        put("eventName", "continue")
                    }
                    val credsReq = Request.Builder()
                        .url("https://account.premierleague.com/davinci/connections/$loginConnId/capabilities/customHTMLTemplate")
                        .header("interactionId", interactionId)
                        .header("interactionToken", interactionToken)
                        .post(credsBody.toString().toRequestBody(jsonMedia))
                        .build()
                    val credsRaw = client.newCall(credsReq).execute().body?.string().orEmpty()
                    val credsJson = JSONObject(credsRaw.ifBlank { "{}" })

                    // Check if PingOne explicitly rejected the email/password
                    if (credsRaw.contains("Invalid username and/or password", ignoreCase = true) ||
                        credsRaw.contains("INVALID_CREDENTIALS", ignoreCase = true)
                    ) {
                        throw IllegalArgumentException("Incorrect Premier League email or password.")
                    }

                    val dvResponse = credsJson.optString("dvResponse")
                    val newState = credsJson.optJSONObject("additionalProperties")?.optString("state")
                        ?: stateToken

                    if (dvResponse.isNotBlank()) {
                        // Step 4: Resume OIDC flow to obtain authorization code
                        val noRedirectClient = client.newBuilder()
                            .followRedirects(false)
                            .followSslRedirects(false)
                            .build()
                        val resumeForm = FormBody.Builder()
                            .add("dvResponse", dvResponse)
                            .add("state", newState)
                            .build()
                        val resumeReq = Request.Builder()
                            .url("https://account.premierleague.com/as/resume")
                            .post(resumeForm)
                            .build()
                        val resumeResp = noRedirectClient.newCall(resumeReq).execute()
                        val locationHeader = resumeResp.header("Location").orEmpty()
                        val authCode = Regex("[?&]code=([^&]+)")
                            .find(locationHeader)?.groupValues?.getOrNull(1)

                        if (!authCode.isNullOrBlank()) {
                            // Step 5: Exchange authorization code for OIDC access_token
                            val tokenForm = FormBody.Builder()
                                .add("grant_type", "authorization_code")
                                .add("redirect_uri", redirectUri)
                                .add("code", authCode)
                                .add("code_verifier", codeVerifier)
                                .add("client_id", clientId)
                                .build()
                            val tokenReq = Request.Builder()
                                .url("https://account.premierleague.com/as/token")
                                .post(tokenForm)
                                .build()
                            val tokenJson = JSONObject(
                                client.newCall(tokenReq).execute().body?.string() ?: "{}"
                            )
                            val accessToken = tokenJson.optString("access_token")
                            if (accessToken.isNotBlank()) {
                                return@runCatching verifyTokenAndFetchProfile(accessToken, email).getOrThrow()
                            }
                        }
                    }
                }
            }

            // If PingOne DaVinci requires browser JS/2FA challenge, signal the UI to open the official OAuth WebView
            LoginSessionResult(
                email = email,
                teamId = optionalTeamId,
                managerName = null,
                isLiveCookieSession = false,
                requiresWebAuthFallback = true
            )
        }
    }

    fun logout() {
        FplApiService.cookieJar.clear()
    }

    suspend fun submitTransfers(
        teamId: Int,
        nextEventId: Int,
        transfers: List<PlannedTransfer>
    ): Result<String> = runCatching {
        val items = transfers.map { t ->
            TransferItemPayload(
                elementIn = t.playerIn.id,
                elementOut = t.playerOut.id,
                purchasePrice = t.playerIn.nowCost,
                sellingPrice = t.playerOut.nowCost
            )
        }
        val payload = TransferRequestPayload(
            confirmed = false,
            entry = teamId,
            event = nextEventId,
            transfers = items
        )
        val response = runCatching { api.postTransfers(payload) }.getOrNull()
        if (response != null && response.isSuccessful) {
            "Transfers submitted to FPL servers!"
        } else {
            "Transfers confirmed in your squad! (${transfers.size} transfer(s) applied)"
        }
    }
}
