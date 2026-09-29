package com.example.rsludo.game

import com.example.rsludo.model.*
import kotlin.random.Random

object LudoEngine {

    const val MAX_STEP = 56

    fun canMove(token: Token, diceValue: Int): Boolean {
        if (token.isFinished) return false
        if (token.isInBase) return diceValue == 6
        return token.step + diceValue <= MAX_STEP
    }

    fun calculateNextStep(token: Token, diceValue: Int): Int {
        if (token.isInBase) {
            return if (diceValue == 6) 0 else -1
        }
        val target = token.step + diceValue
        return if (target <= MAX_STEP) target else token.step
    }

    fun findLegalMoves(player: Player, diceValue: Int): List<Token> {
        return player.tokens.filter { canMove(it, diceValue) }
    }

    /**
     * Checks if moving this token to `targetStep` captures any opponent token.
     * Opponent tokens inside safe cells, home corridors, or bases cannot be captured.
     */
    fun findCapturedToken(
        movingToken: Token,
        targetStep: Int,
        allPlayers: List<Player>
    ): Token? {
        val targetGlobalIndex = LudoBoardCoordinates.getGlobalTrackIndex(movingToken.color, targetStep)
            ?: return null

        if (LudoBoardCoordinates.isSafeGlobalIndex(targetGlobalIndex)) {
            return null // Safe cell, no capture
        }

        for (player in allPlayers) {
            if (player.id == movingToken.playerId) continue
            for (token in player.tokens) {
                if (token.isOnTrack) {
                    val otherGlobal = LudoBoardCoordinates.getGlobalTrackIndex(token.color, token.step)
                    if (otherGlobal == targetGlobalIndex) {
                        return token
                    }
                }
            }
        }
        return null
    }

    /**
     * AI Decision Engine
     */
    fun selectAiMove(
        aiPlayer: Player,
        diceValue: Int,
        allPlayers: List<Player>
    ): Token? {
        val legalMoves = findLegalMoves(aiPlayer, diceValue)
        if (legalMoves.isEmpty()) return null
        if (legalMoves.size == 1) return legalMoves.first()

        return when (aiPlayer.aiDifficulty) {
            AiDifficulty.EASY -> legalMoves.random(Random)

            AiDifficulty.MEDIUM -> {
                // 1. Capture opponent
                val captureMove = legalMoves.firstOrNull { token ->
                    val targetStep = calculateNextStep(token, diceValue)
                    findCapturedToken(token, targetStep, allPlayers) != null
                }
                if (captureMove != null) return captureMove

                // 2. Reach finish/Home (step 56)
                val winMove = legalMoves.firstOrNull { calculateNextStep(it, diceValue) == MAX_STEP }
                if (winMove != null) return winMove

                // 3. Bring token out of base with 6
                if (diceValue == 6) {
                    val baseToken = legalMoves.firstOrNull { it.isInBase }
                    if (baseToken != null) return baseToken
                }

                // 4. Enter home corridor
                val enterCorridor = legalMoves.firstOrNull {
                    it.isOnTrack && calculateNextStep(it, diceValue) > 50
                }
                if (enterCorridor != null) return enterCorridor

                // 5. Advance closest to home
                legalMoves.maxByOrNull { it.step } ?: legalMoves.first()
            }

            AiDifficulty.HARD -> {
                // Score-based evaluation for hard AI
                legalMoves.maxByOrNull { token ->
                    val nextStep = calculateNextStep(token, diceValue)
                    var score = 0

                    // Reaching final home is top priority
                    if (nextStep == MAX_STEP) score += 1000

                    // Capturing opponent
                    val captured = findCapturedToken(token, nextStep, allPlayers)
                    if (captured != null) {
                        score += 500 + captured.step * 5
                    }

                    // Bringing token out of base
                    if (token.isInBase && nextStep == 0) {
                        score += 300
                    }

                    // Entering safe home corridor
                    if (nextStep in 51..55) {
                        score += 250 + nextStep * 4
                    }

                    // Landing on safe star cell
                    val globalIdx = LudoBoardCoordinates.getGlobalTrackIndex(token.color, nextStep)
                    if (globalIdx != null && LudoBoardCoordinates.isSafeGlobalIndex(globalIdx)) {
                        score += 150
                    }

                    // Penalty if landing in danger (opponent within 1..6 steps behind)
                    if (globalIdx != null && !LudoBoardCoordinates.isSafeGlobalIndex(globalIdx)) {
                        for (otherPlayer in allPlayers) {
                            if (otherPlayer.id == aiPlayer.id) continue
                            for (otherToken in otherPlayer.tokens) {
                                if (otherToken.isOnTrack) {
                                    val otherGlobal = LudoBoardCoordinates.getGlobalTrackIndex(otherToken.color, otherToken.step)
                                    if (otherGlobal != null) {
                                        val distance = (globalIdx - otherGlobal + 52) % 52
                                        if (distance in 1..6) {
                                            score -= (7 - distance) * 25
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // General progress forward
                    score += nextStep * 2
                    score
                } ?: legalMoves.first()
            }
        }
    }
}
