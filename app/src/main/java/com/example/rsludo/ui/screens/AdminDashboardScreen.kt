package com.example.rsludo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rsludo.admin.AdminAuditLog
import com.example.rsludo.admin.AdminDashboardState
import com.example.rsludo.admin.AdminPlayerAccount
import com.example.rsludo.admin.DiceRigMode
import com.example.rsludo.ui.components.PlayerAvatar
import com.example.ui.theme.*

private enum class AdminTab(val title: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Filled.Dashboard),
    PLAYERS("Players", Icons.Filled.People),
    ECONOMY("Economy & Rules", Icons.Filled.MonetizationOn),
    SERVER("Server & Logs", Icons.Filled.Dns)
}

@Composable
fun AdminDashboardScreen(
    adminState: AdminDashboardState,
    onAuthenticate: (String) -> Boolean,
    onLogout: () -> Unit,
    onToggleMaintenance: (Boolean) -> Unit,
    onSetDiceRigMode: (DiceRigMode) -> Unit,
    onSetDailyBonusCoins: (Int) -> Unit,
    onSetMatchWinRewardCoins: (Int) -> Unit,
    onUpdatePlayerCoins: (String, Int) -> Unit,
    onSetPlayerVip: (String, Int) -> Unit,
    onTogglePlayerBan: (String) -> Unit,
    onClearLogs: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // If not authenticated, show modern PIN lock screen
    if (!adminState.isAuthenticated) {
        AdminLockScreen(
            onUnlock = onAuthenticate,
            onBack = onBack,
            modifier = modifier
        )
    } else {
        AdminConsoleScreen(
            adminState = adminState,
            onLogout = onLogout,
            onToggleMaintenance = onToggleMaintenance,
            onSetDiceRigMode = onSetDiceRigMode,
            onSetDailyBonusCoins = onSetDailyBonusCoins,
            onSetMatchWinRewardCoins = onSetMatchWinRewardCoins,
            onUpdatePlayerCoins = onUpdatePlayerCoins,
            onSetPlayerVip = onSetPlayerVip,
            onTogglePlayerBan = onTogglePlayerBan,
            onClearLogs = onClearLogs,
            onBack = onBack,
            modifier = modifier
        )
    }
}

