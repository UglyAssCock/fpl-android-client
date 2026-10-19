@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fpl.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fpl.data.model.PlayerDto
import com.example.fpl.ui.viewmodel.FplUiState

@Composable
fun PlayersScreen(
    state: FplUiState,
    onSearchChange: (String) -> Unit,
    onPositionSelect: (Int?) -> Unit,
    onTeamSelect: (Int?) -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            placeholder = { Text("Search player name...") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        // Position Filter Row
        val positions = listOf(null to "All", 1 to "GKP", 2 to "DEF", 3 to "MID", 4 to "FWD")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            positions.forEach { (posId, label) ->
                FilterChip(
                    selected = state.selectedPosition == posId,
                    onClick = { onPositionSelect(posId) },
                    label = { Text(label) }
                )
            }
        }

        // Team Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = state.selectedTeamId == null,
                onClick = { onTeamSelect(null) },
                label = { Text("All Clubs") }
            )
            state.teams.forEach { team ->
                FilterChip(
                    selected = state.selectedTeamId == team.id,
                    onClick = {
                        onTeamSelect(if (state.selectedTeamId == team.id) null else team.id)
                    },
                    label = { Text(team.shortName) }
                )
            }
        }

        Text(
            text = "Showing ${state.filteredPlayers.size} players (sorted by total points)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(state.filteredPlayers, key = { it.id }) { player ->
                val teamShort = state.teamsById[player.teamId]?.shortName ?: "FPL"
                PlayerListItem(
                    player = player,
                    teamShortName = teamShort,
                    onClick = { onPlayerClick(player.id) }
                )
            }
        }
    }
}

