package com.example.rsludo.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rsludo.model.GameStatistics
import com.example.rsludo.model.Player
import com.example.rsludo.model.PlayerColor
import com.example.ui.theme.*

@Composable
fun PlayerProfileDialog(
    player: Player,
    statistics: GameStatistics? = null,
    isCurrentActiveTurn: Boolean = false,
    isInGameInspection: Boolean = false,
    onAvatarChange: ((String) -> Unit)? = null,
    onNameChange: ((String) -> Unit)? = null,
    onSendQuickReaction: ((String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember(player.name) { mutableStateOf(player.name) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && onAvatarChange != null) {
            onAvatarChange(uri.toString())
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = player.color.primary)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E2D6A),
                            Color(0xFF0F1845),
                            Color(0xFF080C26)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(listOf(RsGoldLight, RsGold, player.color.primary, RsGoldLight)),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Header with Close icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isInGameInspection) "PLAYER MATCH INFO" else "PLAYER PROFILE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = RsGold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Avatar with Golden Laurel / Crown
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(12.dp, CircleShape, spotColor = player.color.primary)
                            .clip(CircleShape)
                            .border(3.dp, RsGold, CircleShape)
                    ) {
                        PlayerAvatar(
                            color = player.color,
                            avatarUri = player.avatarUri,
                            isAi = player.isAi,
                            size = 90.dp,
                            isActive = isCurrentActiveTurn,
                            borderWidth = 3.dp
                        )
                    }

                    // Change Photo Button (if editable and not AI)
                    if (onAvatarChange != null && !player.isAi) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(RsGoldLight, RsGoldDark)))
                                .border(1.5.dp, Color.White, CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Player Name & Color Badge
                if (isEditingName && onNameChange != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { if (it.length <= 14) editedName = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = RsGold,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier.width(180.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (editedName.isNotBlank()) {
                                    onNameChange(editedName.trim())
                                    isEditingName = false
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = "Save", tint = RsGold)
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = player.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        if (onNameChange != null && !player.isAi) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Name",
                                tint = RsGold,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { isEditingName = true }
                            )
                        }
                    }
                }

                // Subtitle: VIP status / AI difficulty
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(player.color.primary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${player.color.title} PLAYER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (player.isAi) Color(0xFF7E57C2) else RsGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (player.isAi) "BOT • ${player.aiDifficulty.name}" else "VIP MASTER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (player.isAi) Color.White else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // In-Game Live Match Stats (if inspected during game)
                if (isInGameInspection) {
                    val finishedCount = player.tokens.count { it.isFinished }
                    val onTrackCount = player.tokens.count { it.isOnTrack }
                    val inBaseCount = player.tokens.count { it.isInBase }

                    Text(
                        text = "LIVE MATCH PROGRESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RsGoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF142054))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatChip("HOME", "$finishedCount/4 🏠", highlight = finishedCount > 0)
                        VerticalDivider(modifier = Modifier.height(26.dp), color = BorderSubtle)
                        ProfileStatChip("ON TRACK", "$onTrackCount 🏃")
                        VerticalDivider(modifier = Modifier.height(26.dp), color = BorderSubtle)
                        ProfileStatChip("IN BASE", "$inBaseCount 🏰")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick in-game reaction sender to this player
                    if (onSendQuickReaction != null) {
                        Text(
                            text = "SEND REACTION TO ${player.name.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf("🔥", "👏", "😎", "🎯", "😂", "👑").forEach { emoji ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1B2C69))
                                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                        .clickable {
                                            onSendQuickReaction(emoji)
                                            onDismiss()
                                        }
                                ) {
                                    Text(text = emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }

                // Career Statistics (if available)
                if (statistics != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CAREER RECORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RsGoldLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF142054))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatChip("WINS", "${statistics.wins}", highlight = true)
                        VerticalDivider(modifier = Modifier.height(26.dp), color = BorderSubtle)
                        ProfileStatChip("WIN RATE", "${statistics.winRate}%")
                        VerticalDivider(modifier = Modifier.height(26.dp), color = BorderSubtle)
                        ProfileStatChip("CAPTURES", "${statistics.tokensCaptured} 💥")
                        VerticalDivider(modifier = Modifier.height(26.dp), color = BorderSubtle)
                        ProfileStatChip("STREAK", "${statistics.currentStreak} 🔥")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = RsGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "CLOSE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatChip(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = if (highlight) RsGold else Color.White
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
    }
}
