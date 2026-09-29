package com.example.rsludo.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.AiDifficulty
import com.example.rsludo.model.GameMode
import com.example.rsludo.model.Player
import com.example.rsludo.ui.components.PlayerAvatar
import com.example.ui.theme.*

@Composable
fun PlayerSetupScreen(
    players: List<Player>,
    selectedMode: GameMode,
    onModeChange: (GameMode) -> Unit,
    onPlayerUpdate: (Int, (Player) -> Player) -> Unit,
    onAvatarSelected: (String, Uri) -> Unit,
    onAvatarRemoved: (String) -> Unit,
    onStartGame: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activePickingPlayerId by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && activePickingPlayerId != null) {
            onAvatarSelected(activePickingPlayerId!!, uri)
        }
        activePickingPlayerId = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RsBackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
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
            Text(
                text = "PLAYER SETUP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = RsGold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Selector: 2P, 3P, 4P, VS AI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RsSurfaceElevated)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                GameMode.VS_AI,
                GameMode.LOCAL_2P,
                GameMode.LOCAL_3P,
                GameMode.LOCAL_4P
            ).forEach { mode ->
                val isSelected = mode == selectedMode
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) RsGold else Color.Transparent)
                        .clickable { onModeChange(mode) }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = when (mode) {
                            GameMode.VS_AI -> "AI"
                            GameMode.LOCAL_2P -> "2P"
                            GameMode.LOCAL_3P -> "3P"
                            GameMode.LOCAL_4P -> "4P"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active players list
        val activeCount = selectedMode.playerCount
        val activePlayers = players.take(activeCount)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(activePlayers) { index, player ->
                PlayerConfigCard(
                    player = player,
                    onNameChange = { newName ->
                        onPlayerUpdate(index) { it.copy(name = newName) }
                    },
                    onAiToggle = { isAi ->
                        onPlayerUpdate(index) { it.copy(isAi = isAi) }
                    },
                    onDifficultyChange = { diff ->
                        onPlayerUpdate(index) { it.copy(aiDifficulty = diff) }
                    },
                    onPickPhoto = {
                        activePickingPlayerId = player.id
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemovePhoto = {
                        onAvatarRemoved(player.id)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // START MATCH BUTTON
        Button(
            onClick = onStartGame,
            colors = ButtonDefaults.buttonColors(containerColor = RsGold),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "START MATCH",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun PlayerConfigCard(
    player: Player,
    onNameChange: (String) -> Unit,
    onAiToggle: (Boolean) -> Unit,
    onDifficultyChange: (AiDifficulty) -> Unit,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = RsSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(player.color.primary, RsSurfaceVariantDark))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar with player color ring
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayerAvatar(
                        color = player.color,
                        avatarUri = player.avatarUri,
                        isAi = player.isAi,
                        size = 52.dp,
                        borderWidth = 2.5.dp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "${player.color.title} Player",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = player.color.light
                        )
                        OutlinedTextField(
                            value = player.name,
                            onValueChange = onNameChange,
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = player.color.primary,
                                unfocusedBorderColor = BorderSubtle,
                                cursorColor = RsGold
                            ),
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }

                // AI / Human switch
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (player.isAi) "BOT" else "HUMAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (player.isAi) LudoGreenLight else RsGoldLight
                    )
                    Switch(
                        checked = player.isAi,
                        onCheckedChange = onAiToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = LudoGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = RsSurfaceVariantDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Custom Photo buttons + AI Difficulty selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Photo management
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onPickPhoto,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = RsGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (player.avatarUri != null) "Change Photo" else "Add Photo",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }

                    if (player.avatarUri != null) {
                        IconButton(
                            onClick = onRemovePhoto,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove photo",
                                tint = LudoRedLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // If AI, difficulty options
                if (player.isAi) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RsSurfaceVariantDark)
                            .padding(2.dp)
                    ) {
                        AiDifficulty.entries.forEach { diff ->
                            val isChosen = player.aiDifficulty == diff
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChosen) player.color.primary else Color.Transparent)
                                    .clickable { onDifficultyChange(diff) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = diff.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) Color.Black else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
