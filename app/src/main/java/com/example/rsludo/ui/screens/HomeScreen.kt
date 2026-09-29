package com.example.rsludo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rsludo.model.GameMode
import com.example.rsludo.model.GameStatistics
import com.example.rsludo.model.Player
import com.example.rsludo.model.PlayerColor
import com.example.rsludo.ui.components.PlayerAvatar
import com.example.rsludo.ui.components.PlayerProfileDialog
import com.example.rsludo.ui.components.RSLogo
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    player: Player?,
    statistics: GameStatistics,
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onPlayClick: (GameMode) -> Unit,
    onPlayOnlineClick: () -> Unit = {},
    onPlayerSetupClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBonusClaimed: () -> Unit = {},
    onAvatarChange: ((String) -> Unit)? = null,
    onNameChange: ((String) -> Unit)? = null,
    onOpenAdminClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPlayerSelectDialog by remember { mutableStateOf(false) }
    var showOnlineSoonDialog by remember { mutableStateOf(false) }
    var showClaimBonusDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Shimmer and pulse animations
    val infiniteTransition = rememberInfiniteTransition(label = "ludo_king_anim")
    val crownScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crown_scale"
    )

    val bonusGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bonus_glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF162566), // Rich royal blue gaming glow
                        Color(0xFF0C133B),
                        Color(0xFF06091F)  // Deep gaming perimeter
                    ),
                    radius = 1200f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Decorative background stars and rays
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.35f)
            // Subtle radial burst rays
            for (i in 0 until 12) {
                val angle = (i * 30.0) * Math.PI / 180.0
                val end = Offset(
                    (center.x + Math.cos(angle) * size.width * 1.5).toFloat(),
                    (center.y + Math.sin(angle) * size.height * 1.5).toFloat()
                )
                drawLine(
                    color = Color(0x0CFFFFFF),
                    start = center,
                    end = end,
                    strokeWidth = 32f
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 76.dp) // Room for bottom dock
        ) {
            // 1. TOP BAR: User Profile + Coins + Gems + Sound & Settings
            LudoKingTopBar(
                player = player,
                statistics = statistics,
                soundEnabled = soundEnabled,
                onSoundToggle = onSoundToggle,
                onPlayerProfileClick = { showProfileDialog = true },
                onSettingsClick = onSettingsClick,
                onOpenAdminClick = onOpenAdminClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. HERO LOGO: RS Ludo Royal 3D Crowned Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(crownScale)
                    .padding(vertical = 4.dp)
            ) {
                LudoKingHeroEmblem()
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. DAILY BONUS / STREAK REWARD STRIP
            DailyRewardBar(
                streak = statistics.currentStreak,
                glowAlpha = bonusGlow,
                onClaim = { showClaimBonusDialog = true }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 4. THE ICONIC 2x2 MAIN GAME MODE TILES (Ludo King Signature Grid)
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Row 1: VS COMPUTER & PLAY ONLINE
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LudoKingModeTile(
                        title = "VS COMPUTER",
                        subtitle = "Play Offline",
                        icon = Icons.Filled.SmartToy,
                        badgeText = "PRACTICE",
                        topColor = Color(0xFFFF9100),
                        bottomColor = Color(0xFFC95E00),
                        accentColor = Color(0xFFFFD180),
                        onClick = { onPlayClick(GameMode.VS_AI) },
                        modifier = Modifier.weight(1f)
                    )

                    LudoKingModeTile(
                        title = "PLAY ONLINE",
                        subtitle = "Multiplayer",
                        icon = Icons.Filled.Public,
                        badgeText = "LIVE 🟢",
                        badgeColor = Color(0xFF00C853),
                        topColor = Color(0xFF00B0FF),
                        bottomColor = Color(0xFF0069C0),
                        accentColor = Color(0xFF80D8FF),
                        onClick = onPlayOnlineClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: PLAY WITH FRIENDS (LOCAL) & PASS N PLAY (CUSTOM PHOTOS)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LudoKingModeTile(
                        title = "PASS & PLAY",
                        subtitle = "2 - 4 Players",
                        icon = Icons.Filled.Groups,
                        badgeText = "POPULAR",
                        badgeColor = RsGold,
                        topColor = Color(0xFF00E676),
                        bottomColor = Color(0xFF009624),
                        accentColor = Color(0xFFB9F6CA),
                        onClick = { showPlayerSelectDialog = true },
                        modifier = Modifier.weight(1f)
                    )

                    LudoKingModeTile(
                        title = "CUSTOM PHOTO",
                        subtitle = "Guti Avatars",
                        icon = Icons.Filled.AddAPhoto,
                        badgeText = "NEW VIP",
                        badgeColor = Color(0xFFFF4081),
                        topColor = Color(0xFFAB47BC),
                        bottomColor = Color(0xFF6A1B9A),
                        accentColor = Color(0xFFEA80FC),
                        onClick = onPlayerSetupClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats Ticker Card
            QuickCareerTicker(
                statistics = statistics,
                onStatsClick = onStatsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 5. BOTTOM NAVIGATION DOCK (Ludo King Style)
        LudoKingBottomDock(
            onStatsClick = onStatsClick,
            onAvatarsClick = onPlayerSetupClick,
            onPlayClick = { showPlayerSelectDialog = true },
            onSettingsClick = onSettingsClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 6. POPUPS & DIALOGS
        // Player Count Selection Dialog (2P, 3P, 4P)
        if (showPlayerSelectDialog) {
            LudoKingPlayerSelectDialog(
                onSelectMode = { mode ->
                    showPlayerSelectDialog = false
                    onPlayClick(mode)
                },
                onDismiss = { showPlayerSelectDialog = false }
            )
        }

        // Online Coming Soon Dialog
        if (showOnlineSoonDialog) {
            AlertDialog(
                onDismissRequest = { showOnlineSoonDialog = false },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0069C0))))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Public,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "ONLINE MULTIPLAYER",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = RsGold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Online Live Matchmaking server architecture is ready for future sync! For now, enjoy the complete Pass & Play and VS Computer modes with custom photo avatars.",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showOnlineSoonDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RsGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("GOT IT!", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                },
                containerColor = Color(0xFF141C47)
            )
        }

        // Daily Bonus Claimed Dialog
        if (showClaimBonusDialog) {
            AlertDialog(
                onDismissRequest = { showClaimBonusDialog = false },
                icon = {
                    Text(text = "🪙🎁", fontSize = 42.sp)
                },
                title = {
                    Text(
                        text = "LUCKY BONUS CLAIMED!",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = RsGold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "You received 1,000 Bonus Coins! Play more matches to increase your Win Streak and unlock exclusive custom photo token frames.",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showClaimBonusDialog = false
                            onBonusClaimed()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RsGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("COLLECT!", fontWeight = FontWeight.Black, color = Color.Black)
                    }
                },
                containerColor = Color(0xFF141C47)
            )
        }

        // 7. Full Player Profile & Stats Dialog
        if (showProfileDialog) {
            PlayerProfileDialog(
                player = player ?: Player("p1", "Player 1", PlayerColor.RED),
                statistics = statistics,
                onAvatarChange = onAvatarChange,
                onNameChange = onNameChange,
                onDismiss = { showProfileDialog = false }
            )
        }
    }
}

// -------------------------------------------------------------
// TOP BAR (Profile Avatar + Coins + Gems + Audio / Settings)
// -------------------------------------------------------------
@Composable
private fun LudoKingTopBar(
    player: Player?,
    statistics: GameStatistics,
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onPlayerProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onOpenAdminClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Player Profile Badge with Golden Frame (Iconic Ludo King Left Anchor)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1C2D6A), Color(0xFF0F1B47))
                    )
                )
                .border(1.5.dp, RsGold, RoundedCornerShape(24.dp))
                .clickable(onClick = onPlayerProfileClick)
                .padding(end = 12.dp, top = 2.dp, bottom = 2.dp, start = 2.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                PlayerAvatar(
                    color = player?.color ?: PlayerColor.RED,
                    avatarUri = player?.avatarUri,
                    isAi = player?.isAi == true,
                    size = 40.dp,
                    borderWidth = 2.dp
                )
                // Small Crown icon on top
                Text(
                    text = "👑",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-5).dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = (player?.name ?: "Player 1").take(10),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(RsGold)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Lv. ${(statistics.gamesPlayed / 2 + 1).coerceAtMost(99)}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "VIP",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = RsGoldLight
                    )
                }
            }
        }

        // Center Currency Chips (Coins & Diamonds)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Coins Pill
            val coins = (statistics.wins * 500 + statistics.gamesPlayed * 100 + 2500)
            CurrencyPill(
                iconEmoji = "🪙",
                amountText = if (coins > 9999) "${coins / 1000}k" else "$coins",
                pillColor = Color(0xFF142459),
                borderColor = Color(0xFFFFD54F)
            )

            // Gems / Streak Pill
            CurrencyPill(
                iconEmoji = "💎",
                amountText = "${statistics.currentStreak * 10 + 50}",
                pillColor = Color(0xFF142459),
                borderColor = Color(0xFF00E5FF)
            )
        }

        // Action Icons (Sound toggle & Settings)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
                onClick = onSoundToggle,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B2C69))
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    contentDescription = "Sound",
                    tint = if (soundEnabled) RsGold else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B2C69))
                    .border(1.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onOpenAdminClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B2C69))
                    .border(1.2.dp, RsGold, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.AdminPanelSettings,
                    contentDescription = "Admin Console",
                    tint = RsGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CurrencyPill(
    iconEmoji: String,
    amountText: String,
    pillColor: Color,
    borderColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(pillColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = iconEmoji, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = amountText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

// -------------------------------------------------------------
// HERO 3D EMBLEM (Golden Crown & RS LUDO 3D Title)
// -------------------------------------------------------------
@Composable
private fun LudoKingHeroEmblem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Golden Crown
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "👑", fontSize = 34.sp)
        }

        Spacer(modifier = Modifier.height((-6).dp))

        // 3D Arched Banner for RS LUDO
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = RsGold,
                    ambientColor = RsGold
                )
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF381559),
                            Color(0xFF1E0935),
                            Color(0xFF0C031A)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(RsGoldLight, RsGold, RsGoldDark, RsGoldLight)
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // RS Monogram Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.radialGradient(listOf(RsGoldLight, RsGoldDark)))
                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = "RS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF180A2E)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Bold 3D "RS LUDO" text
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "RS LUDO",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 2.sp,
                        color = RsGold
                    )
                    Text(
                        text = "★ THE ROYAL BOARD ★",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = Color(0xFFFFE082)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DAILY BONUS / STREAK REWARD STRIP
// -------------------------------------------------------------
@Composable
private fun DailyRewardBar(
    streak: Int,
    glowAlpha: Float,
    onClaim: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(6.dp * glowAlpha, RoundedCornerShape(16.dp), spotColor = RsGold)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF26184C),
                        Color(0xFF172054),
                        Color(0xFF26184C)
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(RsGoldDark, RsGoldLight, RsGoldDark)),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClaim)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎁", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "LUCKY REWARDS & STREAK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = RsGold
                    )
                    Text(
                        text = if (streak > 0) "$streak Match Win Streak! 🔥" else "Claim 1,000 Free Coins!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(RsGoldLight, RsGoldDark)))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "CLAIM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

// -------------------------------------------------------------
// THE ICONIC 2x2 MAIN GAME MODE TILES (Ludo King Big 3D Buttons)
// -------------------------------------------------------------
@Composable
private fun LudoKingModeTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    topColor: Color,
    bottomColor: Color,
    accentColor: Color,
    badgeColor: Color = Color.Black.copy(alpha = 0.45f),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = bottomColor
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
            .border(2.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        // Specular highlight gloss at the top half
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)
                    )
                )
        )

        // Top Corner Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(8.dp))
                .background(badgeColor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = badgeText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        // Center Content: 3D Round Icon Badge + Bold Titles
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color.White, Color.White.copy(alpha = 0.85f))
                        )
                    )
                    .border(2.dp, accentColor, CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = bottomColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// -------------------------------------------------------------
// QUICK CAREER STATS TICKER
// -------------------------------------------------------------
@Composable
private fun QuickCareerTicker(
    statistics: GameStatistics,
    onStatsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF101B45))
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onStatsClick)
            .padding(vertical = 10.dp, horizontal = 14.dp)
    ) {
        QuickStatItem("MATCHES", "${statistics.gamesPlayed}")
        VerticalDivider(modifier = Modifier.height(24.dp), color = BorderSubtle)
        QuickStatItem("WINS", "${statistics.wins}", isGold = true)
        VerticalDivider(modifier = Modifier.height(24.dp), color = BorderSubtle)
        QuickStatItem("WIN %", "${statistics.winRate}%")
        VerticalDivider(modifier = Modifier.height(24.dp), color = BorderSubtle)
        QuickStatItem("CAPTURES", "${statistics.tokensCaptured}")
    }
}

