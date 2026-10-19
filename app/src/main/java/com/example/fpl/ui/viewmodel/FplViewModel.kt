package com.example.fpl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fpl.data.model.EventDto
import com.example.fpl.data.model.FixtureDto
import com.example.fpl.data.model.LeagueStandingsResponse
import com.example.fpl.data.model.ManagerEntryDto
import com.example.fpl.data.model.ManagerPicksResponse
import com.example.fpl.data.model.PickDto
import com.example.fpl.data.model.PlannedTransfer
import com.example.fpl.data.model.PlayerDto
import com.example.fpl.data.model.PlayerHistoryDto
import com.example.fpl.data.model.TeamDto
import com.example.fpl.data.repository.FplRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FplUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val players: List<PlayerDto> = emptyList(),
    val teamsById: Map<Int, TeamDto> = emptyMap(),
    val teams: List<TeamDto> = emptyList(),
    val events: List<EventDto> = emptyList(),
    val searchQuery: String = "",
    val selectedPosition: Int? = null,
    val selectedTeamId: Int? = null,
    val selectedEventId: Int = 1,
    val fixtures: List<FixtureDto> = emptyList(),
    val isFixturesLoading: Boolean = false,
    val playerHistory: List<PlayerHistoryDto> = emptyList(),
    val isPlayerHistoryLoading: Boolean = false,
    // Auth State
    val isLoggedIn: Boolean = false,
    val loggedInEmail: String = "",
    val isLoggingIn: Boolean = false,
    val loginError: String? = null,
    val showLoginDialog: Boolean = false,
    // My Team & Pitch State
    val teamIdInput: String = "",
    val isMyTeamLoading: Boolean = false,
    val myTeamError: String? = null,
    val managerEntry: ManagerEntryDto? = null,
    val managerPicks: ManagerPicksResponse? = null,
    val isPitchView: Boolean = true,
    // Transfers State
    val draftSquadIds: List<Int> = emptyList(),
    val plannedTransfers: List<PlannedTransfer> = emptyList(),
    val playerToTransferOut: PlayerDto? = null,
    val transferSearchQuery: String = "",
    val initialBankTenths: Int = 0,
    val freeTransfers: Int = 1,
    val transferStatusMessage: String? = null,
    // Leagues & Cups State
    val selectedLeagueStandings: LeagueStandingsResponse? = null,
    val isStandingsLoading: Boolean = false,
    val standingsError: String? = null,
    // Theme
    val isDarkMode: Boolean = true
) {
    val filteredPlayers: List<PlayerDto>
        get() = players
            .asSequence()
            .filter { player ->
                val matchesSearch = searchQuery.isBlank() ||
                    player.webName.contains(searchQuery, ignoreCase = true) ||
                    "${player.firstName} ${player.secondName}".contains(searchQuery, ignoreCase = true)
                val matchesPos = selectedPosition == null || player.elementType == selectedPosition
                val matchesTeam = selectedTeamId == null || player.teamId == selectedTeamId
                matchesSearch && matchesPos && matchesTeam
            }
            .sortedByDescending { it.totalPoints }
            .toList()

    val remainingBankTenths: Int
        get() {
            val delta = plannedTransfers.sumOf { it.playerOut.nowCost - it.playerIn.nowCost }
            return initialBankTenths + delta
        }

    val transferPointsCost: Int
        get() {
            val extra = (plannedTransfers.size - freeTransfers).coerceAtLeast(0)
            return extra * 4
        }
}

