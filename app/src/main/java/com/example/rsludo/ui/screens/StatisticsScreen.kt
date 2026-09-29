package com.example.rsludo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.model.GameStatistics
import com.example.ui.theme.*

@Composable
fun StatisticsScreen(
    statistics: GameStatistics,
    onResetStats: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RsBackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
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
                text = "CAREER STATISTICS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = RsGold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Win Rate Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RsSurfaceElevated),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RsGoldDark, RsGoldLight))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(20.dp).fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "VICTORY RATE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${statistics.winRate}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = RsGold
                    )
                    Text(
                        text = "${statistics.wins} Wins / ${statistics.losses} Losses",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(RsSurfaceVariantDark)
                        .border(2.dp, RsGold, CircleShape)
                ) {
                    Text(text = "🏆", fontSize = 32.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Key Statistics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "MATCHES",
                value = "${statistics.gamesPlayed}",
                icon = Icons.Filled.SportsEsports,
                color = LudoBlue,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "CAPTURES",
                value = "${statistics.tokensCaptured}",
                icon = Icons.Filled.FlashOn,
                color = LudoRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "CURRENT STREAK",
                value = "${statistics.currentStreak} 🔥",
                icon = Icons.Filled.LocalFireDepartment,
                color = RsGold,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "BEST STREAK",
                value = "${statistics.bestStreak} 👑",
                icon = Icons.Filled.WorkspacePremium,
                color = LudoGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Reset Statistics Button
        OutlinedButton(
            onClick = { showResetConfirm = true },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = LudoRedLight),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(imageVector = Icons.Filled.DeleteSweep, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("RESET STATISTICS", fontWeight = FontWeight.Bold)
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset All Statistics?", color = RsGold, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently clear your match wins, streaks, and captures.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onResetStats()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LudoRed)
                ) {
                    Text("RESET NOW", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("CANCEL", color = TextPrimary)
                }
            },
            containerColor = RsSurfaceDark
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(RsSurfaceElevated)
            .border(1.2.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        }
    }
}