@Composable
private fun QuickStatItem(label: String, value: String, isGold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = if (isGold) RsGold else Color.White
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
    }
}

// -------------------------------------------------------------
// BOTTOM DOCK NAVIGATION (Ludo King Style)
// -------------------------------------------------------------
@Composable
private fun LudoKingBottomDock(
    onStatsClick: () -> Unit,
    onAvatarsClick: () -> Unit,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(16.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B265E),
                        Color(0xFF0C1338)
                    )
                )
            )
            .border(1.5.dp, BorderSubtle, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize()
        ) {
            DockItem(
                icon = Icons.Filled.EmojiEvents,
                label = "STATS",
                tint = RsGold,
                onClick = onStatsClick
            )

            DockItem(
                icon = Icons.Filled.Face,
                label = "AVATARS",
                tint = Color(0xFF00E5FF),
                onClick = onAvatarsClick
            )

            // Elevated Center Big Play Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = (-10).dp)
                    .size(54.dp)
                    .shadow(10.dp, CircleShape, spotColor = RsGold)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFEA00), Color(0xFFFF6D00))
                        )
                    )
                    .border(2.5.dp, Color.White, CircleShape)
                    .clickable(onClick = onPlayClick)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(34.dp)
                )
            }

            DockItem(
                icon = Icons.Filled.Casino,
                label = "DICE",
                tint = Color(0xFFFF4081),
                onClick = onAvatarsClick
            )

            DockItem(
                icon = Icons.Filled.Settings,
                label = "SETTINGS",
                tint = Color.White,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun DockItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

// -------------------------------------------------------------
// PLAYER COUNT SELECT DIALOG (2P, 3P, 4P Modal)
// -------------------------------------------------------------
@Composable
private fun LudoKingPlayerSelectDialog(
    onSelectMode: (GameMode) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1A2A6C),
                            Color(0xFF0F1742)
                        )
                    )
                )
                .border(2.5.dp, RsGold, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header with Trophy & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🎲", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SELECT PLAYERS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = RsGold
                    )
                }

                Text(
                    text = "Choose number of players for Pass & Play match",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                // 2 Players Option
                PlayerOptionButton(
                    countText = "2 PLAYERS",
                    descText = "Classic 1v1 Faceoff",
                    badgeColor = LudoRed,
                    onClick = { onSelectMode(GameMode.LOCAL_2P) }
                )

                // 3 Players Option
                PlayerOptionButton(
                    countText = "3 PLAYERS",
                    descText = "3-Way Board Battle",
                    badgeColor = LudoGreen,
                    onClick = { onSelectMode(GameMode.LOCAL_3P) }
                )

                // 4 Players Option
                PlayerOptionButton(
                    countText = "4 PLAYERS",
                    descText = "Full Grand Arena Match",
                    badgeColor = LudoBlue,
                    onClick = { onSelectMode(GameMode.LOCAL_4P) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CANCEL", color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun PlayerOptionButton(
    countText: String,
    descText: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = badgeColor)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF22347A),
                        Color(0xFF142054)
                    )
                )
            )
            .border(1.5.dp, badgeColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                        .border(1.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = countText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = descText,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = null,
                tint = RsGold,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