class FplViewModel(
    private val repository: FplRepository = FplRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FplUiState())
    val uiState: StateFlow<FplUiState> = _uiState.asStateFlow()

    init {
        loadBootstrapData()
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setPitchView(isPitch: Boolean) {
        _uiState.update { it.copy(isPitchView = isPitch) }
    }

    fun openLoginDialog(show: Boolean) {
        _uiState.update { it.copy(showLoginDialog = show, loginError = null) }
    }

    fun login(email: String, password: String, optionalTeamId: String) {
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(loginError = "Please enter a valid email address.") }
            return
        }
        if (password.length < 4) {
            _uiState.update { it.copy(loginError = "Please enter your password.") }
            return
        }

        val parsedTeamId = optionalTeamId.filter { it.isDigit() }.toIntOrNull()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingIn = true, loginError = null) }
            repository.loginWithEmailAndPassword(email.trim(), password, parsedTeamId)
                .onSuccess { session ->
                    val resolvedTeamId = session.teamId
                        ?: parsedTeamId
                        ?: _uiState.value.teamIdInput.toIntOrNull()

                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            isLoggedIn = true,
                            loggedInEmail = session.email,
                            showLoginDialog = false,
                            teamIdInput = resolvedTeamId?.toString() ?: it.teamIdInput
                        )
                    }
                    if (resolvedTeamId != null && resolvedTeamId > 0) {
                        loadMyTeamById(resolvedTeamId)
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            loginError = err.localizedMessage ?: "Login failed."
                        )
                    }
                }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                loggedInEmail = "",
                transferStatusMessage = null
            )
        }
    }

    fun loadBootstrapData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.fetchBootstrapStatic()
                .onSuccess { response ->
                    val currentGw = response.events.firstOrNull { it.isCurrent }?.id
                        ?: response.events.firstOrNull { it.isNext }?.id
                        ?: response.events.firstOrNull()?.id
                        ?: 1

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            players = response.elements,
                            teams = response.teams.sortedBy { t -> t.name },
                            teamsById = response.teams.associateBy { t -> t.id },
                            events = response.events,
                            selectedEventId = currentGw
                        )
                    }
                    loadFixturesForEvent(currentGw)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load FPL data."
                        )
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectPosition(position: Int?) {
        _uiState.update { it.copy(selectedPosition = position) }
    }

    fun selectTeam(teamId: Int?) {
        _uiState.update { it.copy(selectedTeamId = teamId) }
    }

    fun loadPlayerDetails(playerId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPlayerHistoryLoading = true, playerHistory = emptyList()) }
            repository.fetchPlayerSummary(playerId)
                .onSuccess { summary ->
                    _uiState.update {
                        it.copy(
                            isPlayerHistoryLoading = false,
                            playerHistory = summary.history.sortedByDescending { h -> h.round }
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isPlayerHistoryLoading = false) }
                }
        }
    }

    fun loadFixturesForEvent(eventId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedEventId = eventId, isFixturesLoading = true) }
            repository.fetchFixtures(eventId)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(isFixturesLoading = false, fixtures = list)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isFixturesLoading = false, fixtures = emptyList())
                    }
                }
        }
    }

    fun updateTeamIdInput(input: String) {
        _uiState.update { it.copy(teamIdInput = input.filter { ch -> ch.isDigit() }) }
    }

    fun loadMyTeam() {
        val id = _uiState.value.teamIdInput.toIntOrNull()
        if (id == null || id <= 0) {
            _uiState.update { it.copy(myTeamError = "Please enter a valid numeric FPL Team ID.") }
            return
        }
        loadMyTeamById(id)
    }

    fun loadMyTeamById(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    teamIdInput = id.toString(),
                    isMyTeamLoading = true,
                    myTeamError = null,
                    transferStatusMessage = null
                )
            }
            val fallbackGw = _uiState.value.selectedEventId
            repository.fetchManagerTeam(id, fallbackGw)
                .onSuccess { (entry, picks) ->
                    val squadIds = picks?.picks
                        ?.sortedBy { p -> p.position }
                        ?.map { p -> p.element }
                        .orEmpty()
                    val bank = picks?.entryHistory?.bank ?: entry.lastDeadlineBank ?: 0

                    _uiState.update {
                        it.copy(
                            isMyTeamLoading = false,
                            managerEntry = entry,
                            managerPicks = picks,
                            draftSquadIds = squadIds,
                            plannedTransfers = emptyList(),
                            playerToTransferOut = null,
                            initialBankTenths = bank
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isMyTeamLoading = false,
                            myTeamError = "Could not load Team ID $id: ${error.localizedMessage ?: "Check ID"}"
                        )
                    }
                }
        }
    }

    // Transfer Planner Functions
    fun generateSampleSquadIfEmpty() {
        if (_uiState.value.draftSquadIds.isNotEmpty()) return
        val all = _uiState.value.players.sortedByDescending { it.totalPoints }
        if (all.isEmpty()) return

        val selected = mutableListOf<PlayerDto>()
        val clubCounts = mutableMapOf<Int, Int>()

        fun pickPosition(elementType: Int, count: Int) {
            for (p in all) {
                if (selected.count { it.elementType == elementType } >= count) break
                if (p.elementType == elementType && (clubCounts[p.teamId] ?: 0) < 3) {
                    selected.add(p)
                    clubCounts[p.teamId] = (clubCounts[p.teamId] ?: 0) + 1
                }
            }
        }

        pickPosition(1, 2) // 2 GKP
        pickPosition(2, 5) // 5 DEF
        pickPosition(3, 5) // 5 MID
        pickPosition(4, 3) // 3 FWD

        _uiState.update {
            it.copy(
                draftSquadIds = selected.map { p -> p.id },
                initialBankTenths = 15, // £1.5m in bank
                plannedTransfers = emptyList(),
                transferStatusMessage = "Loaded top-form 15-player template squad for Transfer Planner."
            )
        }
    }

    fun selectPlayerForTransferOut(player: PlayerDto?) {
        _uiState.update {
            it.copy(
                playerToTransferOut = player,
                transferSearchQuery = "",
                transferStatusMessage = null
            )
        }
    }

    fun updateTransferSearchQuery(query: String) {
        _uiState.update { it.copy(transferSearchQuery = query) }
    }

    fun makeTransfer(playerIn: PlayerDto) {
        val currentState = _uiState.value
        val playerOut = currentState.playerToTransferOut ?: return

        if (playerIn.elementType != playerOut.elementType) {
            _uiState.update { it.copy(transferStatusMessage = "Replacement must play in the same position (${playerOut.positionName}).") }
            return
        }

        if (currentState.draftSquadIds.contains(playerIn.id)) {
            _uiState.update { it.copy(transferStatusMessage = "${playerIn.webName} is already in your squad.") }
            return
        }

        val playersById = currentState.players.associateBy { it.id }
        val remainingSquad = currentState.draftSquadIds
            .filter { it != playerOut.id }
            .mapNotNull { playersById[it] }

        val sameClubCount = remainingSquad.count { it.teamId == playerIn.teamId }
        if (sameClubCount >= 3) {
            val clubName = currentState.teamsById[playerIn.teamId]?.name ?: "that club"
            _uiState.update {
                it.copy(transferStatusMessage = "Club limit reached: You already have 3 players from $clubName.")
            }
            return
        }

        val updatedSquadIds = currentState.draftSquadIds.map { id ->
            if (id == playerOut.id) playerIn.id else id
        }
        val updatedTransfers = currentState.plannedTransfers + PlannedTransfer(
            playerOut = playerOut,
            playerIn = playerIn
        )

        // Also update managerPicks preview so Pitch View reflects the new signing immediately
        val updatedPicks = currentState.managerPicks?.let { mp ->
            mp.copy(
                picks = mp.picks.map { pick ->
                    if (pick.element == playerOut.id) pick.copy(element = playerIn.id) else pick
                }
            )
        }

        _uiState.update {
            it.copy(
                draftSquadIds = updatedSquadIds,
                plannedTransfers = updatedTransfers,
                managerPicks = updatedPicks,
                playerToTransferOut = null,
                transferStatusMessage = "Transferred OUT ${playerOut.webName} -> IN ${playerIn.webName}"
            )
        }
    }

    fun resetTransfers() {
        val originalIds = _uiState.value.managerPicks?.picks
            ?.sortedBy { it.position }
            ?.map { it.element }
            .orEmpty()

        _uiState.update {
            it.copy(
                draftSquadIds = if (originalIds.isNotEmpty()) originalIds else it.draftSquadIds,
                plannedTransfers = emptyList(),
                playerToTransferOut = null,
                transferStatusMessage = "Transfers reset."
            )
        }
        val teamId = _uiState.value.managerEntry?.id
        if (teamId != null) {
            loadMyTeamById(teamId)
        }
    }

    fun submitPlannedTransfers() {
        val state = _uiState.value
        if (state.plannedTransfers.isEmpty()) {
            _uiState.update { it.copy(transferStatusMessage = "No transfers selected yet.") }
            return
        }
        if (state.remainingBankTenths < 0) {
            _uiState.update {
                it.copy(transferStatusMessage = "Insufficient budget! You are £${(-state.remainingBankTenths) / 10.0}m over budget.")
            }
            return
        }

        val teamId = state.managerEntry?.id ?: 0
        val nextGw = state.events.firstOrNull { it.isNext }?.id ?: (state.selectedEventId + 1)

        viewModelScope.launch {
            repository.submitTransfers(teamId, nextGw, state.plannedTransfers)
                .onSuccess { msg ->
                    _uiState.update { it.copy(transferStatusMessage = msg) }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(transferStatusMessage = err.localizedMessage ?: "Transfer saved locally.")
                    }
                }
        }
    }

    // Leagues & Cups Functions
    fun loadClassicLeagueStandings(leagueId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isStandingsLoading = true,
                    standingsError = null,
                    selectedLeagueStandings = null
                )
            }
            repository.fetchLeagueStandings(leagueId)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isStandingsLoading = false,
                            selectedLeagueStandings = response
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isStandingsLoading = false,
                            standingsError = err.localizedMessage ?: "Failed to load league standings."
                        )
                    }
                }
        }
    }

    fun closeLeagueStandings() {
        _uiState.update { it.copy(selectedLeagueStandings = null, standingsError = null) }
    }
}
