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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fpl.ui.screens.EditProfileDialog
import com.example.fpl.ui.screens.FixturesScreen
import com.example.fpl.ui.screens.FplFullAuthScreen
import com.example.fpl.ui.screens.HomeDashboardScreen
import com.example.fpl.ui.screens.LeaguesHubScreen
import com.example.fpl.ui.screens.MoreMenuScreen
import com.example.fpl.ui.screens.MyTeamPitchScreen
import com.example.fpl.ui.screens.PlayerDetailScreen
import com.example.fpl.ui.screens.TopBrandHeader
import com.example.fpl.ui.screens.TransfersMarketScreen
import com.example.fpl.ui.theme.FplBgNavy
import com.example.fpl.ui.theme.FplBrightPurple
import com.example.fpl.ui.theme.FplCardNavy
import com.example.fpl.ui.theme.FplTextSecondary
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
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    if (state.showAuthScreen || !state.isLoggedIn) {
        FplFullAuthScreen(
            state = state,
            onSelectSignUpMode = viewModel::setSignUpMode,
            onLogin = { email, password ->
                viewModel.login(email, password)
            },
            onSignUp = { fullName, email, password, confirmPassword ->
                viewModel.signUp(fullName, email, password, confirmPassword)
            },
            onOpenOfficialWebLogin = viewModel::openOfficialWebLogin,
            onOfficialWebAuthSuccess = { token, email ->
                viewModel.onOfficialWebAuthSuccess(token, email)
            },
            onClose = if (state.isLoggedIn) {
                { viewModel.openAuthScreen(false) }
            } else {
                null
            }
        )
        return
    }

    if (state.showEditProfileDialog) {
        EditProfileDialog(
            state = state,
            onDismiss = { viewModel.openEditProfileDialog(false) },
            onSave = { handle ->
                viewModel.saveProfileHandle(handle)
            }
        )
    }

    Scaffold(
        containerColor = FplBgNavy,
        topBar = {
            TopBrandHeader(
                onNotificationClick = { viewModel.loadBootstrapData() },
                onProfileClick = { viewModel.openEditProfileDialog(true) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF05081A)
            ) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FplBrightPurple,
                    selectedTextColor = FplBrightPurple,
                    indicatorColor = FplCardNavy,
                    unselectedIconColor = FplTextSecondary,
                    unselectedTextColor = FplTextSecondary
                )

                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentRoute == "my_team",
                    onClick = {
                        navController.navigate("my_team") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Groups, contentDescription = "My Team") },
                    label = { Text("My Team") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentRoute == "transfers",
                    onClick = {
                        navController.navigate("transfers") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Transfers") },
                    label = { Text("Transfers") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentRoute == "leagues",
                    onClick = {
                        navController.navigate("leagues") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Leagues") },
                    label = { Text("Leagues") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = currentRoute == "more",
                    onClick = {
                        navController.navigate("more") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                    label = { Text("More") },
                    colors = navItemColors
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
                        CircularProgressIndicator(color = FplBrightPurple)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading Fantasy Premier League...", color = Color.White)
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
                            color = MaterialTheme.colorScheme.error
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
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeDashboardScreen(
                                state = state,
                                onNavigateToMyTeam = { navController.navigate("my_team") },
                                onNavigateToTransfers = { navController.navigate("transfers") },
                                onNavigateToLeagues = { navController.navigate("leagues") },
                                onNavigateToFixtures = { navController.navigate("fixtures") },
                                onPlayerClick = { playerId ->
                                    viewModel.loadPlayerDetails(playerId)
                                    navController.navigate("player_detail/$playerId")
                                }
                            )
                        }
                        composable("my_team") {
                            MyTeamPitchScreen(
                                state = state,
                                onTogglePitchView = viewModel::setPitchView,
                                onNavigateToTransfers = { navController.navigate("transfers") },
                                onPlayerClick = { playerId ->
                                    viewModel.loadPlayerDetails(playerId)
                                    navController.navigate("player_detail/$playerId")
                                }
                            )
                        }
                        composable("transfers") {
                            TransfersMarketScreen(
                                state = state,
                                onSelectSubTab = viewModel::setTransferSubTab,
                                onSelectPosFilter = viewModel::setTransferPositionFilter,
                                onSearchChange = viewModel::updateTransferSearchQuery,
                                onSelectPlayerOut = viewModel::selectPlayerForTransferOut,
                                onSelectPlayerIn = viewModel::selectPendingPlayerIn,
                                onToggleWatchlist = viewModel::toggleWatchlist,
                                onConfirmTransfers = viewModel::confirmCurrentTransferPair,
                                onResetTransfers = viewModel::resetTransfers,
                                onPlayerClick = { playerId ->
                                    viewModel.loadPlayerDetails(playerId)
                                    navController.navigate("player_detail/$playerId")
                                }
                            )
                        }
                        composable("leagues") {
                            LeaguesHubScreen(
                                state = state,
                                onSelectSubTab = viewModel::setLeaguesSubTab,
                                onOpenLeagueStandings = viewModel::loadClassicLeagueStandings,
                                onCloseLeagueStandings = viewModel::closeLeagueStandings,
                                onInspectRivalTeam = { rivalEntryId ->
                                    viewModel.closeLeagueStandings()
                                    viewModel.loadMyTeamById(rivalEntryId)
                                    navController.navigate("my_team")
                                }
                            )
                        }
                        composable("more") {
                            MoreMenuScreen(
                                state = state,
                                onEditProfileClick = { viewModel.openEditProfileDialog(true) },
                                onOpenFixtures = { navController.navigate("fixtures") },
                                onOpenPlayers = { navController.navigate("transfers") },
                                onLogoutClick = { viewModel.logout() }
                            )
                        }
                        composable("fixtures") {
                            FixturesScreen(
                                state = state,
                                onSelectEvent = viewModel::loadFixturesForEvent,
                                onBack = { navController.popBackStack() }
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
                    }
                }
            }
        }
    }
}
