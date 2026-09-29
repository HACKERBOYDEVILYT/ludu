package com.example.rsludo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.rsludo.model.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PreferencesManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("rs_ludo_prefs", Context.MODE_PRIVATE)

    fun loadSettings(): GameSettings {
        return GameSettings(
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            hapticEnabled = prefs.getBoolean("haptic_enabled", true),
            fastAnimations = prefs.getBoolean("fast_animations", false),
            confirmExit = prefs.getBoolean("confirm_exit", true)
        )
    }

    fun saveSettings(settings: GameSettings) {
        prefs.edit()
            .putBoolean("sound_enabled", settings.soundEnabled)
            .putBoolean("haptic_enabled", settings.hapticEnabled)
            .putBoolean("fast_animations", settings.fastAnimations)
            .putBoolean("confirm_exit", settings.confirmExit)
            .apply()
    }

    fun loadStatistics(): GameStatistics {
        return GameStatistics(
            gamesPlayed = prefs.getInt("stat_games_played", 0),
            wins = prefs.getInt("stat_wins", 0),
            losses = prefs.getInt("stat_losses", 0),
            tokensCaptured = prefs.getInt("stat_tokens_captured", 0),
            tokensFinished = prefs.getInt("stat_tokens_finished", 0),
            currentStreak = prefs.getInt("stat_current_streak", 0),
            bestStreak = prefs.getInt("stat_best_streak", 0)
        )
    }

    fun recordGameFinished(isHumanWin: Boolean, capturedCount: Int, finishedCount: Int) {
        val current = loadStatistics()
        val newStreak = if (isHumanWin) current.currentStreak + 1 else 0
        val newBestStreak = maxOf(current.bestStreak, newStreak)

        val updated = current.copy(
            gamesPlayed = current.gamesPlayed + 1,
            wins = current.wins + (if (isHumanWin) 1 else 0),
            losses = current.losses + (if (isHumanWin) 0 else 1),
            tokensCaptured = current.tokensCaptured + capturedCount,
            tokensFinished = current.tokensFinished + finishedCount,
            currentStreak = newStreak,
            bestStreak = newBestStreak
        )

        prefs.edit()
            .putInt("stat_games_played", updated.gamesPlayed)
            .putInt("stat_wins", updated.wins)
            .putInt("stat_losses", updated.losses)
            .putInt("stat_tokens_captured", updated.tokensCaptured)
            .putInt("stat_tokens_finished", updated.tokensFinished)
            .putInt("stat_current_streak", updated.currentStreak)
            .putInt("stat_best_streak", updated.bestStreak)
            .apply()
    }

    fun resetStatistics() {
        prefs.edit()
            .remove("stat_games_played")
            .remove("stat_wins")
            .remove("stat_losses")
            .remove("stat_tokens_captured")
            .remove("stat_tokens_finished")
            .remove("stat_current_streak")
            .remove("stat_best_streak")
            .apply()
    }

    /**
     * Copies and squares an image from Uri into internal app cache/avatars, optimizing it to max 256x256
     */
    fun savePlayerAvatar(playerId: String, sourceUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // Center square crop
            val minEdge = minOf(originalBitmap.width, originalBitmap.height)
            val xOffset = (originalBitmap.width - minEdge) / 2
            val yOffset = (originalBitmap.height - minEdge) / 2
            val squareBitmap = Bitmap.createBitmap(originalBitmap, xOffset, yOffset, minEdge, minEdge)

            // Scaled down to 256x256
            val scaledBitmap = Bitmap.createScaledBitmap(squareBitmap, 256, 256, true)

            val avatarDir = File(context.filesDir, "avatars")
            if (!avatarDir.exists()) avatarDir.mkdirs()

            val avatarFile = File(avatarDir, "avatar_$playerId.jpg")
            val outputStream = FileOutputStream(avatarFile)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.flush()
            outputStream.close()

            if (squareBitmap != originalBitmap) squareBitmap.recycle()
            if (scaledBitmap != squareBitmap) scaledBitmap.recycle()
            originalBitmap.recycle()

            avatarFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    fun removePlayerAvatar(playerId: String) {
        try {
            val avatarFile = File(context.filesDir, "avatars/avatar_$playerId.jpg")
            if (avatarFile.exists()) {
                avatarFile.delete()
            }
        } catch (_: Exception) {}
    }
}
