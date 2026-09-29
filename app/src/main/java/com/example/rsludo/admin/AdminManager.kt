package com.example.rsludo.admin

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.rsludo.model.PlayerColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

enum class DiceRigMode(val title: String, val description: String) {
    FAIR("Fair RNG (Standard)", "Standard 1/6 uniform probability across all dice sides"),
    LUCKY_SIX_BOOST("Lucky 6 Booster (QA)", "35% chance to roll a 6 for quick testing"),
    FORCE_SIX("Force 6 (Dev Only)", "Guarantees rolling a 6 on every turn"),
    HIGH_ROLLS("High Roller (4-6)", "Dice only rolls 4, 5, or 6")
}

data class AdminPlayerAccount(
    val id: String,
    val name: String,
    val color: PlayerColor,
    val avatarUri: String? = null,
    val coins: Int = 5000,
    val gems: Int = 120,
    val vipLevel: Int = 5,
    val isBanned: Boolean = false,
    val matchesPlayed: Int = 18,
    val winRate: Int = 67,
    val registrationDate: String = "2026-09-15"
)

data class AdminAuditLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
    val category: String, // "SERVER", "ECONOMY", "PLAYER", "SECURITY"
    val message: String,
    val isWarning: Boolean = false
)

data class AdminDashboardState(
    val isAuthenticated: Boolean = false,
    val adminPin: String = "8888",
    val maintenanceMode: Boolean = false,
    val maintenanceNotice: String = "Scheduled server maintenance in progress. Matchmaking resumes shortly.",
    val diceRigMode: DiceRigMode = DiceRigMode.FAIR,
    val dailyBonusCoins: Int = 1000,
    val matchWinRewardCoins: Int = 500,
    val totalRegisteredPlayers: Int = 1420,
    val activeOnlinePlayers: Int = 89,
    val totalMatchesPlayed: Int = 3840,
    val serverPingMs: Int = 24,
    val serverUptime: String = "99.98% (48d 14h)",
    val players: List<AdminPlayerAccount> = emptyList(),
    val auditLogs: List<AdminAuditLog> = emptyList()
)

class AdminManager(context: Context) {

