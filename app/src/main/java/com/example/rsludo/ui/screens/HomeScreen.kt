package com.example.rsludo.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.GameMode
import com.example.rsludo.model.GameStatistics
import com.example.rsludo.ui.components.RSLogo
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    statistics: GameStatistics,
    onPlayClick: (GameMode) -> Unit,
    onPlayerSetupClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1D0E30),
                        RsBackgroundDark,
                        Color(0xFF0F071A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top luxury header
        RSLogo(size = 64.dp, showSubtitle = true, animatedGlow = true)

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Stats Summary Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(RsSurfaceElevated)
                .border(1.2.dp, BorderSubtle, RoundedCornerShape(18.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(label = "GAMES", value = "${statistics.gamesPlayed}")
            VerticalDivider(modifier = Modifier.height(28.dp), color = BorderSubtle)
            StatItem(label = "WINS", value = "${statistics.wins}", highlight = true)
            VerticalDivider(modifier = Modifier.height(28.dp), color = BorderSubtle)
            StatItem(label = "WIN RATE", value = "${statistics.winRate}%")
            VerticalDivider(modifier = Modifier.height(28.dp), color = BorderSubtle)
            StatItem(label = "STREAK", value = "${statistics.currentStreak} 🔥")
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Primary Action: PLAY LOCAL MULTIPLAYER
        HomeMenuButton(
            title = "PLAY GAME",
            subtitle = "2 - 4 Players Local Match",
            icon = Icons.Filled.PlayArrow,
            gradient = listOf(RsGold, RsGoldDark),
            textColor = Color.Black,
            onClick = { onPlayClick(GameMode.LOCAL_2P) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Fast Action: VS COMPUTER
        HomeMenuButton(
            title = "VS COMPUTER",
            subtitle = "Challenge Smart AI Master",
            icon = Icons.Filled.SmartToy,
            gradient = listOf(LudoGreenLight, LudoGreenDark),
            textColor = Color.White,
            onClick = { onPlayClick(GameMode.VS_AI) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // PLAYER SETUP & CUSTOM PHOTOS
        HomeMenuButton(
            title = "PLAYER SETUP & AVATARS",
            subtitle = "Custom Photos, Colors & Names",
            icon = Icons.Filled.AccountCircle,
            gradient = listOf(LudoBlueLight, LudoBlueDark),
            textColor = Color.White,
            onClick = onPlayerSetupClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Secondary Row: Statistics & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryMenuCard(
                title = "STATISTICS",
                icon = Icons.Filled.EmojiEvents,
                onClick = onStatsClick,
                modifier = Modifier.weight(1f)
            )

            SecondaryMenuCard(
                title = "SETTINGS",
                icon = Icons.Filled.Settings,
                onClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Online Multiplayer - Coming Soon Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(RsSurfaceVariantDark.copy(alpha = 0.6f))
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ONLINE MULTIPLAYER — COMING SOON",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = if (highlight) RsGold else TextPrimary
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun HomeMenuButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(18.dp), spotColor = gradient.first())
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(gradient))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp)
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = textColor
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor.copy(alpha = 0.85f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SecondaryMenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(RsSurfaceElevated)
            .border(1.2.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RsGold,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.5.sp
            )
        }
    }
}
