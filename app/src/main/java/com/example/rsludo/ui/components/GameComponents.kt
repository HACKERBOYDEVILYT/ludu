package com.example.rsludo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.window.Dialog
import com.example.rsludo.model.GameState
import com.example.rsludo.model.Player
import com.example.rsludo.model.PlayerReaction
import com.example.ui.theme.*

@Composable
fun GameHeader(
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onPauseClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Exit / Back
        IconButton(
            onClick = onExitClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RsSurfaceElevated)
                .border(1.dp, BorderSubtle, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Exit game",
                tint = TextPrimary
            )
        }

        // Center RS Ludo Branding Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(RsSurfaceElevated)
                .border(1.2.dp, RsGoldDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            RSLogo(size = 24.dp, showSubtitle = false, animatedGlow = false)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RS LUDO",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = RsGold
            )
        }

        // Right actions: Sound & Pause
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onSoundToggle,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(RsSurfaceElevated)
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    contentDescription = "Toggle sound",
                    tint = if (soundEnabled) RsGold else TextMuted
                )
            }

            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(RsSurfaceElevated)
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = "Pause match",
                    tint = TextPrimary
                )
            }
        }
    }
}

@Composable
fun ReactionBar(
    onSendReaction: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val reactions = listOf(
        Pair("😂", "Haha"),
        Pair("😎", "Cool"),
        Pair("🔥", "Fire"),
        Pair("😭", "Cry"),
        Pair("😱", "Shock"),
        Pair("👏", "Clap"),
        Pair("👑", "King"),
        Pair("GG", "GG"),
        Pair("Nice!", "Nice")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(reactions) { (emoji, label) ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(38.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(RsSurfaceElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(19.dp))
                    .clickable { onSendReaction(emoji, label) }
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun FloatingReactionBubble(
    reaction: PlayerReaction?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = reaction != null,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        if (reaction != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(RsSurfaceElevated, RsSurfaceDark)))
                    .border(2.dp, RsGold, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = reaction.emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reaction.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RsGold
                    )
                }
            }
        }
    }
}

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .background(RsSurfaceDark)
                .border(2.dp, RsGold, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "GAME PAUSED",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = RsGold
                )

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = RsGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(text = "RESUME", fontWeight = FontWeight.Bold, color = Color.Black)
                }

                OutlinedButton(
                    onClick = onRestart,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(text = "RESTART MATCH", color = TextPrimary)
                }

                TextButton(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "EXIT TO HOME", color = LudoRedLight)
                }
            }
        }
    }
}

@Composable
fun ConfirmExitDialog(
    onContinuePlaying: () -> Unit,
    onConfirmLeave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onContinuePlaying,
        title = {
            Text(
                text = "LEAVE MATCH?",
                fontWeight = FontWeight.Bold,
                color = RsGold
            )
        },
        text = {
            Text(
                text = "Your current game progress will be lost. Are you sure you want to return to Home?",
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmLeave,
                colors = ButtonDefaults.buttonColors(containerColor = LudoRed)
            ) {
                Text("LEAVE GAME", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onContinuePlaying) {
                Text("CONTINUE PLAYING", color = TextPrimary)
            }
        },
        containerColor = RsSurfaceDark
    )
}

@Composable
fun WinnerModal(
    winner: Player,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF281744),
                            RsSurfaceDark
                        )
                    )
                )
                .border(2.5.dp, Brush.linearGradient(listOf(RsGoldLight, RsGold, RsGoldDark)), RoundedCornerShape(26.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🏆", fontSize = 48.sp)

                Text(
                    text = "VICTORY!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = RsGold
                )

                // Winner Avatar with colored halo
                PlayerAvatar(
                    color = winner.color,
                    avatarUri = winner.avatarUri,
                    isAi = winner.isAi,
                    size = 72.dp,
                    isActive = true,
                    borderWidth = 3.dp
                )

                Text(
                    text = winner.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = winner.color.light
                )

                Text(
                    text = "All 4 tokens conquered the Home Base!",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = RsGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(
                        text = "PLAY AGAIN",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                OutlinedButton(
                    onClick = onHome,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(text = "BACK TO HOME", color = TextPrimary)
                }
            }
        }
    }
}