@Composable
fun PlayerListItem(
    player: PlayerDto,
    teamShortName: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.webName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$teamShortName • ${player.positionName} • Selected ${player.selectedByPercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (player.news.isNotBlank()) {
                    Text(
                        text = player.news,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatColumn(label = "Price", value = player.formattedPrice)
                StatColumn(label = "Form", value = player.form)
                StatColumn(label = "Pts", value = player.totalPoints.toString(), highlight = true)
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PlayerDetailScreen(
    playerId: Int,
    state: FplUiState,
    onBack: () -> Unit
) {
    val player = state.players.firstOrNull { it.id == playerId }
    if (player == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Player not found.")
        }
        return
    }
    val teamName = state.teamsById[player.teamId]?.name ?: "Unknown Team"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "${player.firstName} ${player.secondName}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$teamName • ${player.positionName} • ${player.formattedPrice}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (player.news.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "News: ${player.news}",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Season Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn("Total Pts", player.totalPoints.toString(), highlight = true)
                        StatColumn("GW Pts", player.eventPoints.toString())
                        StatColumn("Form", player.form)
                        StatColumn("PPG", player.pointsPerGame)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn("Goals", player.goalsScored.toString())
                        StatColumn("Assists", player.assists.toString())
                        StatColumn("Clean Sheets", player.cleanSheets.toString())
                        StatColumn("Minutes", player.minutes.toString())
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn("Selected By", "${player.selectedByPercent}%")
                        StatColumn("ICT Index", player.ictIndex)
                    }
                }
            }
        }

        item {
            Text(
                text = "Recent Match History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (state.isPlayerHistoryLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (state.playerHistory.isEmpty()) {
            item {
                Text(
                    text = "No match history available yet.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(state.playerHistory.take(15)) { match ->
                val oppShort = state.teamsById[match.opponentTeam]?.shortName ?: "OPP"
                val venue = if (match.wasHome) "H" else "A"
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GW${match.round} vs $oppShort ($venue)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${match.minutes}' • G:${match.goalsScored} A:${match.assists}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${match.totalPoints} pts",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyTeamScreen(
    state: FplUiState,
    onTeamIdChange: (String) -> Unit,
    onLoadTeam: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val playersById = state.players.associateBy { it.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "My FPL Squad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter your numeric FPL Team ID (from your team's URL on the FPL website) to view your squad and live points.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.teamIdInput,
                    onValueChange = onTeamIdChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("FPL Team ID (e.g. 123456)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onLoadTeam() })
                )
                Button(
                    onClick = onLoadTeam,
                    enabled = !state.isMyTeamLoading
                ) {
                    Text("Load")
                }
            }
        }

        if (state.isMyTeamLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        state.myTeamError?.let { err ->
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        state.managerEntry?.let { entry ->
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = entry.teamName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Manager: ${entry.playerFirstName} ${entry.playerLastName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatColumn(
                                label = "Overall Pts",
                                value = entry.summaryOverallPoints.toString(),
                                highlight = true
                            )
                            StatColumn(
                                label = "GW Pts",
                                value = entry.summaryEventPoints.toString()
                            )
                            StatColumn(
                                label = "Overall Rank",
                                value = entry.summaryOverallRank?.toString() ?: "N/A"
                            )
                        }
                        state.managerPicks?.entryHistory?.let { hist ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatColumn("Squad Value", "£${hist.value / 10.0}m")
                                StatColumn("In Bank", "£${hist.bank / 10.0}m")
                                StatColumn("Bench Pts", hist.pointsOnBench.toString())
                            }
                        }
                    }
                }
            }

            val picks = state.managerPicks?.picks.orEmpty()
            if (picks.isNotEmpty()) {
                val starters = picks.filter { it.position <= 11 }.sortedBy { it.position }
                val bench = picks.filter { it.position > 11 }.sortedBy { it.position }

                item {
                    Text(
                        text = "Starting XI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                items(starters) { pick ->
                    val player = playersById[pick.element]
                    if (player != null) {
                        val badge = when {
                            pick.isCaptain -> " (C)"
                            pick.isViceCaptain -> " (VC)"
                            else -> ""
                        }
                        val teamShort = state.teamsById[player.teamId]?.shortName ?: ""
                        SquadPickRow(
                            name = "${player.webName}$badge",
                            subtitle = "${player.positionName} • $teamShort",
                            points = player.eventPoints * pick.multiplier,
                            onClick = { onPlayerClick(player.id) }
                        )
                    }
                }

                item {
                    Text(
                        text = "Bench",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                items(bench) { pick ->
                    val player = playersById[pick.element]
                    if (player != null) {
                        val teamShort = state.teamsById[player.teamId]?.shortName ?: ""
                        SquadPickRow(
                            name = player.webName,
                            subtitle = "${player.positionName} • $teamShort",
                            points = player.eventPoints,
                            onClick = { onPlayerClick(player.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SquadPickRow(
    name: String,
    subtitle: String,
    points: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = name, fontWeight = FontWeight.Bold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$points pts",
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FixturesScreen(
    state: FplUiState,
    onSelectEvent: (Int) -> Unit
) {
    val selectedEvent = state.events.firstOrNull { it.id == state.selectedEventId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Horizontal Gameweek Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.events.forEach { event ->
                FilterChip(
                    selected = event.id == state.selectedEventId,
                    onClick = { onSelectEvent(event.id) },
                    label = { Text("GW ${event.id}") }
                )
            }
        }

        selectedEvent?.let { gw ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = gw.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val statusLabel = when {
                            gw.isCurrent -> "Current GW"
                            gw.isNext -> "Next GW"
                            gw.finished -> "Finished"
                            else -> "Upcoming"
                        }
                        AssistChip(onClick = {}, label = { Text(statusLabel) })
                    }
                    Text(
                        text = "Deadline: ${gw.deadlineTime.replace("T", " ").replace("Z", " UTC")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn("Average Score", gw.averageEntryScore.toString())
                        StatColumn("Highest Score", gw.highestScore?.toString() ?: "-")
                    }
                }
            }
        }

        if (state.isFixturesLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.fixtures.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No fixtures found for this Gameweek.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.fixtures, key = { it.id }) { fixture ->
                    val homeTeam = state.teamsById[fixture.teamH]?.name ?: "Home"
                    val awayTeam = state.teamsById[fixture.teamA]?.name ?: "Away"
                    val scoreText = if (fixture.started == true) {
                        "${fixture.teamHScore ?: 0} - ${fixture.teamAScore ?: 0}"
                    } else {
                        "vs"
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = homeTeam,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = scoreText,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = awayTeam,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            fixture.kickoffTime?.let { kickoff ->
                                Text(
                                    text = kickoff.replace("T", " ").replace("Z", " UTC"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