// -----------------------------------------------------------------
// 1. PIN LOCK SCREEN
// -----------------------------------------------------------------
@Composable
private fun AdminLockScreen(
    onUnlock: (String) -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF070B14),
                        Color(0xFF020408)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "RESTRICTED ACCESS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Admin Shield Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .border(2.dp, RsGold, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = RsGold,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RS LUDO OPERATOR CONSOLE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Enter master 4-digit security PIN to unlock",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // PIN Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) RsGold else Color(0xFF334155)
                            )
                            .border(1.dp, if (isFilled) Color.White else Color(0xFF475569), CircleShape)
                    )
                }
            }

            if (showError) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Incorrect PIN. Try again.",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Default Developer PIN: 8888",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Numeric Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "OK")
                )

                keypadRows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { key ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        when (key) {
                                            "OK" -> Brush.horizontalGradient(listOf(RsGold, RsGoldDark))
                                            "C" -> Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                                            else -> Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                                        }
                                    )
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                                    .clickable {
                                        when (key) {
                                            "C" -> {
                                                enteredPin = ""
                                                showError = false
                                            }
                                            "OK" -> {
                                                if (enteredPin.length == 4) {
                                                    val success = onUnlock(enteredPin)
                                                    if (!success) {
                                                        showError = true
                                                        enteredPin = ""
                                                    }
                                                }
                                            }
                                            else -> {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += key
                                                    showError = false
                                                    if (enteredPin.length == 4) {
                                                        val success = onUnlock(enteredPin)
                                                        if (!success) {
                                                            showError = true
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (key == "OK") Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 2. MAIN ADMIN CONSOLE SCREEN
// -----------------------------------------------------------------
@Composable
private fun AdminConsoleScreen(
    adminState: AdminDashboardState,
    onLogout: () -> Unit,
    onToggleMaintenance: (Boolean) -> Unit,
    onSetDiceRigMode: (DiceRigMode) -> Unit,
    onSetDailyBonusCoins: (Int) -> Unit,
    onSetMatchWinRewardCoins: (Int) -> Unit,
    onUpdatePlayerCoins: (String, Int) -> Unit,
    onSetPlayerVip: (String, Int) -> Unit,
    onTogglePlayerBan: (String) -> Unit,
    onClearLogs: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AdminTab.OVERVIEW) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B16))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RS LUDO ADMIN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = RsGold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                    }
                    Text(
                        text = "Operator Control Hub",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("LOCK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color(0xFF0F172A),
            contentColor = RsGold,
            edgePadding = 8.dp,
            divider = {}
        ) {
            AdminTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTab == tab) RsGold else Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Black else FontWeight.Medium,
                                color = if (selectedTab == tab) RsGold else Color(0xFF94A3B8)
                            )
                        }
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                AdminTab.OVERVIEW -> AdminOverviewTab(
                    adminState = adminState,
                    onToggleMaintenance = onToggleMaintenance
                )
                AdminTab.PLAYERS -> AdminPlayersTab(
                    players = adminState.players,
                    onUpdateCoins = onUpdatePlayerCoins,
                    onSetVip = onSetPlayerVip,
                    onToggleBan = onTogglePlayerBan
                )
                AdminTab.ECONOMY -> AdminEconomyTab(
                    adminState = adminState,
                    onSetDiceRigMode = onSetDiceRigMode,
                    onSetDailyBonus = onSetDailyBonusCoins,
                    onSetWinReward = onSetMatchWinRewardCoins
                )
                AdminTab.SERVER -> AdminServerTab(
                    adminState = adminState,
                    onToggleMaintenance = onToggleMaintenance,
                    onClearLogs = onClearLogs
                )
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 1: OVERVIEW & TELEMETRY
// -----------------------------------------------------------------
@Composable
private fun AdminOverviewTab(
    adminState: AdminDashboardState,
    onToggleMaintenance: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Maintenance Alert if active
        if (adminState.maintenanceMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF7F1D1D))
                    .border(1.5.dp, Color(0xFFEF4444), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SERVER MAINTENANCE MODE IS LIVE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = adminState.maintenanceNotice,
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            }
        }

        // 4 KPI Cards (2x2 Grid)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KpiCard(
                title = "REGISTERED USERS",
                value = "${adminState.totalRegisteredPlayers}",
                subtitle = "+42 this week",
                icon = Icons.Filled.Group,
                accentColor = Color(0xFF00E5FF),
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "ACTIVE ONLINE",
                value = "${adminState.activeOnlinePlayers}",
                subtitle = "● Live in matches",
                icon = Icons.Filled.Wifi,
                accentColor = Color(0xFF00E676),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KpiCard(
                title = "TOTAL MATCHES",
                value = "${adminState.totalMatchesPlayed}",
                subtitle = "Pass & Play + AI + Online",
                icon = Icons.Filled.Casino,
                accentColor = RsGold,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "SERVER UPTIME",
                value = "99.98%",
                subtitle = "Asia-East Cluster",
                icon = Icons.Filled.Speed,
                accentColor = Color(0xFFA855F7),
                modifier = Modifier.weight(1f)
            )
        }

        // Quick Controls
        Text(
            text = "QUICK CONTROL SWITCHES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "Server Maintenance Mode",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Temporarily queues matchmaking for backend updates",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
            Switch(
                checked = adminState.maintenanceMode,
                onCheckedChange = onToggleMaintenance,
                colors = SwitchDefaults.colors(checkedThumbColor = RsGold)
            )
        }

        // Live Health Telemetry Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SYSTEM TELEMETRY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = RsGold,
                    letterSpacing = 1.sp
                )
                TelemetryRow("Database Engine", "SQLite + Room v2.6.1 (Active)", Color(0xFF00E676))
                TelemetryRow("Matchmaking Server", "Asia-East Live • 24ms Ping", Color(0xFF00E676))
                TelemetryRow("AI Model Engine", "Gemini 3.5 Flash REST + Local Heuristics", Color(0xFF00E5FF))
                TelemetryRow("Dice Rig Engine", adminState.diceRigMode.title, RsGold)
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = accentColor
            )
        }
    }
}

@Composable
private fun TelemetryRow(label: String, status: String, statusColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(text = status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = statusColor)
    }
}

// -----------------------------------------------------------------
// TAB 2: PLAYERS MANAGEMENT
// -----------------------------------------------------------------
@Composable
private fun AdminPlayersTab(
    players: List<AdminPlayerAccount>,
    onUpdateCoins: (String, Int) -> Unit,
    onSetVip: (String, Int) -> Unit,
    onToggleBan: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlayerForCoins by remember { mutableStateOf<AdminPlayerAccount?>(null) }

    val filteredPlayers = remember(players, searchQuery) {
        if (searchQuery.isBlank()) players
        else players.filter { it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search player name or ID...", color = Color(0xFF64748B)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RsGold,
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredPlayers, key = { it.id }) { player ->
                AdminPlayerRow(
                    player = player,
                    onGrantCoins = { onUpdateCoins(player.id, 1000) },
                    onDeductCoins = { onUpdateCoins(player.id, -1000) },
                    onToggleBan = { onToggleBan(player.id) },
                    onUpgradeVip = { onSetVip(player.id, (player.vipLevel + 1).coerceAtMost(10)) }
                )
            }
        }
    }
}

