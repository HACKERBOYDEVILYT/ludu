package com.example.rsludo.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {

    var soundEnabled: Boolean = true
    var hapticEnabled: Boolean = true

    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private fun playTone(
        frequencies: FloatArray,
        durationsMs: IntArray,
        type: ToneType = ToneType.SINE,
        gain: Float = 0.5f
    ) {
        if (!soundEnabled) return

        scope.launch {
            try {
                val sampleRate = 22050
                var totalSamples = 0
                durationsMs.forEach { totalSamples += (sampleRate * it) / 1000 }
                val buffer = ShortArray(totalSamples)

                var offset = 0
                for (i in frequencies.indices) {
                    val freq = frequencies[i]
                    val durationMs = durationsMs[i]
                    val samples = (sampleRate * durationMs) / 1000
                    val angularFreq = 2.0 * PI * freq / sampleRate

                    for (s in 0 until samples) {
                        val envelope = when {
                            s < samples * 0.1f -> s / (samples * 0.1f) // Attack
                            s > samples * 0.7f -> (samples - s) / (samples * 0.3f) // Decay
                            else -> 1f
                        }

                        val sampleVal = when (type) {
                            ToneType.SINE -> sin(angularFreq * s)
                            ToneType.SQUARE -> if (sin(angularFreq * s) > 0) 0.8 else -0.8
                            ToneType.NOISE -> (Math.random() * 2.0 - 1.0)
                        }

                        val shortVal = (sampleVal * envelope * gain * Short.MAX_VALUE).toInt()
                        if (offset + s < buffer.size) {
                            buffer[offset + s] = shortVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    offset += samples
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val track = AudioTrack(
                    audioAttributes,
                    audioFormat,
                    buffer.size * 2,
                    AudioTrack.MODE_STATIC,
                    android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
                )

                track.write(buffer, 0, buffer.size)
                track.play()
                // Sleep briefly then release track
                Thread.sleep((totalSamples * 1000L / sampleRate) + 50)
                track.stop()
                track.release()
            } catch (_: Exception) {
                // Audio failure should never crash the game
            }
        }
    }

    enum class ToneType { SINE, SQUARE, NOISE }

    fun playDice() {
        // Multi-frequency rattle
        playTone(
            floatArrayOf(420f, 680f, 320f, 750f, 520f, 820f),
            intArrayOf(35, 35, 35, 40, 40, 50),
            ToneType.SQUARE,
            0.4f
        )
        vibrateDice()
    }

    fun playTokenTap() {
        playTone(floatArrayOf(880f, 1320f), intArrayOf(30, 40), ToneType.SINE, 0.45f)
        vibrateTap()
    }

    fun playTokenMove(stepIndex: Int = 0) {
        val baseFreq = 523.25f // C5
        val freq = baseFreq + (stepIndex % 6) * 65f
        playTone(floatArrayOf(freq), intArrayOf(45), ToneType.SINE, 0.4f)
    }

    fun playCapture() {
        playTone(
            floatArrayOf(300f, 180f, 90f, 60f),
            intArrayOf(60, 80, 100, 140),
            ToneType.SQUARE,
            0.6f
        )
        vibrateCapture()
    }

    fun playHome() {
        // Uplifting triad: C5, E5, G5, C6
        playTone(
            floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f),
            intArrayOf(70, 70, 90, 200),
            ToneType.SINE,
            0.5f
        )
        vibrateHome()
    }

    fun playWin() {
        // Victory fanfare
        playTone(
            floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f, 783.99f, 1046.50f),
            intArrayOf(100, 100, 100, 220, 100, 400),
            ToneType.SQUARE,
            0.55f
        )
        vibrateWin()
    }

    fun playTurn() {
        playTone(floatArrayOf(660f, 880f), intArrayOf(40, 50), ToneType.SINE, 0.35f)
    }

    fun playReaction() {
        playTone(floatArrayOf(740f, 980f), intArrayOf(30, 40), ToneType.SINE, 0.4f)
    }

    // --- Haptics ---
    fun vibrateTap() {
        if (!hapticEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    fun vibrateDice() {
        if (!hapticEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 25, 30, 35), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    fun vibrateCapture() {
        if (!hapticEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 40, 90), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    fun vibrateHome() {
        if (!hapticEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 30, 40, 30, 70), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(150)
            }
        } catch (_: Exception) {}
    }

    fun vibrateWin() {
        if (!hapticEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 100, 60, 160), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(250)
            }
        } catch (_: Exception) {}
    }
}
