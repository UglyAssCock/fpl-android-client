package com.example.fpl.data.repository

import com.example.fpl.data.model.BootstrapStaticResponse
import com.example.fpl.data.model.ElementSummaryResponse
import com.example.fpl.data.model.FixtureDto
import com.example.fpl.data.model.ManagerEntryDto
import com.example.fpl.data.model.ManagerPicksResponse
import com.example.fpl.data.remote.FplApiService

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
}
