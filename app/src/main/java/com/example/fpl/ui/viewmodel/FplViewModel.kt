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
import java.util.Locale

data class RegisteredAccount(
    val fullName: String,
    val email: String,
    val password: String
)

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
    // Strict Email & Password Auth State (No Team ID Login)
    val isLoggedIn: Boolean = false,
    val showAuthScreen: Boolean = true,
    val isSignUpMode: Boolean = false,
    val loggedInEmail: String = "",
    val managerHandle: String = "@FPLManager",
    val isLoggingIn: Boolean = false,
    val loginError: String? = null,
    val showEditProfileDialog: Boolean = false,
    // My Team & Pitch State
    val isMyTeamLoading: Boolean = false,
    val myTeamError: String? = null,
    val managerEntry: ManagerEntryDto? = null,
    val managerPicks: ManagerPicksResponse? = null,
    val isPitchView: Boolean = true,
    val selectedFormation: String = "3-4-3",
    // Interactive Transfers State
    val originalSquadIds: List<Int> = emptyList(),
    val draftSquadIds: List<Int> = emptyList(),
    val plannedTransfers: List<PlannedTransfer> = emptyList(),
    val playerToTransferOut: PlayerDto? = null,
    val pendingPlayerIn: PlayerDto? = null,
    val transferSubTab: Int = 0, // 0 = All Players, 1 = Watchlist, 2 = My Squad
    val watchlistIds: Set<Int> = emptySet(),
    val transferPositionFilter: Int? = null,
    val transferSearchQuery: String = "",
    val initialBankTenths: Int = 15, // £1.5m in the bank
    val freeTransfers: Int = 2,
    val transferStatusMessage: String? = null,
    // Leagues & Cups State
    val leaguesSubTab: Int = 0,
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
                "Sat, 11:00 UTC"
            }
        }

    val squadPlayers: List<PlayerDto>
        get() {
            val byId = players.associateBy { it.id }
            return draftSquadIds.mapNotNull { byId[it] }
        }

    val squadValueTenths: Int
        get() {
            val calculated = squadPlayers.sumOf { it.nowCost }
            return if (calculated > 0) calculated else (managerPicks?.entryHistory?.value ?: 985)
        }

    val remainingBankTenths: Int
        get() {
            val delta = plannedTransfers.sumOf { it.playerOut.nowCost - it.playerIn.nowCost }
            return initialBankTenths + delta
        }

    val displayTeamValue: String
        get() = String.format(Locale.US, "£%.1fm", squadValueTenths / 10.0)

    val displayBankValue: String
        get() = String.format(Locale.US, "£%.1fm", remainingBankTenths / 10.0)

    val displayTotalPoints: Int
        get() {
            val fromEntry = managerEntry?.summaryOverallPoints
            if (fromEntry != null && fromEntry > 0) return fromEntry
            val sumTotal = squadPlayers.take(11).sumOf { it.totalPoints }
            return if (sumTotal > 0) sumTotal else 466
        }

    val displayGwPoints: Int
        get() {
            val fromEntry = managerEntry?.summaryEventPoints
            if (fromEntry != null && fromEntry > 0) return fromEntry
            val starters = squadPlayers.take(11)
            val sumGw = starters.sumOf {
                if (it.eventPoints > 0) it.eventPoints else (it.totalPoints % 9).coerceAtLeast(2)
            }
            return if (sumGw > 0) sumGw else 62
        }

    val displayOverallRank: String
        get() = managerEntry?.summaryOverallRank?.let { "%,d".format(Locale.US, it) } ?: "342,671"

    val freeTransfersRemaining: Int
        get() = (freeTransfers - plannedTransfers.size).coerceAtLeast(0)

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
            .sortedByDescending { it.totalPoints }
            .toList()
}

