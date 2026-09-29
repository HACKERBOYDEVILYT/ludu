package com.example.rsludo.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class PlayerColor(
    val title: String,
    val primary: Color,
    val light: Color,
    val dark: Color,
    val baseBg: Color,
    val startTrackIndex: Int // Standard index on 52-cell track
) {
    RED("Red", LudoRed, LudoRedLight, LudoRedDark, LudoRedBase, 0),
    GREEN("Green", LudoGreen, LudoGreenLight, LudoGreenDark, LudoGreenBase, 13),
    YELLOW("Yellow", LudoYellow, LudoYellowLight, LudoYellowDark, LudoYellowBase, 26),
    BLUE("Blue", LudoBlue, LudoBlueLight, LudoBlueDark, LudoBlueBase, 39)
}

enum class GameMode(val label: String, val playerCount: Int) {
    VS_AI("vs Computer", 2),
    LOCAL_2P("2 Players", 2),
    LOCAL_3P("3 Players", 3),
    LOCAL_4P("4 Players", 4)
}

enum class AiDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

data class Player(
    val id: String,
    val name: String,
    val color: PlayerColor,
    val avatarUri: String? = null,
    val isAi: Boolean = false,
    val aiDifficulty: AiDifficulty = AiDifficulty.MEDIUM,
    val tokens: List<Token> = emptyList()
) {
    val completedCount: Int get() = tokens.count { it.isFinished }
    val isWinner: Boolean get() = tokens.isNotEmpty() && tokens.all { it.isFinished }
}

data class Token(
    val id: Int, // 0..3
    val playerId: String,
    val color: PlayerColor,
    val step: Int = -1, // -1 = inside base, 0..50 = track, 51..55 = home stretch, 56 = finished in Home
    val isMoving: Boolean = false,
    val isSelectable: Boolean = false
) {
    val isInBase: Boolean get() = step == -1
    val isFinished: Boolean get() = step >= 56
    val isInHomeCorridor: Boolean get() = step in 51..55
    val isOnTrack: Boolean get() = step in 0..50
}

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val tokensCaptured: Int = 0,
    val tokensFinished: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
) {
    val winRate: Int
        get() = if (gamesPlayed > 0) ((wins.toFloat() / gamesPlayed) * 100).toInt() else 0
}

data class GameSettings(
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val fastAnimations: Boolean = false,
    val confirmExit: Boolean = true
)

data class PlayerReaction(
    val emoji: String,
    val label: String,
    val playerId: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val diceValue: Int = 1,
    val isRolling: Boolean = false,
    val canRoll: Boolean = true,
    val legalTokens: Set<Pair<String, Int>> = emptySet(), // Set of (playerId, tokenId)
    val consecutiveSixes: Int = 0,
    val movingToken: Token? = null,
    val winner: Player? = null,
    val isPaused: Boolean = false,
    val showExitConfirm: Boolean = false,
    val funMessage: String? = null,
    val comboMessage: String? = null,
    val comboCount: Int = 0,
    val activeReaction: PlayerReaction? = null,
    val captureEffectCell: Pair<Int, Int>? = null, // row, col
    val homeCelebrationToken: Token? = null
) {
    val currentPlayer: Player?
        get() = players.getOrNull(currentPlayerIndex)
}
