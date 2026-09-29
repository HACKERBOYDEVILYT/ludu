package com.example.rsludo.online

import com.example.rsludo.model.Player
import com.example.rsludo.model.PlayerColor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

enum class ConnectionState {
    IDLE,
    SEARCHING,
    LOBBY,
    IN_MATCH,
    DISCONNECTED
}

data class OnlinePlayer(
    val id: String,
    val name: String,
    val color: PlayerColor,
    val avatarUri: String? = null,
    val isReady: Boolean = true,
    val pingMs: Int = 28
)

data class OnlineChatMessage(
    val senderName: String,
    val text: String,
    val color: PlayerColor,
    val timestamp: Long = System.currentTimeMillis()
)

data class OnlineServerState(
    val connectionState: ConnectionState = ConnectionState.IDLE,
    val roomCode: String? = null,
    val isHost: Boolean = false,
    val pingMs: Int = 24,
    val serverRegion: String = "Asia-East (Live)",
    val players: List<OnlinePlayer> = emptyList(),
    val turnTimerSeconds: Int = 15,
    val chatMessages: List<OnlineChatMessage> = emptyList()
)

class OnlineServerManager {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null
    private var matchmakingJob: Job? = null

    private val _state = MutableStateFlow(OnlineServerState())
    val state: StateFlow<OnlineServerState> = _state.asStateFlow()

    fun quickMatch(localPlayer: Player, onMatchFound: (List<Player>) -> Unit) {
        matchmakingJob?.cancel()
        _state.value = OnlineServerState(
            connectionState = ConnectionState.SEARCHING,
            pingMs = Random.nextInt(20, 45)
        )

        matchmakingJob = scope.launch {
            // Simulated live matchmaking search
            delay(1800)

            val generatedRoomCode = "RS-" + Random.nextInt(1000, 9999)
            val opponent1 = OnlinePlayer(
                id = "online_p2",
                name = listOf("Ayan_Pro", "Robiul_King", "Samir_99", "LudoMaster").random(),
                color = PlayerColor.GREEN,
                pingMs = Random.nextInt(25, 50)
            )

            val onlineList = listOf(
                OnlinePlayer(
                    id = localPlayer.id,
                    name = localPlayer.name,
                    color = PlayerColor.RED,
                    avatarUri = localPlayer.avatarUri,
                    pingMs = _state.value.pingMs
                ),
                opponent1
            )

            _state.value = _state.value.copy(
                connectionState = ConnectionState.IN_MATCH,
                roomCode = generatedRoomCode,
                players = onlineList
            )

            // Convert to Player models
            val gamePlayers = onlineList.map { op ->
                Player(
                    id = op.id,
                    name = op.name,
                    color = op.color,
                    avatarUri = op.avatarUri,
                    isAi = false
                )
            }

            withContext(Dispatchers.Main) {
                onMatchFound(gamePlayers)
            }

            startTurnTimer()
        }
    }

    fun createPrivateRoom(localPlayer: Player) {
        val code = "RS-" + Random.nextInt(1000, 9999)
        _state.value = OnlineServerState(
            connectionState = ConnectionState.LOBBY,
            roomCode = code,
            isHost = true,
            pingMs = Random.nextInt(22, 38),
            players = listOf(
                OnlinePlayer(
                    id = localPlayer.id,
                    name = localPlayer.name,
                    color = PlayerColor.RED,
                    avatarUri = localPlayer.avatarUri
                )
            )
        )
    }

    fun joinPrivateRoom(code: String, localPlayer: Player, onJoined: (Boolean) -> Unit) {
        scope.launch {
            _state.value = _state.value.copy(connectionState = ConnectionState.SEARCHING)
            delay(1200)

            val host = OnlinePlayer(
                id = "host_p1",
                name = "RoomHost_Pro",
                color = PlayerColor.RED,
                pingMs = Random.nextInt(24, 40)
            )

            val joining = OnlinePlayer(
                id = localPlayer.id,
                name = localPlayer.name,
                color = PlayerColor.GREEN,
                avatarUri = localPlayer.avatarUri,
                pingMs = Random.nextInt(26, 45)
            )

            _state.value = OnlineServerState(
                connectionState = ConnectionState.LOBBY,
                roomCode = code.uppercase(),
                isHost = false,
                players = listOf(host, joining)
            )

            withContext(Dispatchers.Main) {
                onJoined(true)
            }
        }
    }

    fun sendChatMessage(text: String, sender: Player) {
        val msg = OnlineChatMessage(
            senderName = sender.name,
            text = text,
            color = sender.color
        )
        _state.value = _state.value.copy(
            chatMessages = _state.value.chatMessages + msg
        )
    }

    fun startTurnTimer(onTimeout: (() -> Unit)? = null) {
        timerJob?.cancel()
        _state.value = _state.value.copy(turnTimerSeconds = 15)
        timerJob = scope.launch {
            for (sec in 15 downTo 0) {
                _state.value = _state.value.copy(turnTimerSeconds = sec)
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                onTimeout?.invoke()
            }
        }
    }

    fun resetTurnTimer() {
        startTurnTimer()
    }

    fun leaveRoom() {
        timerJob?.cancel()
        matchmakingJob?.cancel()
        _state.value = OnlineServerState(connectionState = ConnectionState.IDLE)
    }
}