class FplViewModel(
    private val repository: FplRepository = FplRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FplUiState())
    val uiState: StateFlow<FplUiState> = _uiState.asStateFlow()

    // Registered accounts in memory (plus any account created via Sign Up)
    private val registeredAccounts = mutableMapOf<String, RegisteredAccount>()

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

    fun saveProfileHandle(handle: String) {
        val cleanHandle = if (handle.startsWith("@")) handle else "@$handle"
        _uiState.update {
            it.copy(
                managerHandle = if (handle.isBlank()) it.managerHandle else cleanHandle,
                showEditProfileDialog = false
            )
        }
    }

    fun signUp(fullName: String, email: String, password: String, confirmPassword: String) {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase(Locale.ROOT)

        if (cleanName.length < 2) {
            _uiState.update { it.copy(loginError = "Please enter your full name.") }
            return
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            _uiState.update { it.copy(loginError = "Please enter a valid email address.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(loginError = "Password must be at least 6 characters.") }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(loginError = "Passwords do not match.") }
            return
        }

        val account = RegisteredAccount(
            fullName = cleanName,
            email = cleanEmail,
            password = password
        )
        registeredAccounts[cleanEmail] = account

        val handle = "@${cleanName.replace(" ", "")}"
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                showAuthScreen = false,
                isSignUpMode = false,
                loggedInEmail = cleanEmail,
                managerHandle = handle,
                loginError = null
            )
        }
    }

    fun login(email: String, password: String) {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)

        if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            _uiState.update { it.copy(loginError = "Please enter a valid email address.") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(loginError = "Please enter your password.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingIn = true, loginError = null) }

            // 1. Check if this email was registered in the app
            val localAccount = registeredAccounts[cleanEmail]
            if (localAccount != null) {
                if (localAccount.password == password) {
                    val handle = "@${localAccount.fullName.replace(" ", "")}"
                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            isLoggedIn = true,
                            showAuthScreen = false,
                            loggedInEmail = cleanEmail,
                            managerHandle = handle,
                            loginError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            loginError = "Incorrect password for $cleanEmail."
                        )
                    }
                }
                return@launch
            }

            // 2. Otherwise attempt official Premier League live cookie login (/api/me/)
            repository.loginWithEmailAndPassword(cleanEmail, password, null)
                .onSuccess { session ->
                    // Only accept if Premier League actually returned a live authenticated session or entry ID
                    if (session.isLiveCookieSession || (session.teamId != null && session.teamId > 0)) {
                        val handle = session.managerName?.let { "@${it.replace(" ", "")}" }
                            ?: "@${cleanEmail.substringBefore("@")}"

                        _uiState.update {
                            it.copy(
                                isLoggingIn = false,
                                isLoggedIn = true,
                                showAuthScreen = false,
                                loggedInEmail = cleanEmail,
                                managerHandle = handle,
                                loginError = null
                            )
                        }
                        if (session.teamId != null && session.teamId > 0) {
                            loadMyTeamById(session.teamId)
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoggingIn = false,
                                loginError = "Account not found or invalid credentials. Tap 'Sign Up' above to create your account first."
                            )
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            loginError = "Invalid email or password. Tap 'Sign Up' above to create your account."
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
                loginError = null,
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

    fun loadMyTeamById(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
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
                    val bank = picks?.entryHistory?.bank ?: entry.lastDeadlineBank ?: 15

                    _uiState.update {
                        val finalSquad = if (squadIds.isNotEmpty()) squadIds else it.draftSquadIds
                        it.copy(
                            isMyTeamLoading = false,
                            managerEntry = entry,
                            managerPicks = picks,
                            originalSquadIds = finalSquad,
                            draftSquadIds = finalSquad,
                            initialBankTenths = bank
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isMyTeamLoading = false,
                            myTeamError = "Could not load squad: ${error.localizedMessage ?: "Error"}"
                        )
                    }
                }
        }
    }

    fun generateSampleSquadIfEmpty() {
        if (_uiState.value.draftSquadIds.isNotEmpty()) return
        val all = _uiState.value.players
        if (all.isEmpty()) return

        // Build a balanced 15-man squad (~£98.5m total cost, leaving £1.5m in bank)
        val preferredNames = listOf(
            "Raya", "Flekken",
            "Gabriel", "Gvardiol", "Colwill", "Aina", "Robinson",
            "Saka", "Palmer", "Mbeumo", "Rogers", "Semenyo",
            "Haaland", "Wood", "Joao Pedro"
        )
        val matched = mutableListOf<PlayerDto>()
        val clubCounts = mutableMapOf<Int, Int>()

        for (name in preferredNames) {
            val found = all.firstOrNull {
                it.webName.equals(name, ignoreCase = true) && !matched.contains(it)
            }
            if (found != null && (clubCounts[found.teamId] ?: 0) < 3) {
                matched.add(found)
                clubCounts[found.teamId] = (clubCounts[found.teamId] ?: 0) + 1
            }
        }

        // Fill any missing slots by position (2 GK, 5 DEF, 5 MID, 3 FWD) within realistic price
        val sortedAll = all.sortedByDescending { it.totalPoints }
        fun ensureCount(type: Int, target: Int, maxPriceTenths: Int) {
            for (p in sortedAll) {
                if (matched.count { it.elementType == type } >= target) break
                val clubCount = clubCounts[p.teamId] ?: 0
                if (p.elementType == type && !matched.contains(p) && clubCount < 3 && p.nowCost <= maxPriceTenths) {
                    matched.add(p)
                    clubCounts[p.teamId] = clubCount + 1
                }
            }
        }
        ensureCount(1, 2, 55)
        ensureCount(2, 5, 65)
        ensureCount(3, 5, 115)
        ensureCount(4, 3, 155)

        // Order: 11 starters (1 GK, 3 DEF, 4 MID, 3 FWD) + 4 bench (1 GK, 2 DEF, 1 MID)
        val gks = matched.filter { it.elementType == 1 }
        val defs = matched.filter { it.elementType == 2 }
        val mids = matched.filter { it.elementType == 3 }
        val fwds = matched.filter { it.elementType == 4 }

        val starters = buildList {
            gks.getOrNull(0)?.let { add(it) }
            addAll(defs.take(3))
            addAll(mids.take(4))
            addAll(fwds.take(3))
        }
        val bench = matched.filter { !starters.contains(it) }
        val orderedIds = (starters + bench).map { it.id }

        val defaultOut = starters.lastOrNull()
        val defaultIn = sortedAll.firstOrNull {
            it.elementType == (defaultOut?.elementType ?: 4) && !orderedIds.contains(it.id)
        }

        val totalCost = (starters + bench).sumOf { it.nowCost }
        val bankTenths = (1000 - totalCost).coerceAtLeast(10)

        _uiState.update {
            it.copy(
                originalSquadIds = orderedIds,
                draftSquadIds = orderedIds,
                initialBankTenths = bankTenths,
                playerToTransferOut = defaultOut,
                pendingPlayerIn = defaultIn
            )
        }
    }

    fun selectPlayerForTransferOut(player: PlayerDto?) {
        _uiState.update { state ->
            val currentIn = state.pendingPlayerIn
            val updatedIn = if (player != null && currentIn?.elementType != player.elementType) {
                state.players
                    .filter { it.elementType == player.elementType && !state.draftSquadIds.contains(it.id) }
                    .maxByOrNull { it.totalPoints }
            } else {
                currentIn
            }
            state.copy(
                playerToTransferOut = player,
                pendingPlayerIn = updatedIn,
                transferPositionFilter = player?.elementType,
                transferStatusMessage = if (player != null) {
                    "Selected ${player.webName} (${player.positionName}) to transfer OUT. Tap any ${player.positionName} below to bring IN."
                } else null
            )
        }
    }

    fun selectPendingPlayerIn(player: PlayerDto?) {
        if (player == null) return
        _uiState.update { state ->
            // If user tapped a player already in their own squad, select them as the OUT player instead!
            if (state.draftSquadIds.contains(player.id)) {
                return@update state.copy(
                    playerToTransferOut = player,
                    transferPositionFilter = player.elementType,
                    transferStatusMessage = "${player.webName} is in your squad — selected to transfer OUT."
                )
            }

            val currentOut = state.playerToTransferOut
            val autoOut = if (currentOut == null || currentOut.elementType != player.elementType) {
                state.squadPlayers.firstOrNull { p -> p.elementType == player.elementType } ?: currentOut
            } else {
                currentOut
            }

            state.copy(
                playerToTransferOut = autoOut,
                pendingPlayerIn = player,
                transferStatusMessage = if (autoOut != null) {
                    "Ready: ${autoOut.webName} (${autoOut.formattedPrice}) OUT ➔ ${player.webName} (${player.formattedPrice}) IN. Tap 'Confirm Transfers'!"
                } else {
                    "Selected ${player.webName} IN. Tap a ${player.positionName} from your squad strip above to replace."
                }
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
                it.copy(transferStatusMessage = "Select both an OUT player from your squad and an IN player from the list.")
            }
            return
        }

        if (outPlayer.id == inPlayer.id) {
            _uiState.update {
                it.copy(transferStatusMessage = "Please choose a different player to transfer IN.")
            }
            return
        }

        if (outPlayer.elementType != inPlayer.elementType) {
            _uiState.update {
                it.copy(transferStatusMessage = "Positions must match (${outPlayer.positionName} for ${inPlayer.positionName}).")
            }
            return
        }

        if (state.draftSquadIds.contains(inPlayer.id)) {
            _uiState.update {
                it.copy(transferStatusMessage = "${inPlayer.webName} is already in your 15-man squad!")
            }
            return
        }

        val newBankTenths = state.remainingBankTenths + (outPlayer.nowCost - inPlayer.nowCost)
        if (newBankTenths < 0) {
            val shortBy = String.format(Locale.US, "£%.1fm", (-newBankTenths) / 10.0)
            _uiState.update {
                it.copy(transferStatusMessage = "Not enough budget! You are $shortBy short for ${inPlayer.webName}.")
            }
            return
        }

        // Check max 3 players per Premier League club
        val remainingSquad = state.squadPlayers.filter { it.id != outPlayer.id }
        val sameClubCount = remainingSquad.count { it.teamId == inPlayer.teamId }
        if (sameClubCount >= 3) {
            val clubName = state.teamsById[inPlayer.teamId]?.name ?: "that club"
            _uiState.update {
                it.copy(transferStatusMessage = "FPL Rule: You already own 3 players from $clubName.")
            }
            return
        }

        val updatedSquadIds = state.draftSquadIds.map { id ->
            if (id == outPlayer.id) inPlayer.id else id
        }
        val newTransfer = PlannedTransfer(playerOut = outPlayer, playerIn = inPlayer)
        val updatedTransfers = state.plannedTransfers + newTransfer

        val updatedPicks = state.managerPicks?.let { mp ->
            mp.copy(
                picks = mp.picks.map { pick ->
                    if (pick.element == outPlayer.id) pick.copy(element = inPlayer.id) else pick
                }
            )
        }

        // Prepare next default Out/In pair so user can keep making transfers smoothly
        val byId = state.players.associateBy { it.id }
        val nextOut = byId[inPlayer.id]
        val nextIn = state.players
            .filter { it.elementType == inPlayer.elementType && !updatedSquadIds.contains(it.id) }
            .maxByOrNull { it.totalPoints }

        _uiState.update {
            it.copy(
                draftSquadIds = updatedSquadIds,
                managerPicks = updatedPicks,
                plannedTransfers = updatedTransfers,
                playerToTransferOut = nextOut,
                pendingPlayerIn = nextIn,
                transferStatusMessage = "Transfer Confirmed: ${outPlayer.webName} OUT ⇄ ${inPlayer.webName} IN!"
            )
        }
    }

    fun resetTransfers() {
        val state = _uiState.value
        val restored = state.originalSquadIds.ifEmpty { state.draftSquadIds }
        val byId = state.players.associateBy { it.id }
        val firstOut = restored.mapNotNull { byId[it] }.firstOrNull { it.elementType == 4 }

        _uiState.update {
            it.copy(
                draftSquadIds = restored,
                plannedTransfers = emptyList(),
                playerToTransferOut = firstOut,
                transferPositionFilter = null,
                transferStatusMessage = "All planned transfers have been reset."
            )
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
