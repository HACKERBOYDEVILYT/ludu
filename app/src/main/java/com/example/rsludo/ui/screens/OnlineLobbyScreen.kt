package com.example.rsludo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.Player
import com.example.rsludo.model.PlayerColor
import com.example.rsludo.online.ConnectionState
import com.example.rsludo.online.OnlineServerState
import com.example.rsludo.ui.components.PlayerAvatar
import com.example.ui.theme.*

@Composable
fun OnlineLobbyScreen(
    localPlayer: Player,
    onlineState: OnlineServerState,
    onQuickMatch: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    onStartMatch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var joinRoomCodeInput by remember { mutableStateOf("") }
    var showJoinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F1B47),
                        Color(0xFF080D26),
                        Color(0xFF030512)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(RsSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ONLINE ARENA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = RsGold
                    )
                    Text(
                        text = "Global Live Matchmaking",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Live Server Ping Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF142459))
                    .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${onlineState.pingMs}ms",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // State: SEARCHING
        if (onlineState.connectionState == ConnectionState.SEARCHING) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = RsGold,
                        modifier = Modifier.size(56.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "FINDING MATCH...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Connecting with online players globally",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        } else if (onlineState.connectionState == ConnectionState.LOBBY) {
            // State: LOBBY
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ROOM CODE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = onlineState.roomCode ?: "RS-ROOM",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    color = RsGold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "PLAYERS IN LOBBY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lobby player slots
                onlineState.players.forEach { p ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF142254))
                            .border(1.5.dp, p.color.primary, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PlayerAvatar(
                                color = p.color,
                                avatarUri = p.avatarUri,
                                size = 42.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = p.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${p.color.title} Player",
                                    fontSize = 11.sp,
                                    color = p.color.light
                                )
                            }
                        }

                        Text(
                            text = "READY ✓",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E676)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onStartMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = RsGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "START ONLINE MATCH",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        } else {
            // Main Online Options
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Quick Match Option
                OnlineActionCard(
                    title = "QUICK MATCH",
                    subtitle = "Instant live matchmaking with random players",
                    icon = Icons.Filled.FlashOn,
                    gradient = listOf(Color(0xFF00E5FF), Color(0xFF0069C0)),
                    badgeText = "POPULAR",
                    onClick = onQuickMatch
                )

                // Create Private Room
                OnlineActionCard(
                    title = "CREATE PRIVATE ROOM",
                    subtitle = "Play with your friends using a Room Code",
                    icon = Icons.Filled.AddCircle,
                    gradient = listOf(Color(0xFF00E676), Color(0xFF009624)),
                    badgeText = "CUSTOM",
                    onClick = onCreateRoom
                )

                // Join Room
                OnlineActionCard(
                    title = "JOIN WITH CODE",
                    subtitle = "Enter your friend's 6-digit Room Code",
                    icon = Icons.Filled.MeetingRoom,
                    gradient = listOf(Color(0xFFFFB300), Color(0xFFE65100)),
                    badgeText = "ENTER",
                    onClick = { showJoinDialog = true }
                )
            }
        }
    }

    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = {
                Text(
                    text = "ENTER ROOM CODE",
                    fontWeight = FontWeight.Black,
                    color = RsGold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter the 6-character room code shared by your friend:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = joinRoomCodeInput,
                        onValueChange = { joinRoomCodeInput = it.uppercase() },
                        placeholder = { Text("e.g. RS-4921", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RsGold,
                            unfocusedBorderColor = BorderSubtle,
                            cursorColor = RsGold,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (joinRoomCodeInput.isNotBlank()) {
                            showJoinDialog = false
                            onJoinRoom(joinRoomCodeInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RsGold)
                ) {
                    Text("JOIN ROOM", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("CANCEL", color = TextPrimary)
                }
            },
            containerColor = Color(0xFF141E4D)
        )
    }
}

@Composable
private fun OnlineActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    badgeText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(gradient))
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
