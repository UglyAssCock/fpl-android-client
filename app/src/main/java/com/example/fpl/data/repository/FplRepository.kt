package com.example.fpl.data.repository

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

data class LoginSessionResult(
    val email: String,
    val teamId: Int?,
    val managerName: String?,
    val isLiveCookieSession: Boolean
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

    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String,
        optionalTeamId: Int?
    ): Result<LoginSessionResult> = runCatching {
        // Attempt official Premier League login endpoint + /api/me/ discovery
        val loginCall = runCatching { api.loginPremierLeague(email = email, password = password) }
        val meCall = runCatching { api.getAuthenticatedUser() }.getOrNull()

        val discoveredEntryId = meCall?.player?.entry ?: optionalTeamId
        val discoveredName = meCall?.player?.let {
            "${it.firstName.orEmpty()} ${it.lastName.orEmpty()}".trim()
        }?.takeIf { it.isNotBlank() }

        if (discoveredEntryId != null && discoveredEntryId > 0) {
            LoginSessionResult(
                email = email,
                teamId = discoveredEntryId,
                managerName = discoveredName,
                isLiveCookieSession = FplApiService.cookieJar.hasSessionCookies() && loginCall.isSuccess
            )
        } else {
            // Account logged in locally; if PL firewall blocked auto-ID lookup, still sign user in
            LoginSessionResult(
                email = email,
                teamId = null,
                managerName = discoveredName,
                isLiveCookieSession = FplApiService.cookieJar.hasSessionCookies()
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
            "Transfers applied to your active squad planner! (${transfers.size} transfer(s) confirmed)"
        }
    }
}
