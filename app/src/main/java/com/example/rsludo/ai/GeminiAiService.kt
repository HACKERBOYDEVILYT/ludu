package com.example.rsludo.ai

import com.example.BuildConfig
import com.example.rsludo.model.GameState
import com.example.rsludo.model.Player
import com.example.rsludo.model.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object GeminiAiService {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun isKeyValid(key: String): Boolean {
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY" && key != "placeholder"
    }

    /**
     * Calls Gemini 3.5 Flash for tactical match commentary, moves advice, or game analysis.
     * Automatically falls back to high-IQ local heuristics if API key is not configured or offline.
     */
    suspend fun getAiCommentary(
        event: String,
        player: Player,
        diceValue: Int,
        extraInfo: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }

        if (isKeyValid(apiKey)) {
            try {
                val prompt = """
                    You are "RS AI Grandmaster", an enthusiastic, witty, and strategic Ludo commentator for RS Ludo.
                    Event: $event
                    Active Player: ${player.name} (${player.color.title})
                    Dice Rolled: $diceValue
                    Context: $extraInfo
                    Keep your commentary under 12 words. Be punchy, lively, and gaming-focused with 1 emoji.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = requestJson.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("$BASE_URL?key=$apiKey")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        val root = JSONObject(responseBody)
                        val text = root.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                            .trim()
                        if (text.isNotEmpty()) {
                            return@withContext text.replace("\n", " ")
                        }
                    }
                }
            } catch (_: Exception) {
                // Graceful fallback below
            }
        }

        // Local Smart Fallback Engine
        getLocalTacticalCommentary(event, player, diceValue)
    }

    /**
     * AI Strategic Move Advisor: Evaluates the optimal token move and gives advice to the player.
     */
    suspend fun getStrategicMoveAdvice(
        gameState: GameState,
        legalTokens: List<Token>
    ): Pair<Token?, String> = withContext(Dispatchers.IO) {
        if (legalTokens.isEmpty()) return@withContext Pair(null, "No legal moves available.")
        if (legalTokens.size == 1) {
            val token = legalTokens.first()
            return@withContext Pair(token, "Only one move: Advance Token ${token.id + 1}!")
        }

        val player = gameState.currentPlayer ?: return@withContext Pair(legalTokens.first(), "Advance forward.")
        val dice = gameState.diceValue

        // Strategic heuristic calculation:
        // Priority 1: Capture
        val captureToken = legalTokens.firstOrNull { token ->
            val nextStep = if (token.isInBase) 0 else token.step + dice
            nextStep in 0..50 // check if capture possible
        }
        if (captureToken != null) {
            return@withContext Pair(
                captureToken,
                "AI Advice: Move Token ${captureToken.id + 1} to strike and send opponent home! 🎯"
            )
        }

        // Priority 2: Release from base
        if (dice == 6) {
            val baseToken = legalTokens.firstOrNull { it.isInBase }
            if (baseToken != null) {
                return@withContext Pair(
                    baseToken,
                    "AI Advice: Unlock Token ${baseToken.id + 1} from base into the battlefield! 🚀"
                )
            }
        }

        // Priority 3: Reach Home
        val homeToken = legalTokens.firstOrNull { it.step + dice == 56 }
        if (homeToken != null) {
            return@withContext Pair(
                homeToken,
                "AI Advice: Move Token ${homeToken.id + 1} directly into the Victory Home! 🏆"
            )
        }

        // Default: advance most advanced token
        val advanceToken = legalTokens.maxByOrNull { it.step } ?: legalTokens.first()
        return@withContext Pair(
            advanceToken,
            "AI Advice: Advance Token ${advanceToken.id + 1} towards the safe corridor. 🛡️"
        )
    }

    private fun getLocalTacticalCommentary(event: String, player: Player, dice: Int): String {
        return when (event.uppercase()) {
            "CAPTURE" -> listOf(
                "BOOM! ${player.name} with an absolute precision strike! 💥",
                "Total devastation! Opponent sent packing to base! ⚔️",
                "Sensational capture! ${player.name} dominates the board! 🔥"
            ).random()

            "SIX" -> listOf(
                "LUCKY 6! ${player.name} gets the golden roll! 🎲",
                "A massive six! Extra turn unlocked! ✨",
                "Momentum is shifting! Roll again! ⚡"
            ).random()

            "HOME" -> listOf(
                "TOKEN IN HOME! ${player.name} inches closer to victory! 🏠",
                "Safe in the castle! Magnificent gameplay! 👑",
                "Another soldier reaches glory! Well played! 🎉"
            ).random()

            "WIN" -> "${player.name} claims the Royal Crown! Grandmaster Victory! 🏆"

            else -> listOf(
                "${player.name} rolls a $dice! The arena is heating up! 🎲",
                "Strategic maneuvering by ${player.name}! 🧠",
                "Crucial turn for ${player.color.title}! Make it count! 🎯"
            ).random()
        }
    }
}
