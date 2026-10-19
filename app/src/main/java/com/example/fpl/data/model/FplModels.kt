package com.example.fpl.data.model

import com.google.gson.annotations.SerializedName

data class BootstrapStaticResponse(
    @SerializedName("events") val events: List<EventDto> = emptyList(),
    @SerializedName("teams") val teams: List<TeamDto> = emptyList(),
    @SerializedName("elements") val elements: List<PlayerDto> = emptyList()
)

data class EventDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("deadline_time") val deadlineTime: String,
    @SerializedName("average_entry_score") val averageEntryScore: Int,
    @SerializedName("finished") val finished: Boolean,
    @SerializedName("is_current") val isCurrent: Boolean,
    @SerializedName("is_next") val isNext: Boolean,
    @SerializedName("highest_score") val highestScore: Int?
)

data class TeamDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("short_name") val shortName: String
)

data class PlayerDto(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("second_name") val secondName: String,
    @SerializedName("web_name") val webName: String,
    @SerializedName("team") val teamId: Int,
    @SerializedName("element_type") val elementType: Int,
    @SerializedName("now_cost") val nowCost: Int,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("event_points") val eventPoints: Int,
    @SerializedName("form") val form: String,
    @SerializedName("selected_by_percent") val selectedByPercent: String,
    @SerializedName("minutes") val minutes: Int,
    @SerializedName("goals_scored") val goalsScored: Int,
    @SerializedName("assists") val assists: Int,
    @SerializedName("clean_sheets") val cleanSheets: Int,
    @SerializedName("points_per_game") val pointsPerGame: String,
    @SerializedName("ict_index") val ictIndex: String,
    @SerializedName("status") val status: String,
    @SerializedName("news") val news: String
) {
    val formattedPrice: String
        get() = "£${nowCost / 10.0}m"

    val positionName: String
        get() = when (elementType) {
            1 -> "GKP"
            2 -> "DEF"
            3 -> "MID"
            4 -> "FWD"
            else -> "UNK"
        }
}

data class ElementSummaryResponse(
    @SerializedName("history") val history: List<PlayerHistoryDto> = emptyList()
)

data class PlayerHistoryDto(
    @SerializedName("round") val round: Int,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("minutes") val minutes: Int,
    @SerializedName("goals_scored") val goalsScored: Int,
    @SerializedName("assists") val assists: Int,
    @SerializedName("clean_sheets") val cleanSheets: Int,
    @SerializedName("opponent_team") val opponentTeam: Int,
    @SerializedName("was_home") val wasHome: Boolean
)

data class FixtureDto(
    @SerializedName("id") val id: Int,
    @SerializedName("event") val event: Int?,
    @SerializedName("kickoff_time") val kickoffTime: String?,
    @SerializedName("team_h") val teamH: Int,
    @SerializedName("team_a") val teamA: Int,
    @SerializedName("team_h_score") val teamHScore: Int?,
    @SerializedName("team_a_score") val teamAScore: Int?,
    @SerializedName("started") val started: Boolean?,
    @SerializedName("finished") val finished: Boolean?
)

data class ManagerEntryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val teamName: String,
    @SerializedName("player_first_name") val playerFirstName: String,
    @SerializedName("player_last_name") val playerLastName: String,
    @SerializedName("summary_overall_points") val summaryOverallPoints: Int,
    @SerializedName("summary_overall_rank") val summaryOverallRank: Int?,
    @SerializedName("summary_event_points") val summaryEventPoints: Int,
    @SerializedName("current_event") val currentEvent: Int?
)

data class ManagerPicksResponse(
    @SerializedName("entry_history") val entryHistory: EntryHistoryDto,
    @SerializedName("picks") val picks: List<PickDto> = emptyList()
)

data class EntryHistoryDto(
    @SerializedName("event") val event: Int,
    @SerializedName("points") val points: Int,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("rank") val rank: Int?,
    @SerializedName("bank") val bank: Int,
    @SerializedName("value") val value: Int,
    @SerializedName("event_transfers") val eventTransfers: Int,
    @SerializedName("event_transfers_cost") val eventTransfersCost: Int,
    @SerializedName("points_on_bench") val pointsOnBench: Int
)

data class PickDto(
    @SerializedName("element") val element: Int,
    @SerializedName("position") val position: Int,
    @SerializedName("multiplier") val multiplier: Int,
    @SerializedName("is_captain") val isCaptain: Boolean,
    @SerializedName("is_vice_captain") val isViceCaptain: Boolean
)
