package com.example.rsludo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.*
import com.example.rsludo.ui.components.*
import com.example.ui.theme.*

@Composable
fun GameScreen(
    gameState: GameState,
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onRollDice: () -> Unit,
    onTokenClick: (Token) -> Unit,
    onSendReaction: (String, String) -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onExitClick: () -> Unit,
    onConfirmLeave: () -> Unit,
    onDismissLeave: () -> Unit,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept back button to confirm exit
    BackHandler {
        onExitClick()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B0F2E),
                        RsBackgroundDark,
                        Color(0xFF100720)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header: Exit, RS Ludo, Sound, Pause
            GameHeader(
                soundEnabled = soundEnabled,
                onSoundToggle = onSoundToggle,
                onPauseClick = onPauseClick,
                onExitClick = onExitClick
            )

            // Current Turn Banner
            val currentPlayer = gameState.currentPlayer
            if (currentPlayer != null) {
                CurrentTurnBanner(
                    player = currentPlayer,
                    canRoll = gameState.canRoll,
                    isRolling = gameState.isRolling,
                    hasLegalMoves = gameState.legalTokens.isNotEmpty()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Responsive Ludo Board
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                // Dynamically fit square board to width with safe margins, capped at 420.dp
                val boardSize = minOf(maxWidth - 8.dp, 420.dp)

                LudoBoardView(
                    gameState = gameState,
                    boardSize = boardSize,
                    onTokenClick = onTokenClick
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fun & Combo Announcement Banner
            AnimatedVisibility(
                visible = gameState.funMessage != null || gameState.comboMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(RsGoldDark, RsGold, RsGoldDark)))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = gameState.funMessage ?: gameState.comboMessage ?: "",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Dice & Action Section
            if (currentPlayer != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    DiceView(
                        value = gameState.diceValue,
                        isRolling = gameState.isRolling,
                        canRoll = gameState.canRoll && !currentPlayer.isAi,
                        playerColor = currentPlayer.color,
                        size = 62.dp,
                        onRoll = onRollDice
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    if (!currentPlayer.isAi) {
                        Button(
                            onClick = onRollDice,
                            enabled = gameState.canRoll && !gameState.isRolling,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentPlayer.color.primary,
                                disabledContainerColor = RsSurfaceElevated
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .widthIn(min = 140.dp)
                        ) {
                            Text(
                                text = if (gameState.isRolling) "ROLLING..." else "ROLL DICE",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp,
                                color = if (gameState.canRoll) Color.White else TextMuted
                            )
                        }
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(RsSurfaceElevated)
                                .border(1.dp, currentPlayer.color.primary, RoundedCornerShape(14.dp))
                                .padding(horizontal = 18.dp)
                        ) {
                            Text(
                                text = "BOT THINKING...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentPlayer.color.light
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Emoji Reactions
            ReactionBar(
                onSendReaction = onSendReaction,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Floating Reaction Pop-up
        FloatingReactionBubble(
            reaction = gameState.activeReaction,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-60).dp)
        )

        // Pause Modal
        if (gameState.isPaused) {
            PauseDialog(
                onResume = onResumeClick,
                onRestart = onRestartClick,
                onExit = onConfirmLeave
            )
        }

        // Confirm Exit Dialog
        if (gameState.showExitConfirm) {
            ConfirmExitDialog(
                onContinuePlaying = onDismissLeave,
                onConfirmLeave = onConfirmLeave
            )
        }

        // Winner Screen
        if (gameState.winner != null) {
            WinnerModal(
                winner = gameState.winner,
                onPlayAgain = onPlayAgain,
                onHome = onConfirmLeave
            )
        }
    }
}

@Composable
private fun CurrentTurnBanner(
    player: Player,
    canRoll: Boolean,
    isRolling: Boolean,
    hasLegalMoves: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(RsSurfaceElevated)
            .border(1.5.dp, player.color.primary, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlayerAvatar(
                color = player.color,
                avatarUri = player.avatarUri,
                isAi = player.isAi,
                size = 38.dp,
                isActive = true
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = player.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = player.color.light
                )
                Text(
                    text = when {
                        isRolling -> "Rolling dice..."
                        hasLegalMoves -> "Tap your token to move!"
                        canRoll -> "Your turn to roll!"
                        else -> "Waiting for moves..."
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }

        // Token status pills
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until 4) {
                val token = player.tokens.getOrNull(i)
                val isFinished = token?.isFinished == true
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isFinished) RsGold else player.color.primary.copy(alpha = 0.35f))
                        .border(1.dp, player.color.primary, CircleShape)
                )
            }
        }
    }
}
