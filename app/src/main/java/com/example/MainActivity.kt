package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rsludo.model.AiDifficulty
import com.example.rsludo.model.GameMode
import com.example.rsludo.model.Player
import com.example.rsludo.model.PlayerColor
import com.example.rsludo.online.OnlinePlayer
import com.example.rsludo.ui.screens.*
import com.example.rsludo.viewmodel.LudoViewModel
import com.example.ui.theme.RSLudoTheme
import com.example.ui.theme.RsBackgroundDark

enum class AppScreen {
    SPLASH,
    HOME,
    PLAYER_SETUP,
    ONLINE_LOBBY,
    GAME,
    STATISTICS,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: LudoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RSLudoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RsBackgroundDark
                ) {
                    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
                    val settings by viewModel.settings.collectAsStateWithLifecycle()
                    val statistics by viewModel.statistics.collectAsStateWithLifecycle()
                    val players by viewModel.configuredPlayers.collectAsStateWithLifecycle()
                    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()
                    val onlineState by viewModel.onlineServerManager.state.collectAsStateWithLifecycle()

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "screen_transition"
                    ) { target ->
                        when (target) {
                            AppScreen.SPLASH -> {
                                SplashScreen(
                                    onSplashFinished = {
                                        currentScreen = AppScreen.HOME
                                    }
                                )
                            }

                            AppScreen.HOME -> {
                                HomeScreen(
                                    player = players.firstOrNull(),
                                    statistics = statistics,
                                    soundEnabled = settings.soundEnabled,
                                    onSoundToggle = {
                                        viewModel.updateSettings(
                                            settings.copy(soundEnabled = !settings.soundEnabled)
                                        )
                                    },
                                    onBonusClaimed = {
                                        viewModel.soundManager.playHome()
                                    },
                                    onAvatarChange = { uri ->
                                        viewModel.setPlayerAvatar("p1", android.net.Uri.parse(uri))
                                    },
                                    onNameChange = { name ->
                                        viewModel.updatePlayerConfig(0) { it.copy(name = name) }
                                    },
                                    onPlayClick = { mode ->
                                        viewModel.selectedMode.value = mode
                                        if (mode == GameMode.VS_AI) {
                                            viewModel.updatePlayerConfig(1) {
                                                it.copy(isAi = true, name = "AI Master", aiDifficulty = AiDifficulty.MEDIUM)
                                            }
                                        } else {
                                            viewModel.updatePlayerConfig(1) {
                                                it.copy(isAi = false, name = "Player 2")
                                            }
                                        }
                                        viewModel.startNewGame()
                                        currentScreen = AppScreen.GAME
                                    },
                                    onPlayOnlineClick = {
                                        currentScreen = AppScreen.ONLINE_LOBBY
                                    },
                                    onPlayerSetupClick = {
                                        currentScreen = AppScreen.PLAYER_SETUP
                                    },
                                    onStatsClick = {
                                        currentScreen = AppScreen.STATISTICS
                                    },
                                    onSettingsClick = {
                                        currentScreen = AppScreen.SETTINGS
                                    }
                                )
                            }

                            AppScreen.PLAYER_SETUP -> {
                                PlayerSetupScreen(
                                    players = players,
                                    selectedMode = selectedMode,
                                    onModeChange = { mode ->
                                        viewModel.selectedMode.value = mode
                                    },
                                    onPlayerUpdate = { index, update ->
                                        viewModel.updatePlayerConfig(index, update)
                                    },
                                    onAvatarSelected = { playerId, uri ->
                                        viewModel.setPlayerAvatar(playerId, uri)
                                    },
                                    onAvatarRemoved = { playerId ->
                                        viewModel.removePlayerAvatar(playerId)
                                    },
                                    onStartGame = {
                                        viewModel.startNewGame()
                                        currentScreen = AppScreen.GAME
                                    },
                                    onBack = {
                                        currentScreen = AppScreen.HOME
                                    }
                                )
                            }

                            AppScreen.ONLINE_LOBBY -> {
                                val localP = players.firstOrNull() ?: Player("p1", "Player 1", PlayerColor.RED)
                                OnlineLobbyScreen(
                                    localPlayer = localP,
                                    onlineState = onlineState,
                                    onQuickMatch = {
                                        viewModel.onlineServerManager.quickMatch(localP) { matched ->
                                            viewModel.startOnlineGame(matched)
                                            currentScreen = AppScreen.GAME
                                        }
                                    },
                                    onCreateRoom = {
                                        viewModel.onlineServerManager.createPrivateRoom(localP)
                                    },
                                    onJoinRoom = { code ->
                                        viewModel.onlineServerManager.joinPrivateRoom(code, localP) { isJoined ->
                                            if (isJoined) {
                                                val currentOnline = viewModel.onlineServerManager.state.value.players.map { op ->
                                                    Player(op.id, op.name, op.color, op.avatarUri)
                                                }
                                                viewModel.startOnlineGame(currentOnline)
                                                currentScreen = AppScreen.GAME
                                            }
                                        }
                                    },
                                    onStartMatch = {
                                        val currentOnline = viewModel.onlineServerManager.state.value.players.map { op ->
                                            Player(op.id, op.name, op.color, op.avatarUri)
                                        }
                                        viewModel.startOnlineGame(currentOnline)
                                        currentScreen = AppScreen.GAME
                                    },
                                    onBack = {
                                        viewModel.onlineServerManager.leaveRoom()
                                        currentScreen = AppScreen.HOME
                                    }
                                )
                            }

                            AppScreen.GAME -> {
                                GameScreen(
                                    gameState = gameState,
                                    soundEnabled = settings.soundEnabled,
                                    onSoundToggle = {
                                        viewModel.updateSettings(
                                            settings.copy(soundEnabled = !settings.soundEnabled)
                                        )
                                    },
                                    onRollDice = {
                                        viewModel.rollDice()
                                    },
                                    onTokenClick = { token ->
                                        viewModel.onTokenTapped(token)
                                    },
                                    onSendReaction = { emoji, label ->
                                        viewModel.sendReaction(emoji, label)
                                    },
                                    onAskAiAdvice = {
                                        viewModel.askAiMoveAdvice()
                                    },
                                    onPauseClick = {
                                        viewModel.togglePause(true)
                                    },
                                    onResumeClick = {
                                        viewModel.togglePause(false)
                                    },
                                    onRestartClick = {
                                        viewModel.togglePause(false)
                                        viewModel.startNewGame()
                                    },
                                    onExitClick = {
                                        if (settings.confirmExit && gameState.winner == null) {
                                            viewModel.setExitConfirm(true)
                                        } else {
                                            viewModel.onlineServerManager.leaveRoom()
                                            currentScreen = AppScreen.HOME
                                        }
                                    },
                                    onConfirmLeave = {
                                        viewModel.setExitConfirm(false)
                                        viewModel.togglePause(false)
                                        viewModel.onlineServerManager.leaveRoom()
                                        currentScreen = AppScreen.HOME
                                    },
                                    onDismissLeave = {
                                        viewModel.setExitConfirm(false)
                                    },
                                    onPlayAgain = {
                                        viewModel.startNewGame()
                                    }
                                )
                            }

                            AppScreen.STATISTICS -> {
                                StatisticsScreen(
                                    statistics = statistics,
                                    onResetStats = {
                                        viewModel.resetStats()
                                    },
                                    onBack = {
                                        currentScreen = AppScreen.HOME
                                    }
                                )
                            }

                            AppScreen.SETTINGS -> {
                                SettingsScreen(
                                    settings = settings,
                                    onSettingsChanged = { updated ->
                                        viewModel.updateSettings(updated)
                                    },
                                    onBack = {
                                        currentScreen = AppScreen.HOME
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
