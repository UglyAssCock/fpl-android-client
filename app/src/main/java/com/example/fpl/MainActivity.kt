@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fpl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fpl.ui.screens.FixturesScreen
import com.example.fpl.ui.screens.FplLoginDialog
import com.example.fpl.ui.screens.LeaguesAndCupsScreen
import com.example.fpl.ui.screens.MyTeamScreen
import com.example.fpl.ui.screens.PlayerDetailScreen
import com.example.fpl.ui.screens.PlayersScreen
import com.example.fpl.ui.screens.TransfersScreen
import com.example.fpl.ui.theme.FplTheme
import com.example.fpl.ui.viewmodel.FplViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: FplViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()

            FplTheme(darkTheme = state.isDarkMode) {
                FplApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FplApp(viewModel: FplViewModel) {
    val state by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "players"

    if (state.showLoginDialog) {
        FplLoginDialog(
            state = state,
            onDismiss = { viewModel.openLoginDialog(false) },
            onLoginSubmit = { email, password, teamId ->
                viewModel.login(email, password, teamId)
            },
            onLogout = { viewModel.logout() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FPL Companion",
                            fontWeight = FontWeight.Bold
                        )
                        if (state.isLoggedIn) {
                            Text(
                                text = state.loggedInEmail,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openLoginDialog(true) }) {
                        Icon(
                            imageVector = if (state.isLoggedIn) Icons.Default.AccountCircle else Icons.AutoMirrored.Filled.Login,
                            contentDescription = "Login",
                            tint = if (state.isLoggedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (state.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode"
                        )
                    }
                    IconButton(onClick = { viewModel.loadBootstrapData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "players",
                    onClick = {
                        navController.navigate("players") {
                            popUpTo("players") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Players") },
                    label = { Text("Players") }
                )
                NavigationBarItem(
                    selected = currentRoute == "my_team",
                    onClick = {
                        navController.navigate("my_team") {
                            popUpTo("players") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Groups, contentDescription = "Pitch") },
                    label = { Text("Pitch") }
                )
                NavigationBarItem(
                    selected = currentRoute == "transfers",
                    onClick = {
                        navController.navigate("transfers") {
                            popUpTo("players") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Transfers") },
                    label = { Text("Transfers") }
                )
                NavigationBarItem(
                    selected = currentRoute == "leagues",
                    onClick = {
                        navController.navigate("leagues") {
                            popUpTo("players") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Leagues") },
                    label = { Text("Leagues") }
                )
                NavigationBarItem(
                    selected = currentRoute == "fixtures",
                    onClick = {
                        navController.navigate("fixtures") {
                            popUpTo("players") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Fixtures") },
                    label = { Text("Fixtures") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading official FPL data...")
                    }
                }
                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.errorMessage ?: "Unknown Error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadBootstrapData() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    NavHost(
                        navController = navController,
                        startDestination = "players"
                    ) {
                        composable("players") {
                            PlayersScreen(
                                state = state,
                                onSearchChange = viewModel::updateSearchQuery,
                                onPositionSelect = viewModel::selectPosition,
                                onTeamSelect = viewModel::selectTeam,
                                onPlayerClick = { playerId ->
                                    viewModel.loadPlayerDetails(playerId)
                                    navController.navigate("player_detail/$playerId")
                                }
                            )
                        }
                        composable(
                            route = "player_detail/{playerId}",
                            arguments = listOf(navArgument("playerId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val playerId = backStackEntry.arguments?.getInt("playerId") ?: 0
                            PlayerDetailScreen(
                                playerId = playerId,
                                state = state,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("my_team") {
                            MyTeamScreen(
                                state = state,
                                onTeamIdChange = viewModel::updateTeamIdInput,
                                onLoadTeam = viewModel::loadMyTeam,
                                onTogglePitchView = viewModel::setPitchView,
                                onPlayerClick = { playerId ->
                                    viewModel.loadPlayerDetails(playerId)
                                    navController.navigate("player_detail/$playerId")
                                }
                            )
                        }
                        composable("transfers") {
                            TransfersScreen(
                                state = state,
                                onGenerateTemplateSquad = viewModel::generateSampleSquadIfEmpty,
                                onSelectPlayerOut = viewModel::selectPlayerForTransferOut,
                                onTransferSearchChange = viewModel::updateTransferSearchQuery,
                                onConfirmReplacement = viewModel::makeTransfer,
                                onResetTransfers = viewModel::resetTransfers,
                                onSubmitTransfers = viewModel::submitPlannedTransfers
                            )
                        }
                        composable("leagues") {
                            LeaguesAndCupsScreen(
                                state = state,
                                onOpenLeagueStandings = viewModel::loadClassicLeagueStandings,
                                onCloseLeagueStandings = viewModel::closeLeagueStandings,
                                onInspectRivalTeam = { rivalEntryId ->
                                    viewModel.closeLeagueStandings()
                                    viewModel.loadMyTeamById(rivalEntryId)
                                    navController.navigate("my_team")
                                }
                            )
                        }
                        composable("fixtures") {
                            FixturesScreen(
                                state = state,
                                onSelectEvent = viewModel::loadFixturesForEvent
                            )
                        }
                    }
                }
            }
        }
    }
}
