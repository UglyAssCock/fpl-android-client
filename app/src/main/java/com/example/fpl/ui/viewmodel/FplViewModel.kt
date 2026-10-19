package com.example.fpl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fpl.data.model.EventDto
import com.example.fpl.data.model.FixtureDto
import com.example.fpl.data.model.ManagerEntryDto
import com.example.fpl.data.model.ManagerPicksResponse
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
    val teamIdInput: String = "",
    val isMyTeamLoading: Boolean = false,
    val myTeamError: String? = null,
    val managerEntry: ManagerEntryDto? = null,
    val managerPicks: ManagerPicksResponse? = null,
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

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isMyTeamLoading = true,
                    myTeamError = null
                )
            }
            val fallbackGw = _uiState.value.selectedEventId
            repository.fetchManagerTeam(id, fallbackGw)
                .onSuccess { (entry, picks) ->
                    _uiState.update {
                        it.copy(
                            isMyTeamLoading = false,
                            managerEntry = entry,
                            managerPicks = picks
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
}
