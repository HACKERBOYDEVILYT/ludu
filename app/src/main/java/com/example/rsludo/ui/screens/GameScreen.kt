package com.example.rsludo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    onAskAiAdvice: () -> Unit = {},
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onExitClick: () -> Unit,
    onConfirmLeave: () -> Unit,
    onDismissLeave: () -> Unit,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inspectedPlayer by remember { mutableStateOf<Player?>(null) }

    // Intercept back button to confirm exit
    BackHandler {
        onExitClick()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF192868),
                        RsBackgroundDark,
                        Color(0xFF080C26)
                    ),
                    radius = 1200f
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

            // Online Match Server & Ping Bar (if online match)
            if (gameState.isOnlineMatch) {
                OnlineServerStatusBar(turnSeconds = gameState.turnTimerSeconds)
                Spacer(modifier = Modifier.height(4.dp))
            }

            // AI Tactical Commentator Strip
            AnimatedVisibility(
                visible = gameState.aiCommentary != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF261245), Color(0xFF152252), Color(0xFF261245))
                            )
                        )
                        .border(1.2.dp, RsGold, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = gameState.aiCommentary ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RsGoldLight
                        )
                    }
                }
            }

            // Match Players Quick Bar (Tap any player to inspect their info!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                gameState.players.forEach { p ->
                    val isCurrent = gameState.currentPlayer?.id == p.id
                    val finishedCount = p.tokens.count { it.isFinished }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCurrent) p.color.primary.copy(alpha = 0.35f) else Color(0xFF142054))
                            .border(
                                width = if (isCurrent) 1.5.dp else 1.dp,
                                color = if (isCurrent) RsGold else p.color.light.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { inspectedPlayer = p }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        PlayerAvatar(color = p.color, avatarUri = p.avatarUri, isAi = p.isAi, size = 22.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = p.name.take(6),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) RsGold else Color.White
                        )
                        if (finishedCount > 0) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = " $finishedCount👑", fontSize = 9.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Current Turn Banner
            val currentPlayer = gameState.currentPlayer
            if (currentPlayer != null) {
                CurrentTurnBanner(
                    player = currentPlayer,
                    canRoll = gameState.canRoll,
                    isRolling = gameState.isRolling,
                    hasLegalMoves = gameState.legalTokens.isNotEmpty(),
                    onClick = { inspectedPlayer = currentPlayer }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Responsive 3D Ludo Board
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                val boardSize = minOf(maxWidth - 8.dp, 420.dp)

                LudoBoardView(
                    gameState = gameState,
                    boardSize = boardSize,
                    onTokenClick = onTokenClick,
                    onPlayerBaseClick = { inspectedPlayer = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI Strategic Move Advice Banner
            AnimatedVisibility(
                visible = gameState.aiAdviceMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F2B48))
                        .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = gameState.aiAdviceMessage ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE0F7FA)
                        )
                    }
                }
            }

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

                    Spacer(modifier = Modifier.width(14.dp))

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
                                .widthIn(min = 130.dp)
                        ) {
                            Text(
                                text = if (gameState.isRolling) "ROLLING..." else "ROLL DICE",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp,
                                color = if (gameState.canRoll) Color.White else TextMuted
                            )
                        }

                        // AI Move Advice Button (Available when player has legal moves)
                        if (gameState.legalTokens.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = onAskAiAdvice,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0069C0)))
                                    )
                                    .border(1.5.dp, Color.White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = "Ask AI Advice",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
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

            Spacer(modifier = Modifier.height(10.dp))

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

        // In-Game Player Profile Inspection Dialog
        val currentInspected = inspectedPlayer
        if (currentInspected != null) {
            PlayerProfileDialog(
                player = currentInspected,
                isInGameInspection = true,
                isCurrentActiveTurn = gameState.currentPlayer?.id == currentInspected.id,
                onSendQuickReaction = { emoji -> onSendReaction(emoji, "Direct") },
                onDismiss = { inspectedPlayer = null }
            )
        }
    }
}

@Composable
private fun OnlineServerStatusBar(turnSeconds: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1B47))
            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Live Server • 28ms",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Turn Timer
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = null,
                tint = if (turnSeconds <= 5) LudoRedLight else RsGold,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${turnSeconds}s Turn",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (turnSeconds <= 5) LudoRedLight else RsGold
            )
        }
    }
}

@Composable
private fun CurrentTurnBanner(
    player: Player,
    canRoll: Boolean,
    isRolling: Boolean,
    hasLegalMoves: Boolean,
    onClick: () -> Unit = {}
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
            .clickable(onClick = onClick)
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
                        hasLegalMoves -> "Tap your piece to move!"
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
