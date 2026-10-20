package com.example.fpl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fpl.data.model.EventDto
import com.example.fpl.data.model.FixtureDto
import com.example.fpl.data.model.LeagueStandingsResponse
import com.example.fpl.data.model.ManagerEntryDto
import com.example.fpl.data.model.ManagerPicksResponse
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
    val selectedEventId: Int = 8,
    val fixtures: List<FixtureDto> = emptyList(),
    val isFixturesLoading: Boolean = false,
    val playerHistory: List<PlayerHistoryDto> = emptyList(),
    val isPlayerHistoryLoading: Boolean = false,
    // Auth & Profile Screen State
    val isLoggedIn: Boolean = true,
    val showAuthScreen: Boolean = false,
    val isSignUpMode: Boolean = false,
    val loggedInEmail: String = "manager@fantasy.premierleague.com",
    val managerHandle: String = "@FantasyUser",
    val isLoggingIn: Boolean = false,
    val loginError: String? = null,
    val showEditProfileDialog: Boolean = false,
    // My Team & Pitch State
    val teamIdInput: String = "",
    val isMyTeamLoading: Boolean = false,
    val myTeamError: String? = null,
    val managerEntry: ManagerEntryDto? = null,
    val managerPicks: ManagerPicksResponse? = null,
    val isPitchView: Boolean = true,
    val selectedFormation: String = "3-4-3",
    // Transfers State
    val draftSquadIds: List<Int> = emptyList(),
    val plannedTransfers: List<PlannedTransfer> = emptyList(),
    val playerToTransferOut: PlayerDto? = null,
    val pendingPlayerIn: PlayerDto? = null,
    val transferSubTab: Int = 0, // 0 = All Players, 1 = Watchlist, 2 = My Squad
    val watchlistIds: Set<Int> = emptySet(),
    val transferPositionFilter: Int? = null,
    val transferSearchQuery: String = "",
    val initialBankTenths: Int = 1024,
    val freeTransfers: Int = 1,
    val transferStatusMessage: String? = null,
    // Leagues & Cups State
    val leaguesSubTab: Int = 0, // 0 = My Leagues, 1 = Public, 2 = Global, 3 = Cups
    val selectedLeagueStandings: LeagueStandingsResponse? = null,
    val isStandingsLoading: Boolean = false,
    val standingsError: String? = null,
    val isDarkMode: Boolean = true
) {
    val currentEventName: String
        get() = events.firstOrNull { it.id == selectedEventId }?.name ?: "Gameweek $selectedEventId"

    val currentDeadlineFormatted: String
        get() {
            val raw = events.firstOrNull { it.id == selectedEventId }?.deadlineTime
            return if (raw != null && raw.length >= 16) {
                raw.substring(0, 16).replace("T", " ")
            } else {
                "Sat, 26 Oct 11:00"
            }
        }

    val displayTotalPoints: Int
        get() = managerEntry?.summaryOverallPoints ?: 487

    val displayGwPoints: Int
        get() = managerEntry?.summaryEventPoints ?: 62

    val displayOverallRank: String
        get() = managerEntry?.summaryOverallRank?.let { "%,d".format(it) } ?: "342,671"

    val displayTeamValue: String
        get() {
            val v = managerPicks?.entryHistory?.value ?: managerEntry?.lastDeadlineValue ?: 1024
            return "£${v / 10.0}m"
        }

    val filteredTransferPlayers: List<PlayerDto>
        get() = players
            .asSequence()
            .filter { p ->
                val matchesTab = when (transferSubTab) {
                    1 -> watchlistIds.contains(p.id)
                    2 -> draftSquadIds.contains(p.id)
                    else -> true
                }
                val matchesPos = transferPositionFilter == null || p.elementType == transferPositionFilter
                val matchesQuery = transferSearchQuery.isBlank() ||
                    p.webName.contains(transferSearchQuery, ignoreCase = true) ||
                    "${p.firstName} ${p.secondName}".contains(transferSearchQuery, ignoreCase = true)
                matchesTab && matchesPos && matchesQuery
            }
            .sortedByDescending { it.form.toDoubleOrNull() ?: 0.0 }
            .toList()
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

    fun setTransferSubTab(tab: Int) {
        _uiState.update { it.copy(transferSubTab = tab) }
    }

    fun setTransferPositionFilter(pos: Int?) {
        _uiState.update { it.copy(transferPositionFilter = pos) }
    }

    fun setLeaguesSubTab(tab: Int) {
        _uiState.update { it.copy(leaguesSubTab = tab) }
    }

    fun toggleWatchlist(playerId: Int) {
        _uiState.update { state ->
            val updated = state.watchlistIds.toMutableSet()
            if (!updated.add(playerId)) updated.remove(playerId)
            state.copy(watchlistIds = updated)
        }
    }

    fun openAuthScreen(show: Boolean, signUpMode: Boolean = false) {
        _uiState.update {
            it.copy(
                showAuthScreen = show,
                isSignUpMode = signUpMode,
                loginError = null
            )
        }
    }

    fun setSignUpMode(signUp: Boolean) {
        _uiState.update { it.copy(isSignUpMode = signUp, loginError = null) }
    }

    fun openEditProfileDialog(show: Boolean) {
        _uiState.update { it.copy(showEditProfileDialog = show) }
    }

    fun saveProfileAndTeamId(handle: String, teamIdText: String) {
        val cleanHandle = if (handle.startsWith("@")) handle else "@$handle"
        _uiState.update {
            it.copy(
                managerHandle = if (handle.isBlank()) it.managerHandle else cleanHandle,
                teamIdInput = teamIdText.filter { c -> c.isDigit() },
                showEditProfileDialog = false
            )
        }
        val parsedId = teamIdText.filter { it.isDigit() }.toIntOrNull()
        if (parsedId != null && parsedId > 0) {
            loadMyTeamById(parsedId)
        }
    }

    fun login(email: String, password: String, optionalTeamId: String, fullName: String = "") {
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

                    val handle = if (fullName.isNotBlank()) {
                        "@${fullName.replace(" ", "")}"
                    } else {
                        "@${email.substringBefore("@")}"
                    }

                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            isLoggedIn = true,
                            showAuthScreen = false,
                            loggedInEmail = session.email,
                            managerHandle = handle,
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
                            loginError = err.localizedMessage ?: "Authentication failed."
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
                showAuthScreen = true,
                isSignUpMode = false,
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
                        ?: 8

                    // Pick default Out/In showcase players (e.g. Haaland & Isak or top 2 forwards)
                    val forwards = response.elements.filter { it.elementType == 4 }
                        .sortedByDescending { it.nowCost }
                    val defaultOut = forwards.firstOrNull {
                        it.webName.contains("Haaland", ignoreCase = true)
                    } ?: forwards.getOrNull(0)
                    val defaultIn = forwards.firstOrNull {
                        it.webName.contains("Isak", ignoreCase = true)
                    } ?: forwards.getOrNull(1)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            players = response.elements,
                            teams = response.teams.sortedBy { t -> t.name },
                            teamsById = response.teams.associateBy { t -> t.id },
                            events = response.events,
                            selectedEventId = currentGw,
                            playerToTransferOut = it.playerToTransferOut ?: defaultOut,
                            pendingPlayerIn = it.pendingPlayerIn ?: defaultIn
                        )
                    }
                    generateSampleSquadIfEmpty()
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
            _uiState.update { it.copy(myTeamError = "Enter a valid numeric FPL Team ID.") }
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
                            managerHandle = "@${entry.playerFirstName}${entry.playerLastName}".replace(" ", ""),
                            draftSquadIds = if (squadIds.isNotEmpty()) squadIds else it.draftSquadIds,
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

    fun generateSampleSquadIfEmpty() {
        if (_uiState.value.draftSquadIds.isNotEmpty()) return
        val all = _uiState.value.players
        if (all.isEmpty()) return

        // Try to match the exact iconic players from the screenshots first
        val preferredNames = listOf(
            "Raya", "Flekken",
            "Gabriel", "Tsimikas", "Colwill", "Estupiñán", "Gvardiol",
            "Saka", "Palmer", "Mbeumo", "Rogers", "Eze",
            "Haaland", "Watkins", "Solanke"
        )
        val matched = mutableListOf<PlayerDto>()
        for (name in preferredNames) {
            val found = all.firstOrNull {
                it.webName.equals(name, ignoreCase = true) && !matched.contains(it)
            }
            if (found != null) matched.add(found)
        }

        // Fill any missing slots by top points per position (2 GK, 5 DEF, 5 MID, 3 FWD)
        val sortedAll = all.sortedByDescending { it.totalPoints }
        fun ensureCount(type: Int, target: Int) {
            for (p in sortedAll) {
                if (matched.count { it.elementType == type } >= target) break
                if (p.elementType == type && !matched.contains(p)) {
                    matched.add(p)
                }
            }
        }
        ensureCount(1, 2)
        ensureCount(2, 5)
        ensureCount(3, 5)
        ensureCount(4, 3)

        _uiState.update {
            it.copy(draftSquadIds = matched.map { p -> p.id })
        }
    }

    fun selectPlayerForTransferOut(player: PlayerDto?) {
        _uiState.update {
            it.copy(playerToTransferOut = player, transferStatusMessage = null)
        }
    }

    fun selectPendingPlayerIn(player: PlayerDto?) {
        _uiState.update {
            val currentOut = it.playerToTransferOut
            val autoOut = if (currentOut == null || currentOut.elementType != player?.elementType) {
                val squadPlayers = it.draftSquadIds.mapNotNull { id ->
                    it.players.firstOrNull { p -> p.id == id }
                }
                squadPlayers.firstOrNull { p -> p.elementType == player?.elementType } ?: currentOut
            } else {
                currentOut
            }
            it.copy(
                playerToTransferOut = autoOut,
                pendingPlayerIn = player,
                transferStatusMessage = null
            )
        }
    }

    fun updateTransferSearchQuery(query: String) {
        _uiState.update { it.copy(transferSearchQuery = query) }
    }

    fun confirmCurrentTransferPair() {
        val state = _uiState.value
        val outPlayer = state.playerToTransferOut
        val inPlayer = state.pendingPlayerIn

        if (outPlayer == null || inPlayer == null) {
            _uiState.update {
                it.copy(transferStatusMessage = "Select both an OUT player and an IN player first.")
            }
            return
        }

        val updatedSquad = state.draftSquadIds.map { id ->
            if (id == outPlayer.id) inPlayer.id else id
        }
        val newTransfer = PlannedTransfer(playerOut = outPlayer, playerIn = inPlayer)
        val updatedTransfers = state.plannedTransfers + newTransfer

        val teamId = state.managerEntry?.id ?: 0
        val nextGw = state.events.firstOrNull { it.isNext }?.id ?: state.selectedEventId

        viewModelScope.launch {
            repository.submitTransfers(teamId, nextGw, updatedTransfers)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            draftSquadIds = updatedSquad,
                            plannedTransfers = updatedTransfers,
                            transferStatusMessage = "Confirmed: ${outPlayer.webName} OUT ⇄ ${inPlayer.webName} IN!"
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            draftSquadIds = updatedSquad,
                            plannedTransfers = updatedTransfers,
                            transferStatusMessage = "Confirmed in Squad: ${outPlayer.webName} OUT ⇄ ${inPlayer.webName} IN!"
                        )
                    }
                }
        }
    }

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
                            standingsError = err.localizedMessage ?: "Failed to load standings."
                        )
                    }
                }
        }
    }

    fun closeLeagueStandings() {
        _uiState.update { it.copy(selectedLeagueStandings = null, standingsError = null) }
    }
}
