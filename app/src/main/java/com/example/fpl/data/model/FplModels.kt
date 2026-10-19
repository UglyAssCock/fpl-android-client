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
    @SerializedName("short_name") val shortName: String,
    @SerializedName("code") val code: Int = 0
)

data class PlayerDto(
    @SerializedName("id") val id: Int,
    @SerializedName("code") val code: Int = 0,
    @SerializedName("photo") val photo: String = "",
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

    val photoUrl: String
        get() {
            val cleanCode = if (code != 0) code.toString() else photo.substringBefore(".")
            return "https://resources.premierleague.com/premierleague/photos/players/110x140/p$cleanCode.png"
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

// Manager Entry + Leagues + Cups
data class ManagerEntryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val teamName: String,
    @SerializedName("player_first_name") val playerFirstName: String,
    @SerializedName("player_last_name") val playerLastName: String,
    @SerializedName("summary_overall_points") val summaryOverallPoints: Int,
    @SerializedName("summary_overall_rank") val summaryOverallRank: Int?,
    @SerializedName("summary_event_points") val summaryEventPoints: Int?,
    @SerializedName("current_event") val currentEvent: Int?,
    @SerializedName("last_deadline_bank") val lastDeadlineBank: Int?,
    @SerializedName("last_deadline_value") val lastDeadlineValue: Int?,
    @SerializedName("leagues") val leagues: ManagerLeaguesDto?
)

data class ManagerLeaguesDto(
    @SerializedName("classic") val classic: List<ClassicLeagueInfoDto> = emptyList(),
    @SerializedName("h2h") val h2h: List<ClassicLeagueInfoDto> = emptyList(),
    @SerializedName("cup") val cup: CupDto? = null,
    @SerializedName("cup_matches") val cupMatches: List<CupMatchDto> = emptyList()
)

data class ClassicLeagueInfoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("league_type") val leagueType: String?,
    @SerializedName("entry_rank") val entryRank: Int?,
    @SerializedName("entry_last_rank") val entryLastRank: Int?
)

data class CupDto(
    @SerializedName("matches") val matches: List<CupMatchDto> = emptyList(),
    @SerializedName("status") val status: CupStatusDto? = null
)

data class CupStatusDto(
    @SerializedName("qualification_event") val qualificationEvent: Int?,
    @SerializedName("qualification_numbers") val qualificationNumbers: Int?,
    @SerializedName("qualification_rank") val qualificationRank: Int?,
    @SerializedName("qualification_state") val qualificationState: String?
)

data class CupMatchDto(
    @SerializedName("id") val id: Int,
    @SerializedName("event") val event: Int,
    @SerializedName("entry_1_entry") val entry1Id: Int,
    @SerializedName("entry_1_name") val entry1Name: String,
    @SerializedName("entry_1_player_name") val entry1PlayerName: String,
    @SerializedName("entry_1_points") val entry1Points: Int,
    @SerializedName("entry_2_entry") val entry2Id: Int,
    @SerializedName("entry_2_name") val entry2Name: String,
    @SerializedName("entry_2_player_name") val entry2PlayerName: String,
    @SerializedName("entry_2_points") val entry2Points: Int,
    @SerializedName("winner") val winner: Int?
)

// Classic League Standings
data class LeagueStandingsResponse(
    @SerializedName("league") val league: LeagueMetaDto,
    @SerializedName("standings") val standings: StandingsPageDto
)

data class LeagueMetaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class StandingsPageDto(
    @SerializedName("has_next") val hasNext: Boolean = false,
    @SerializedName("results") val results: List<StandingResultDto> = emptyList()
)

data class StandingResultDto(
    @SerializedName("id") val id: Int,
    @SerializedName("entry") val entry: Int,
    @SerializedName("entry_name") val entryName: String,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("rank") val rank: Int,
    @SerializedName("last_rank") val lastRank: Int,
    @SerializedName("event_total") val eventTotal: Int,
    @SerializedName("total") val total: Int
)

// Squad Picks
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

// Login & Authenticated User Endpoint (/api/me/)
data class MeResponse(
    @SerializedName("player") val player: MePlayerDto?
)

data class MePlayerDto(
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("entry") val entry: Int?,
    @SerializedName("email") val email: String?
)

// Transfers Models
data class PlannedTransfer(
    val playerOut: PlayerDto,
    val playerIn: PlayerDto
)

data class TransferRequestPayload(
    @SerializedName("confirmed") val confirmed: Boolean = false,
    @SerializedName("entry") val entry: Int,
    @SerializedName("event") val event: Int,
    @SerializedName(" transfers") val transfers: List<TransferItemPayload>,
    @SerializedName("wildcard") val wildcard: Boolean = false,
    @SerializedName("freehit") val freehit: Boolean = false
)

data class TransferItemPayload(
    @SerializedName("element_in") val elementIn: Int,
    @SerializedName("element_out") val elementOut: Int,
    @SerializedName("purchase_price") val purchasePrice: Int,
    @SerializedName("selling_price") val sellingPrice: Int
)
