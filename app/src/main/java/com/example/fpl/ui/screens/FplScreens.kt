@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fpl.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.fpl.data.model.PickDto
import com.example.fpl.data.model.PlayerDto
import com.example.fpl.ui.viewmodel.FplUiState

// =========================================================================
// REUSABLE PLAYER PHOTO AVATAR (COIL + JERSEY FALLBACK)
// =========================================================================

@Composable
fun PlayerPhotoAvatar(
    player: PlayerDto,
    size: Dp = 48.dp
) {
    Surface(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        SubcomposeAsyncImage(
            model = player.photoUrl,
            contentDescription = player.webName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
            },
            error = {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
            }
        )
    }
}

// =========================================================================
// EMAIL & PASSWORD LOGIN DIALOG
// =========================================================================

@Composable
fun FplLoginDialog(
    state: FplUiState,
    onDismiss: () -> Unit,
    onLoginSubmit: (email: String, password: String, teamId: String) -> Unit,
    onLogout: () -> Unit
) {
    var email by remember { mutableStateOf(state.loggedInEmail) }
    var password by remember { mutableStateOf("") }
    var teamId by remember { mutableStateOf(state.teamIdInput) }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = if (state.isLoggedIn) "FPL Account Session" else "Sign In to FPL",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (state.isLoggedIn) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Signed in as ${state.loggedInEmail}",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    state.managerEntry?.let { entry ->
                        Text(
                            text = "Linked Squad: ${entry.teamName} (ID: ${entry.id})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Sign in with your Fantasy Premier League email & password. Adding your FPL Team ID ensures instant sync even if Premier League Cloudflare challenges mobile requests.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = teamId,
                        onValueChange = { ch -> teamId = ch.filter { it.isDigit() } },
                        label = { Text("FPL Team ID (Recommended)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )

                    state.loginError?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (state.isLoggingIn) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (state.isLoggedIn) {
                Button(onClick = {
                    onLogout()
                    onDismiss()
                }) {
                    Text("Sign Out")
                }
            } else {
                Button(
                    onClick = { onLoginSubmit(email, password, teamId) },
                    enabled = !state.isLoggingIn
                ) {
                    Text("Sign In")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// =========================================================================
// 1. PLAYERS LIST & DETAIL SCREENS (WITH PLAYER PHOTOS)
// =========================================================================

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
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlayerPhotoAvatar(player = player, size = 48.dp)
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.webName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$teamShortName • ${player.positionName} • ${player.selectedByPercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (player.news.isNotBlank()) {
                    Text(
                        text = player.news,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                PlayerPhotoAvatar(player = player, size = 68.dp)
                Spacer(modifier = Modifier.width(12.dp))
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

// =========================================================================
// 2. MY TEAM SCREEN WITH VISUAL FOOTBALL PITCH LINEUP
// =========================================================================

@Composable
fun MyTeamScreen(
    state: FplUiState,
    onTeamIdChange: (String) -> Unit,
    onLoadTeam: () -> Unit,
    onTogglePitchView: (Boolean) -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val playersById = state.players.associateBy { it.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                    Text("Load Squad")
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
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = entry.teamName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Manager: ${entry.playerFirstName} ${entry.playerLastName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = state.isPitchView,
                                    onClick = { onTogglePitchView(true) },
                                    label = { Text("Pitch") }
                                )
                                FilterChip(
                                    selected = !state.isPitchView,
                                    onClick = { onTogglePitchView(false) },
                                    label = { Text("List") }
                                )
                            }
                        }
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
                                value = (entry.summaryEventPoints ?: 0).toString()
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
                if (state.isPitchView) {
                    item {
                        FootballPitchLineup(
                            picks = picks,
                            playersById = playersById,
                            onPlayerClick = onPlayerClick
                        )
                    }
                } else {
                    val starters = picks.filter { it.position <= 11 }.sortedBy { it.position }
                    val bench = picks.filter { it.position > 11 }.sortedBy { it.position }

                    item {
                        Text(
                            text = "Starting XI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
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
                                player = player,
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
                                player = player,
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
}

@Composable
fun FootballPitchLineup(
    picks: List<PickDto>,
    playersById: Map<Int, PlayerDto>,
    onPlayerClick: (Int) -> Unit
) {
    val starters = picks.filter { it.position <= 11 }
    val bench = picks.filter { it.position > 11 }.sortedBy { it.position }

    val gkPicks = starters.filter { playersById[it.element]?.elementType == 1 }
    val defPicks = starters.filter { playersById[it.element]?.elementType == 2 }
    val midPicks = starters.filter { playersById[it.element]?.elementType == 3 }
    val fwdPicks = starters.filter { playersById[it.element]?.elementType == 4 }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Green Football Pitch Canvas + Formation Rows
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(470.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF00FF87).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stripeCount = 8
                val stripeHeight = size.height / stripeCount
                for (i in 0 until stripeCount) {
                    drawRect(
                        color = if (i % 2 == 0) Color(0xFF1B6E38) else Color(0xFF155E2E),
                        topLeft = Offset(0f, i * stripeHeight),
                        size = Size(size.width, stripeHeight)
                    )
                }

                val lineStroke = Stroke(width = 3f)
                val lineColor = Color.White.copy(alpha = 0.55f)
                val pad = 20f

                // Outer pitch boundary
                drawRect(
                    color = lineColor,
                    topLeft = Offset(pad, pad),
                    size = Size(size.width - pad * 2, size.height - pad * 2),
                    style = lineStroke
                )

                // Halfway line & center circle
                val midY = size.height / 2f
                drawLine(
                    color = lineColor,
                    start = Offset(pad, midY),
                    end = Offset(size.width - pad, midY),
                    strokeWidth = 3f
                )
                drawCircle(
                    color = lineColor,
                    radius = 52f,
                    center = Offset(size.width / 2f, midY),
                    style = lineStroke
                )

                // Top Penalty Box
                val boxW = size.width * 0.52f
                val boxH = size.height * 0.14f
                drawRect(
                    color = lineColor,
                    topLeft = Offset((size.width - boxW) / 2f, pad),
                    size = Size(boxW, boxH),
                    style = lineStroke
                )

                // Bottom Penalty Box
                drawRect(
                    color = lineColor,
                    topLeft = Offset((size.width - boxW) / 2f, size.height - pad - boxH),
                    size = Size(boxW, boxH),
                    style = lineStroke
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 14.dp, horizontal = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PitchLineRow(picks = gkPicks, playersById = playersById, onPlayerClick = onPlayerClick)
                PitchLineRow(picks = defPicks, playersById = playersById, onPlayerClick = onPlayerClick)
                PitchLineRow(picks = midPicks, playersById = playersById, onPlayerClick = onPlayerClick)
                PitchLineRow(picks = fwdPicks, playersById = playersById, onPlayerClick = onPlayerClick)
            }
        }

        // Substitutes Bench Box
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "SUBSTITUTES BENCH",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bench.forEachIndexed { index, pick ->
                        val player = playersById[pick.element]
                        if (player != null) {
                            val subLabel = if (index == 0) "GKP" else "${index}. ${player.positionName}"
                            PitchPlayerToken(
                                player = player,
                                pointsText = "${player.eventPoints} pts",
                                badgeText = subLabel,
                                isCaptainOrVice = false,
                                onClick = { onPlayerClick(player.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PitchLineRow(
    picks: List<PickDto>,
    playersById: Map<Int, PlayerDto>,
    onPlayerClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        picks.forEach { pick ->
            val player = playersById[pick.element]
            if (player != null) {
                val badge = when {
                    pick.isCaptain -> "C"
                    pick.isViceCaptain -> "V"
                    else -> null
                }
                val pts = player.eventPoints * pick.multiplier
                PitchPlayerToken(
                    player = player,
                    pointsText = "$pts pts",
                    badgeText = badge,
                    isCaptainOrVice = badge != null,
                    onClick = { onPlayerClick(player.id) }
                )
            }
        }
    }
}

@Composable
fun PitchPlayerToken(
    player: PlayerDto,
    pointsText: String,
    badgeText: String?,
    isCaptainOrVice: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(74.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            PlayerPhotoAvatar(player = player, size = 48.dp)
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isCaptainOrVice) Color(0xFF00FF87) else Color(0xFF37003C))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCaptainOrVice) Color(0xFF37003C) else Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = player.webName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(Color(0xFF37003C))
                .padding(horizontal = 3.dp, vertical = 2.dp)
        )
        Text(
            text = pointsText,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF37003C),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(Color(0xFF00FF87))
                .padding(vertical = 1.dp)
        )
    }
}

@Composable
fun SquadPickRow(
    player: PlayerDto,
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
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerPhotoAvatar(player = player, size = 42.dp)
                Column {
                    Text(text = name, fontWeight = FontWeight.Bold)
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "$points pts",
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// =========================================================================
// 3. INTERACTIVE TRANSFERS SCREEN
// =========================================================================

@Composable
fun TransfersScreen(
    state: FplUiState,
    onGenerateTemplateSquad: () -> Unit,
    onSelectPlayerOut: (PlayerDto?) -> Unit,
    onTransferSearchChange: (String) -> Unit,
    onConfirmReplacement: (PlayerDto) -> Unit,
    onResetTransfers: () -> Unit,
    onSubmitTransfers: () -> Unit
) {
    val playersById = state.players.associateBy { it.id }
    val draftPlayers = state.draftSquadIds.mapNotNull { playersById[it] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Budget & Transfer Summary Card
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transfer Centre",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onResetTransfers) {
                            Text("Reset")
                        }
                        Button(onClick = onSubmitTransfers) {
                            Text("Confirm")
                        }
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatColumn("Free Transfers", state.freeTransfers.toString())
                    StatColumn("Transfers Made", state.plannedTransfers.size.toString())
                    StatColumn(
                        label = "Cost",
                        value = if (state.transferPointsCost > 0) "-${state.transferPointsCost} pts" else "0 pts"
                    )
                    StatColumn(
                        label = "In Bank",
                        value = "£${state.remainingBankTenths / 10.0}m",
                        highlight = state.remainingBankTenths >= 0
                    )
                }
            }
        }

        state.transferStatusMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (draftPlayers.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Load your FPL Team ID on the 'My Team' tab first, or generate a 15-man template squad right now to plan transfers.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onGenerateTemplateSquad) {
                        Text("Load 15-Player Template Squad")
                    }
                }
            }
            return@Column
        }

        // If user selected a player to transfer OUT, show replacement picker
        val playerOut = state.playerToTransferOut
        if (playerOut != null) {
            val maxAffordableCost = playerOut.nowCost + state.remainingBankTenths
            val replacements = state.players
                .asSequence()
                .filter { p ->
                    p.elementType == playerOut.elementType &&
                        !state.draftSquadIds.contains(p.id) &&
                        (state.transferSearchQuery.isBlank() ||
                            p.webName.contains(state.transferSearchQuery, ignoreCase = true))
                }
                .sortedByDescending { it.totalPoints }
                .toList()

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Transferring OUT: ${playerOut.webName} (${playerOut.formattedPrice})",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Max affordable ${playerOut.positionName}: £${maxAffordableCost / 10.0}m",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    TextButton(onClick = { onSelectPlayerOut(null) }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            OutlinedTextField(
                value = state.transferSearchQuery,
                onValueChange = onTransferSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search replacement ${playerOut.positionName}...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(replacements, key = { it.id }) { candidate ->
                    val clubShort = state.teamsById[candidate.teamId]?.shortName ?: "FPL"
                    PlayerListItem(
                        player = candidate,
                        teamShortName = clubShort,
                        onClick = { onConfirmReplacement(candidate) }
                    )
                }
            }
        } else {
            Text(
                text = "Tap any player in your 15-man squad below to transfer them OUT:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                val groups = listOf(
                    1 to "Goalkeepers",
                    2 to "Defenders",
                    3 to "Midfielders",
                    4 to "Forwards"
                )
                groups.forEach { (posType, posTitle) ->
                    item {
                        Text(
                            text = posTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    val posPlayers = draftPlayers.filter { it.elementType == posType }
                    items(posPlayers, key = { it.id }) { player ->
                        val clubShort = state.teamsById[player.teamId]?.shortName ?: "FPL"
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPlayerOut(player) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    PlayerPhotoAvatar(player = player, size = 42.dp)
                                    Column {
                                        Text(text = player.webName, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "$clubShort • ${player.formattedPrice} • ${player.totalPoints} pts",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                AssistChip(
                                    onClick = { onSelectPlayerOut(player) },
                                    label = { Text("Swap") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.SwapHoriz,
                                            contentDescription = "Swap",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. LEAGUES & CUPS SCREEN (WITH CLASSIC STANDINGS DRILL-DOWN)
// =========================================================================

@Composable
fun LeaguesAndCupsScreen(
    state: FplUiState,
    onOpenLeagueStandings: (Int) -> Unit,
    onCloseLeagueStandings: () -> Unit,
    onInspectRivalTeam: (Int) -> Unit
) {
    // If a Classic League is selected, show its full Standings Table
    val standingsResp = state.selectedLeagueStandings
    if (standingsResp != null || state.isStandingsLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCloseLeagueStandings) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = standingsResp?.league?.name ?: "Loading League Standings...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.isStandingsLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (standingsResp != null) {
                Text(
                    text = "Tap any manager row to load and inspect their squad on the Pitch!",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(standingsResp.standings.results, key = { it.id }) { row ->
                        val movement = when {
                            row.lastRank == 0 -> "•"
                            row.rank < row.lastRank -> "▲"
                            row.rank > row.lastRank -> "▼"
                            else -> "•"
                        }
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onInspectRivalTeam(row.entry) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "#${row.rank} $movement",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(text = row.entryName, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = row.playerName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    StatColumn("GW", row.eventTotal.toString())
                                    StatColumn("Total", row.total.toString(), highlight = true)
                                }
                            }
                        }
                    }
                }
            }
        }
        return
    }

    val entry = state.managerEntry
    if (entry == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Load your FPL Team ID on the 'My Team' tab or Sign In at the top right to view all of your Classic Leagues, H2H Leagues, and FPL Cup ties!",
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val classicLeagues = entry.leagues?.classic.orEmpty()
    val h2hLeagues = entry.leagues?.h2h.orEmpty()
    val cupMatches = entry.leagues?.cup?.matches.orEmpty() + entry.leagues?.cupMatches.orEmpty()
    val cupStatus = entry.leagues?.cup?.status

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Classic Leagues (${classicLeagues.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Tap any league to view full standings table",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(classicLeagues, key = { it.id }) { league ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenLeagueStandings(league.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = league.name, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Previous Rank: ${league.entryLastRank ?: "-"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatColumn(
                        label = "Current Rank",
                        value = league.entryRank?.let { "#$it" } ?: "-",
                        highlight = true
                    )
                }
            }
        }

        if (h2hLeagues.isNotEmpty()) {
            item {
                Text(
                    text = "Head-to-Head Leagues",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(h2hLeagues, key = { it.id }) { h2h ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = h2h.name, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Rank #${h2h.entryRank ?: "-"}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "FPL Cup",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        cupStatus?.let { status ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Qualification State: ${status.qualificationState ?: "In Progress"}",
                            fontWeight = FontWeight.SemiBold
                        )
                        status.qualificationRank?.let { qRank ->
                            Text(
                                text = "Qualification Rank: #$qRank",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        if (cupMatches.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No Cup ties recorded for this squad yet.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(cupMatches.distinctBy { it.id }) { tie ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Gameweek ${tie.event} Cup Tie",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = tie.entry1Name, fontWeight = FontWeight.Bold)
                                Text(
                                    text = tie.entry1PlayerName,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = "${tie.entry1Points} - ${tie.entry2Points}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(text = tie.entry2Name, fontWeight = FontWeight.Bold)
                                Text(
                                    text = tie.entry2PlayerName,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. FIXTURES & GAMEWEEK SCREEN
// =========================================================================

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