    private val prefs = context.getSharedPreferences("rs_ludo_admin_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        AdminDashboardState(
            maintenanceMode = prefs.getBoolean("maintenance_mode", false),
            dailyBonusCoins = prefs.getInt("daily_bonus_coins", 1000),
            matchWinRewardCoins = prefs.getInt("win_reward_coins", 500),
            players = defaultAccounts(),
            auditLogs = initialLogs()
        )
    )
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    private fun defaultAccounts(): List<AdminPlayerAccount> {
        return listOf(
            AdminPlayerAccount(
                id = "p1",
                name = "Player 1",
                color = PlayerColor.RED,
                coins = 12500,
                gems = 250,
                vipLevel = 8,
                matchesPlayed = 24,
                winRate = 75
            ),
            AdminPlayerAccount(
                id = "p2",
                name = "AI Master",
                color = PlayerColor.GREEN,
                coins = 99999,
                gems = 999,
                vipLevel = 10,
                matchesPlayed = 150,
                winRate = 50
            ),
            AdminPlayerAccount(
                id = "guest_4921",
                name = "Ayan_Pro",
                color = PlayerColor.YELLOW,
                coins = 8200,
                gems = 180,
                vipLevel = 4,
                matchesPlayed = 12,
                winRate = 58
            ),
            AdminPlayerAccount(
                id = "guest_8302",
                name = "Robiul_King",
                color = PlayerColor.BLUE,
                coins = 4100,
                gems = 90,
                vipLevel = 3,
                matchesPlayed = 8,
                winRate = 50
            )
        )
    }

    private fun initialLogs(): List<AdminAuditLog> {
        return listOf(
            AdminAuditLog(category = "SERVER", message = "Online Matchmaking server live in Asia-East (Ping: 24ms)"),
            AdminAuditLog(category = "SECURITY", message = "Admin session initialized with PIN verification"),
            AdminAuditLog(category = "ECONOMY", message = "Daily lucky chest pool balanced at 1,000 Coins"),
            AdminAuditLog(category = "ENGINE", message = "Ludo Rules Engine: Exact 56 Home check active")
        )
    }

    fun authenticate(enteredPin: String): Boolean {
        val isValid = enteredPin == _state.value.adminPin
        if (isValid) {
            _state.value = _state.value.copy(isAuthenticated = true)
            addLog("SECURITY", "Admin console unlocked successfully")
        } else {
            addLog("SECURITY", "Failed login attempt with PIN: $enteredPin", isWarning = true)
        }
        return isValid
    }

    fun logout() {
        _state.value = _state.value.copy(isAuthenticated = false)
        addLog("SECURITY", "Admin logged out")
    }

    fun toggleMaintenance(enabled: Boolean, notice: String = _state.value.maintenanceNotice) {
        prefs.edit().putBoolean("maintenance_mode", enabled).apply()
        _state.value = _state.value.copy(
            maintenanceMode = enabled,
            maintenanceNotice = notice
        )
        addLog("SERVER", if (enabled) "Maintenance mode ENABLED: $notice" else "Maintenance mode DISABLED", isWarning = enabled)
    }

    fun setDiceRigMode(mode: DiceRigMode) {
        _state.value = _state.value.copy(diceRigMode = mode)
        addLog("ENGINE", "Dice Engine mode switched to: ${mode.title}")
    }

    fun setDailyBonusCoins(amount: Int) {
        prefs.edit().putInt("daily_bonus_coins", amount).apply()
        _state.value = _state.value.copy(dailyBonusCoins = amount)
        addLog("ECONOMY", "Daily Bonus amount updated to $amount Coins")
    }

    fun setMatchWinRewardCoins(amount: Int) {
        prefs.edit().putInt("win_reward_coins", amount).apply()
        _state.value = _state.value.copy(matchWinRewardCoins = amount)
        addLog("ECONOMY", "Match Win prize updated to $amount Coins")
    }

    fun updatePlayerCoins(playerId: String, amountDelta: Int) {
        val updated = _state.value.players.map { player ->
            if (player.id == playerId) {
                val newCoins = (player.coins + amountDelta).coerceAtLeast(0)
                player.copy(coins = newCoins)
            } else player
        }
        _state.value = _state.value.copy(players = updated)
        addLog("ECONOMY", "Adjusted coins for $playerId by ${if (amountDelta >= 0) "+$amountDelta" else "$amountDelta"}")
    }

    fun setPlayerVip(playerId: String, newVipLevel: Int) {
        val updated = _state.value.players.map { player ->
            if (player.id == playerId) {
                player.copy(vipLevel = newVipLevel)
            } else player
        }
        _state.value = _state.value.copy(players = updated)
        addLog("PLAYER", "Updated VIP level of $playerId to VIP $newVipLevel")
    }

    fun togglePlayerBan(playerId: String) {
        val updated = _state.value.players.map { player ->
            if (player.id == playerId) {
                val newBan = !player.isBanned
                addLog("SECURITY", "${player.name} ($playerId) was ${if (newBan) "BANNED" else "UNBANNED"}", isWarning = newBan)
                player.copy(isBanned = newBan)
            } else player
        }
        _state.value = _state.value.copy(players = updated)
    }

    fun addLog(category: String, message: String, isWarning: Boolean = false) {
        val newEntry = AdminAuditLog(
            category = category,
            message = message,
            isWarning = isWarning
        )
        _state.value = _state.value.copy(
            auditLogs = listOf(newEntry) + _state.value.auditLogs.take(40)
        )
    }

    fun clearAuditLogs() {
        _state.value = _state.value.copy(auditLogs = emptyList())
    }
}
