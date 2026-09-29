package com.example.rsludo.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rsludo.admin.AdminManager
import com.example.rsludo.admin.DiceRigMode
import com.example.rsludo.ai.GeminiAiService
import com.example.rsludo.audio.SoundManager
import com.example.rsludo.data.PreferencesManager
import com.example.rsludo.game.LudoEngine
import com.example.rsludo.model.*
import com.example.rsludo.online.OnlineServerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class LudoViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SoundManager(application)
    val prefsManager = PreferencesManager(application)

    private val _settings = MutableStateFlow(prefsManager.loadSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    private val _statistics = MutableStateFlow(prefsManager.loadStatistics())
    val statistics: StateFlow<GameStatistics> = _statistics.asStateFlow()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Setup configuration
    val selectedMode = MutableStateFlow(GameMode.VS_AI)
    val configuredPlayers = MutableStateFlow(defaultPlayers())

    val onlineServerManager = OnlineServerManager()
    val adminManager = AdminManager(application)

    private var aiJob: Job? = null
    private var reactionJob: Job? = null
    private var funMessageJob: Job? = null
    private var aiCommentaryJob: Job? = null

    init {
        soundManager.soundEnabled = _settings.value.soundEnabled
        soundManager.hapticEnabled = _settings.value.hapticEnabled
    }

    private fun defaultPlayers(): List<Player> {
        return listOf(
            Player(id = "p1", name = "Player 1", color = PlayerColor.RED, isAi = false),
            Player(id = "p2", name = "AI Master", color = PlayerColor.GREEN, isAi = true, aiDifficulty = AiDifficulty.MEDIUM),
            Player(id = "p3", name = "Player 3", color = PlayerColor.YELLOW, isAi = false),
            Player(id = "p4", name = "Player 4", color = PlayerColor.BLUE, isAi = false)
        )
    }

    fun updateSettings(newSettings: GameSettings) {
        _settings.value = newSettings
        soundManager.soundEnabled = newSettings.soundEnabled
        soundManager.hapticEnabled = newSettings.hapticEnabled
        prefsManager.saveSettings(newSettings)
    }

    fun resetStats() {
        prefsManager.resetStatistics()
        _statistics.value = prefsManager.loadStatistics()
    }

    fun updatePlayerConfig(index: Int, update: (Player) -> Player) {
        configuredPlayers.update { current ->
            current.mapIndexed { i, p -> if (i == index) update(p) else p }
        }
    }

    fun setPlayerAvatar(playerId: String, imageUri: Uri) {
        val savedPath = prefsManager.savePlayerAvatar(playerId, imageUri)
        configuredPlayers.update { list ->
            list.map { if (it.id == playerId) it.copy(avatarUri = savedPath) else it }
        }
    }

    fun removePlayerAvatar(playerId: String) {
        prefsManager.removePlayerAvatar(playerId)
        configuredPlayers.update { list ->
            list.map { if (it.id == playerId) it.copy(avatarUri = null) else it }
        }
    }

    /**
     * Starts a new match with the configured players based on selectedMode
     */
    fun startNewGame() {
        aiJob?.cancel()
        val count = selectedMode.value.playerCount
        val activeConfigs = configuredPlayers.value.take(count)

        // Initialize players with 4 tokens each
        val initialPlayers = activeConfigs.map { config ->
            val tokens = (0..3).map { tokenId ->
                Token(
                    id = tokenId,
                    playerId = config.id,
                    color = config.color,
                    step = -1
                )
            }
            config.copy(tokens = tokens)
        }

        _gameState.value = GameState(
            players = initialPlayers,
            currentPlayerIndex = 0,
            diceValue = 1,
            isRolling = false,
            canRoll = true,
            legalTokens = emptySet(),
            consecutiveSixes = 0,
            winner = null,
            isPaused = false
        )

        // Check if first player is AI
        checkAiTurn()
    }

    fun startOnlineGame(onlinePlayers: List<Player>) {
        aiJob?.cancel()
        val initialPlayers = onlinePlayers.map { config ->
            val tokens = (0..3).map { tokenId ->
                Token(
                    id = tokenId,
                    playerId = config.id,
                    color = config.color,
                    step = -1
                )
            }
            config.copy(tokens = tokens)
        }

        _gameState.value = GameState(
            players = initialPlayers,
            currentPlayerIndex = 0,
            diceValue = 1,
            isRolling = false,
            canRoll = true,
            legalTokens = emptySet(),
            consecutiveSixes = 0,
            winner = null,
            isPaused = false,
            isOnlineMatch = true
        )
        onlineServerManager.startTurnTimer()
    }

    fun askAiMoveAdvice() {
        val state = _gameState.value
        val player = state.currentPlayer ?: return
        val legalTokens = player.tokens.filter { state.legalTokens.contains(Pair(it.playerId, it.id)) }
        if (legalTokens.isEmpty()) return

        soundManager.playTokenTap()
        viewModelScope.launch {
            _gameState.update { it.copy(aiAdviceMessage = "AI Grandmaster analyzing board... 🧠") }
            val (_, advice) = GeminiAiService.getStrategicMoveAdvice(state, legalTokens)
            _gameState.update { it.copy(aiAdviceMessage = advice) }
            delay(4500)
            _gameState.update { it.copy(aiAdviceMessage = null) }
        }
    }

    private fun triggerAiCommentary(event: String, player: Player, dice: Int = 1) {
        aiCommentaryJob?.cancel()
        aiCommentaryJob = viewModelScope.launch {
            val commentary = GeminiAiService.getAiCommentary(event, player, dice)
            _gameState.update { it.copy(aiCommentary = commentary) }
            delay(3500)
            _gameState.update { it.copy(aiCommentary = null) }
        }
    }

    fun rollDice() {
        val state = _gameState.value
        if (!state.canRoll || state.isRolling || state.winner != null || state.isPaused) return

        val currentPlayer = state.currentPlayer ?: return

        viewModelScope.launch {
            _gameState.update { it.copy(isRolling = true, canRoll = false, legalTokens = emptySet()) }
            soundManager.playDice()

            // Realistic rolling animation loop
            for (i in 0 until 6) {
                _gameState.update { it.copy(diceValue = Random.nextInt(1, 7)) }
                delay(60)
            }

            val rigMode = adminManager.state.value.diceRigMode
            val finalDice = when (rigMode) {
                DiceRigMode.FORCE_SIX -> 6
                DiceRigMode.LUCKY_SIX_BOOST -> if (Random.nextFloat() < 0.35f) 6 else Random.nextInt(1, 7)
                DiceRigMode.HIGH_ROLLS -> Random.nextInt(4, 7)
                DiceRigMode.FAIR -> Random.nextInt(1, 7)
            }
            val newSixCount = if (finalDice == 6) state.consecutiveSixes + 1 else 0

            var funMsg: String? = null
            if (finalDice == 6) {
                funMsg = "LUCKY 6! 🎲"
                triggerAiCommentary("SIX", currentPlayer, 6)
            }

            _gameState.update {
                it.copy(
                    diceValue = finalDice,
                    isRolling = false,
                    consecutiveSixes = newSixCount,
                    funMessage = funMsg
                )
            }

            if (funMsg != null) showFunMessage(funMsg)

            // Penalty for 3 consecutive sixes
            if (newSixCount >= 3) {
                showFunMessage("3 SIXES! TURN LOST ⚠️")
                delay(1000)
                advanceTurn()
                return@launch
            }

            // Calculate legal moves
            val legalMoves = LudoEngine.findLegalMoves(currentPlayer, finalDice)
            if (legalMoves.isEmpty()) {
                showFunMessage("NO MOVES 🚫")
                delay(950)
                advanceTurn()
            } else {
                val legalSet = legalMoves.map { Pair(it.playerId, it.id) }.toSet()
                _gameState.update { it.copy(legalTokens = legalSet) }

                // If only 1 move and it's AI, or if AI turn
                if (currentPlayer.isAi) {
                    delay(700)
                    val chosen = LudoEngine.selectAiMove(currentPlayer, finalDice, _gameState.value.players)
                    if (chosen != null) {
                        moveToken(chosen)
                    } else {
                        advanceTurn()
                    }
                }
            }
        }
    }

    fun onTokenTapped(token: Token) {
        val state = _gameState.value
        if (state.isRolling || state.winner != null || state.isPaused) return
        val isLegal = state.legalTokens.contains(Pair(token.playerId, token.id))
        if (!isLegal) return

        soundManager.playTokenTap()
        moveToken(token)
    }

    private fun moveToken(token: Token) {
        val state = _gameState.value
        val dice = state.diceValue
        val targetStep = LudoEngine.calculateNextStep(token, dice)

        viewModelScope.launch {
            _gameState.update { it.copy(legalTokens = emptySet(), canRoll = false) }

            val startStep = token.step
            val stepDelay = if (_settings.value.fastAnimations) 100L else 180L

            if (startStep == -1) {
                // Moving out from base to step 0
                soundManager.playTokenMove(0)
                delay(stepDelay)
                updateSingleToken(token.copy(step = 0))
            } else {
                // Step-by-step animated progression
                for (s in (startStep + 1)..targetStep) {
                    soundManager.playTokenMove(s)
                    updateSingleToken(token.copy(step = s))
                    delay(stepDelay)
                }
            }

            val arrivedToken = token.copy(step = targetStep)
            handleArrival(arrivedToken, dice)
        }
    }

    private fun updateSingleToken(updatedToken: Token) {
        _gameState.update { state ->
            val updatedPlayers = state.players.map { player ->
                if (player.id == updatedToken.playerId) {
                    val updatedTokens = player.tokens.map { t ->
                        if (t.id == updatedToken.id) updatedToken else t
                    }
                    player.copy(tokens = updatedTokens)
                } else {
                    player
                }
            }
            state.copy(players = updatedPlayers)
        }
    }

    private suspend fun handleArrival(token: Token, diceValue: Int) {
        val state = _gameState.value
        var bonusTurn = false

        // 1. Check if token reached Home (step 56)
        if (token.step == LudoEngine.MAX_STEP) {
            soundManager.playHome()
            showFunMessage("HOME! 🏠")
            triggerAiCommentary("HOME", state.players.first { it.id == token.playerId }, diceValue)
            bonusTurn = true
            _gameState.update { it.copy(homeCelebrationToken = token) }
            delay(800)
            _gameState.update { it.copy(homeCelebrationToken = null) }
        }

        // 2. Check if token captured an opponent
        val captured = LudoEngine.findCapturedToken(token, token.step, state.players)
        if (captured != null) {
            soundManager.playCapture()
            showFunMessage("BOOM! CAPTURE! 💥")
            triggerAiCommentary("CAPTURE", state.players.first { it.id == token.playerId }, diceValue)
            bumpCombo()
            bonusTurn = true

            // Send captured token back to base (-1)
            val resetCaptured = captured.copy(step = -1)
            updateSingleToken(resetCaptured)
            delay(600)
        }

        // 3. Check for Game Victory
        val updatedCurrentPlayer = _gameState.value.players.first { it.id == token.playerId }
        if (updatedCurrentPlayer.isWinner) {
            handleVictory(updatedCurrentPlayer)
            return
        }

        // 4. Bonus turn for 6
        if (diceValue == 6) {
            bonusTurn = true
        }

        if (bonusTurn) {
            _gameState.update { it.copy(canRoll = true, consecutiveSixes = if (diceValue == 6) it.consecutiveSixes else 0) }
            checkAiTurn()
        } else {
            advanceTurn()
        }
    }

    private fun bumpCombo() {
        val newCombo = _gameState.value.comboCount + 1
        val comboMsg = when (newCombo) {
            1 -> "NICE! ✨"
            2 -> "GREAT! 🌟"
            3 -> "ON FIRE! 🔥"
            else -> "UNSTOPPABLE! ⚡"
        }
        _gameState.update { it.copy(comboCount = newCombo, comboMessage = comboMsg) }
    }

    private fun advanceTurn() {
        val state = _gameState.value
        val nextIndex = (state.currentPlayerIndex + 1) % state.players.size
        _gameState.update {
            it.copy(
                currentPlayerIndex = nextIndex,
                canRoll = true,
                isRolling = false,
                legalTokens = emptySet(),
                consecutiveSixes = 0,
                comboCount = 0,
                comboMessage = null
            )
        }
        soundManager.playTurn()
        checkAiTurn()
    }

    private fun checkAiTurn() {
        aiJob?.cancel()
        val state = _gameState.value
        val player = state.currentPlayer ?: return

        if (player.isAi && state.winner == null && !state.isPaused) {
            aiJob = viewModelScope.launch {
                delay(750)
                if (_gameState.value.canRoll) {
                    rollDice()
                }
            }
        }
    }

    private fun handleVictory(winner: Player) {
        soundManager.playWin()
        _gameState.update { it.copy(winner = winner, canRoll = false) }

        val isHumanWin = !winner.isAi
        val capturedCount = _gameState.value.comboCount
        val finishedCount = winner.completedCount

        prefsManager.recordGameFinished(isHumanWin, capturedCount, finishedCount)
        _statistics.value = prefsManager.loadStatistics()
    }

    fun showFunMessage(msg: String) {
        funMessageJob?.cancel()
        _gameState.update { it.copy(funMessage = msg) }
        funMessageJob = viewModelScope.launch {
            delay(1500)
            _gameState.update { it.copy(funMessage = null) }
        }
    }

    fun sendReaction(emoji: String, label: String) {
        val player = _gameState.value.currentPlayer ?: return
        soundManager.playReaction()
        reactionJob?.cancel()
        _gameState.update {
            it.copy(activeReaction = PlayerReaction(emoji, label, player.id))
        }
        reactionJob = viewModelScope.launch {
            delay(2200)
            _gameState.update { it.copy(activeReaction = null) }
        }
    }

    fun togglePause(pause: Boolean) {
        _gameState.update { it.copy(isPaused = pause) }
        if (!pause) {
            checkAiTurn()
        }
    }

    fun setExitConfirm(show: Boolean) {
        _gameState.update { it.copy(showExitConfirm = show) }
    }
}
