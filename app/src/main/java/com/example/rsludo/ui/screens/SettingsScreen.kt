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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.GameSettings
import com.example.rsludo.ui.components.RSLogo
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    settings: GameSettings,
    onSettingsChanged: (GameSettings) -> Unit,
    onOpenAdminClick: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RsBackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
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
                text = "SETTINGS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = RsGold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Audio & Haptics Group
        SectionTitle(title = "AUDIO & HAPTICS")

        SettingSwitchRow(
            title = "Sound Effects",
            subtitle = "Game sounds for dice, moves, captures, and victory",
            icon = Icons.Filled.VolumeUp,
            checked = settings.soundEnabled,
            onCheckedChange = { onSettingsChanged(settings.copy(soundEnabled = it)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingSwitchRow(
            title = "Haptic Vibration",
            subtitle = "Tactile feedback on dice roll, tap, and token collisions",
            icon = Icons.Filled.Vibration,
            checked = settings.hapticEnabled,
            onCheckedChange = { onSettingsChanged(settings.copy(hapticEnabled = it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Gameplay Preferences Group
        SectionTitle(title = "GAMEPLAY PREFERENCES")

        SettingSwitchRow(
            title = "Fast Token Animations",
            subtitle = "Accelerates step-by-step movement speed",
            icon = Icons.Filled.Speed,
            checked = settings.fastAnimations,
            onCheckedChange = { onSettingsChanged(settings.copy(fastAnimations = it)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingSwitchRow(
            title = "Confirm Match Exit",
            subtitle = "Prompt confirmation before leaving an active match",
            icon = Icons.Filled.Warning,
            checked = settings.confirmExit,
            onCheckedChange = { onSettingsChanged(settings.copy(confirmExit = it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Operator Access Card
        SectionTitle("OPERATOR ACCESS")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.5.dp, RsGold, RoundedCornerShape(16.dp))
                .clickable(onClick = onOpenAdminClick)
                .padding(16.dp)
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, RsGold, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AdminPanelSettings,
                            contentDescription = null,
                            tint = RsGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "ADMIN CONTROL PANEL",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = RsGold
                        )
                        Text(
                            text = "RNG dice rigging, server health & player management",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = RsGold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // About Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(RsSurfaceElevated)
                .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                RSLogo(size = 48.dp, showSubtitle = true, animatedGlow = false)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Production Mobile Edition",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Version 1.0.0",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = RsGold,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(RsSurfaceElevated)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(RsSurfaceVariantDark)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RsGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = RsGold,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = RsSurfaceVariantDark
            )
        )
    }
}