@Composable
private fun AdminPlayerRow(
    player: AdminPlayerAccount,
    onGrantCoins: () -> Unit,
    onDeductCoins: () -> Unit,
    onToggleBan: () -> Unit,
    onUpgradeVip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(
                width = 1.2.dp,
                color = if (player.isBanned) Color(0xFFEF4444) else Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayerAvatar(
                        color = player.color,
                        avatarUri = player.avatarUri,
                        size = 36.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = player.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            if (player.isBanned) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEF4444))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("BANNED", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }
                        }
                        Text(
                            text = "ID: ${player.id} • Registered ${player.registrationDate}",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // VIP Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RsGold)
                        .clickable(onClick = onUpgradeVip)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "VIP ${player.vipLevel} ▲",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            // Stats row (Coins, Gems, Win Rate)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF070B16))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = "🪙 ${player.coins}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RsGoldLight)
                Text(text = "💎 ${player.gems}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                Text(text = "Win: ${player.winRate}%", fontSize = 12.sp, color = Color.White)
            }

            // Operator Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onGrantCoins,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF065F46)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+1,000 🪙", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDeductCoins,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("-1,000 🪙", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onToggleBan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (player.isBanned) Color(0xFF1E3A8A) else Color(0xFF7F1D1D)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (player.isBanned) "UNBAN" else "BAN", fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 3: ECONOMY & GAME ENGINE RULES
// -----------------------------------------------------------------
@Composable
private fun AdminEconomyTab(
    adminState: AdminDashboardState,
    onSetDiceRigMode: (DiceRigMode) -> Unit,
    onSetDailyBonus: (Int) -> Unit,
    onSetWinReward: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Dice Engine & RNG Mode
        Text(
            text = "DICE ENGINE & PROBABILITY RIGGING",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = RsGold,
            letterSpacing = 1.sp
        )

        DiceRigMode.values().forEach { mode ->
            val isSelected = adminState.diceRigMode == mode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) RsGold else Color(0xFF1E293B),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSetDiceRigMode(mode) }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mode.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) RsGold else Color.White
                        )
                        Text(
                            text = mode.description,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSetDiceRigMode(mode) },
                        colors = RadioButtonDefaults.colors(selectedColor = RsGold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Economy: Daily Bonus
        Text(
            text = "DAILY LUCKY CHEST REWARD",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = RsGold,
            letterSpacing = 1.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(500, 1000, 2500, 5000).forEach { amount ->
                val isSelected = adminState.dailyBonusCoins == amount
                Button(
                    onClick = { onSetDailyBonus(amount) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) RsGold else Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$amount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Economy: Match Win Reward
        Text(
            text = "MATCH WIN PRIZE COINS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = RsGold,
            letterSpacing = 1.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(250, 500, 1000, 2000).forEach { prize ->
                val isSelected = adminState.matchWinRewardCoins == prize
                Button(
                    onClick = { onSetWinReward(prize) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) RsGold else Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$prize",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 4: SERVER & AUDIT LOGS
// -----------------------------------------------------------------
@Composable
private fun AdminServerTab(
    adminState: AdminDashboardState,
    onToggleMaintenance: (Boolean) -> Unit,
    onClearLogs: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Server Action Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "LIVE AUDIT TERMINAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = RsGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Real-time security and gameplay event stream",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }

            Button(
                onClick = onClearLogs,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("CLEAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Terminal Log Console
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF030712))
                .border(1.5.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (adminState.auditLogs.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("No audit events recorded.", color = Color(0xFF475569), fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(adminState.auditLogs, key = { it.id }) { log ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "[${log.timestamp}]",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (log.category) {
                                            "SECURITY" -> Color(0xFF7F1D1D)
                                            "SERVER" -> Color(0xFF065F46)
                                            "ECONOMY" -> Color(0xFF1E3A8A)
                                            else -> Color(0xFF374151)
                                        }
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = log.category,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = log.message,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (log.isWarning) Color(0xFFF87171) else Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }
    }
}
