package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance ASMR Sound & Music Engine for Amaze GO.
 *
 * Features:
 * - SoundPool architecture: Zero-latency instantaneous sound effects.
 * - Organic acoustic physical synthesis: Bamboo/wood slides, temple block knocks, liquid bubble pops,
 *   crystal music box victory arpeggios, and singing bell pings.
 * - Zen Ambient Background Music: Tranquil 16-second Kalimba & Tibetan singing bowl loop.
 * - Multi-version vibration engine with fallback for all Android API levels.
 */
class SoundManager(private val context: Context) {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    var isMusicEnabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                startMusic()
            } else {
                stopMusic()
            }
        }

    private var mediaPlayer: MediaPlayer? = null
    private var isPreparingMedia = false

    private val soundPool: SoundPool
    private var soundWhooshId = 0
    private var soundKnockId = 0
    private var soundPopId = 0
    private var soundChimeId = 0
    private var soundHintId = 0
    private var soundClickId = 0

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build()

        audioScope.launch {
            initSoundEffects()
        }
    }

    private fun initSoundEffects() {
        try {
            val cacheDir = context.cacheDir

            // 1. Whoosh / Wood slide
            val whooshFile = File(cacheDir, "sfx_whoosh_v6.wav")
            generateWhooshWav(whooshFile)
            soundWhooshId = soundPool.load(whooshFile.absolutePath, 1)

            // 2. Double Hardwood Knock ("tok-tok")
            val knockFile = File(cacheDir, "sfx_knock_v6.wav")
            generateKnockWav(knockFile)
            soundKnockId = soundPool.load(knockFile.absolutePath, 1)

            // 3. Liquid Droplet Pop
            val popFile = File(cacheDir, "sfx_pop_v6.wav")
            generatePopWav(popFile)
            soundPopId = soundPool.load(popFile.absolutePath, 1)

            // 4. Crystal Music Box Victory Arpeggio
            val chimeFile = File(cacheDir, "sfx_chime_v6.wav")
            generateChimeWav(chimeFile)
            soundChimeId = soundPool.load(chimeFile.absolutePath, 1)

            // 5. Singing Bell Ping
            val hintFile = File(cacheDir, "sfx_hint_v6.wav")
            generateHintWav(hintFile)
            soundHintId = soundPool.load(hintFile.absolutePath, 1)

            // 6. Wooden UI Click
            val clickFile = File(cacheDir, "sfx_click_v6.wav")
            generateClickWav(clickFile)
            soundClickId = soundPool.load(clickFile.absolutePath, 1)
        } catch (_: Exception) {}
    }

    fun ensureAudibleVolume() {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume < (maxVolume * 0.70f).toInt()) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    (maxVolume * 0.85f).toInt(),
                    0
                )
            }
        } catch (_: Exception) {}
    }

    fun startMusic() {
        if (!isMusicEnabled) return

        audioScope.launch {
            ensureAudibleVolume()
            try {
                if (mediaPlayer?.isPlaying == true) return@launch

                if (mediaPlayer != null) {
                    mediaPlayer?.start()
                    return@launch
                }

                if (isPreparingMedia) return@launch
                isPreparingMedia = true

                val wavFile = File(context.cacheDir, "zen_kalimba_v6.wav")
                generateZenKalimbaWav(wavFile)

                val mp = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(wavFile.absolutePath)
                    isLooping = true
                    setVolume(0.90f, 0.90f)
                    prepare()
                }

                mediaPlayer = mp
                isPreparingMedia = false

                if (isMusicEnabled) {
                    mp.start()
                } else {
                    mp.pause()
                }
            } catch (_: Exception) {
                isPreparingMedia = false
            }
        }
    }

    fun stopMusic() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            soundPool.release()
        } catch (_: Exception) {}
    }

    fun playSlideWhoosh() {
        if (isHapticsEnabled) {
            vibrateTick()
        }
        if (!isSoundEnabled) return
        if (soundWhooshId != 0) {
            soundPool.play(soundWhooshId, 0.95f, 0.95f, 1, 0, 1.0f)
        }
    }

    fun playBlockError() {
        if (isHapticsEnabled) {
            vibrateError()
        }
        if (!isSoundEnabled) return
        if (soundKnockId != 0) {
            soundPool.play(soundKnockId, 1.0f, 1.0f, 2, 0, 1.0f)
        }
    }

    fun playWaterDropPop() {
        if (isHapticsEnabled) {
            vibrateTick()
        }
        if (!isSoundEnabled) return
        if (soundPopId != 0) {
            soundPool.play(soundPopId, 0.90f, 0.90f, 1, 0, 1.0f)
        }
    }

    fun playVictoryChime() {
        if (isHapticsEnabled) {
            vibrateSuccess()
        }
        if (!isSoundEnabled) return
        if (soundChimeId != 0) {
            soundPool.play(soundChimeId, 1.0f, 1.0f, 3, 0, 1.0f)
        }
    }

    fun playHintPing() {
        if (isHapticsEnabled) {
            vibrateTick()
        }
        if (!isSoundEnabled) return
        if (soundHintId != 0) {
            soundPool.play(soundHintId, 0.90f, 0.90f, 1, 0, 1.0f)
        }
    }

    fun playButtonClick() {
        if (isHapticsEnabled) {
            vibrateTick()
        }
        if (!isSoundEnabled) return
        if (soundClickId != 0) {
            soundPool.play(soundClickId, 0.80f, 0.80f, 1, 0, 1.0f)
        }
    }

    fun vibrateTick() {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40L)
            }
        } catch (_: Exception) {}
    }

    fun vibrateError() {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 45, 60), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100L)
            }
        } catch (_: Exception) {}
    }

    fun vibrateSuccess() {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 35, 45, 35, 75), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(140L)
            }
        } catch (_: Exception) {}
    }

    // --- Audio Synthesis Engine ---

    private fun generateWhooshWav(file: File) {
        if (file.exists() && file.length() > 5000L) return
        val sampleRate = 22050
        val durationMs = 240
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toFloat() / numSamples
            val tSec = i.toFloat() / sampleRate
            val env = sin(t * PI.toFloat()) * (1f - t * 0.20f)

            val freq = 460f - 200f * t
            val woodTone = sin(2.0 * PI * freq * tSec).toFloat() * 0.50f +
                    sin(2.0 * PI * (freq * 1.85f) * tSec).toFloat() * 0.22f

            // Smooth friction noise
            val noise = ((i * 1103515245 + 12345) % 1000).toFloat() / 500f - 1f
            val friction = noise * (0.22f * (1f - t))

            val catch = if (t > 0.82f) {
                val catchT = (t - 0.82f) / 0.18f
                sin(catchT * PI.toFloat()) * sin(2.0 * PI * 240.0 * tSec).toFloat() * 0.38f
            } else 0f

            val sample = ((woodTone + friction + catch) * env * Short.MAX_VALUE * 0.90f).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeWavFile(file, buffer, sampleRate)
    }

    private fun generateKnockWav(file: File) {
        if (file.exists() && file.length() > 5000L) return
        val sampleRate = 22050
        val durationMs = 180
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        val tap1Samples = (sampleRate * 0.055).toInt()
        val tap2Start = (sampleRate * 0.065).toInt()
        val tap2Samples = (sampleRate * 0.060).toInt()

        for (i in 0 until numSamples) {
            var s = 0f
            if (i < tap1Samples) {
                val t = i.toFloat() / sampleRate
                val env = exp(-t * 50.0).toFloat()
                val f = 390f - 90f * (i.toFloat() / tap1Samples)
                val wood = sin(2.0 * PI * f * t).toFloat() * 0.72f +
                        sin(2.0 * PI * (f * 2.2f) * t).toFloat() * 0.25f
                s += wood * env
            }
            if (i in tap2Start until (tap2Start + tap2Samples)) {
                val t = (i - tap2Start).toFloat() / sampleRate
                val env = exp(-t * 46.0).toFloat()
                val f = 310f - 70f * ((i - tap2Start).toFloat() / tap2Samples)
                val wood = sin(2.0 * PI * f * t).toFloat() * 0.68f +
                        sin(2.0 * PI * (f * 2.15f) * t).toFloat() * 0.22f
                s += wood * env * 0.88f
            }
            val pcm = (s * Short.MAX_VALUE * 0.92f).toInt()
            buffer[i] = pcm.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeWavFile(file, buffer, sampleRate)
    }

    private fun generatePopWav(file: File) {
        if (file.exists() && file.length() > 5000L) return
        val sampleRate = 22050
        val durationMs = 130
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val normT = i.toFloat() / numSamples
            val freq = 490f + 820f * (normT * normT)
            val env = exp(-t * 26.0).toFloat()
            val wave = sin(2.0 * PI * freq * t).toFloat() +
                    0.25f * sin(2.0 * PI * (freq * 2.0f) * t).toFloat()
            val sample = (wave * env * Short.MAX_VALUE * 0.90f).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeWavFile(file, buffer, sampleRate)
    }

    private fun generateChimeWav(file: File) {
        if (file.exists() && file.length() > 15000L) return
        val sampleRate = 22050
        val durationMs = 1100
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        val notes = listOf(523.25f, 659.25f, 783.99f, 987.77f, 1046.50f, 1318.51f)
        val noteSpacingSamples = (sampleRate * 0.085).toInt()

        for (i in 0 until numSamples) {
            var totalSample = 0f
            for (n in notes.indices) {
                val noteStart = n * noteSpacingSamples
                if (i >= noteStart) {
                    val t = (i - noteStart).toFloat() / sampleRate
                    if (t < 0.70f) {
                        val decay = exp(-t * 4.5).toFloat()
                        val f = notes[n]
                        val bell = sin(2.0 * PI * f * t).toFloat() * 0.55f +
                                sin(2.0 * PI * (f * 2.0f) * t).toFloat() * 0.28f +
                                sin(2.0 * PI * (f * 3.02f) * t).toFloat() * 0.12f +
                                sin(2.0 * PI * (f * 4.04f) * t).toFloat() * 0.05f
                        totalSample += bell * decay * 0.35f
                    }
                }
            }
            buffer[i] = (totalSample * Short.MAX_VALUE * 0.92f).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeWavFile(file, buffer, sampleRate)
    }

    private fun generateHintWav(file: File) {
        if (file.exists() && file.length() > 5000L) return
        val sampleRate = 22050
        val durationMs = 480
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val attack = (t / 0.008f).coerceIn(0f, 1f)
            val decay = exp(-t * 5.0).toFloat()
            val shimmer = 1f + 0.15f * sin(2.0 * PI * 4.5 * t).toFloat()

            val chime = sin(2.0 * PI * 880.0 * t).toFloat() * 0.65f +
                    sin(2.0 * PI * 1760.0 * t).toFloat() * 0.25f +
                    sin(2.0 * PI * 2640.0 * t).toFloat() * 0.10f

            val sample = (chime * attack * decay * shimmer * Short.MAX_VALUE * 0.88f).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeWavFile(file, buffer, sampleRate)
    }

    private fun generateClickWav(file: File) {
        if (file.exists() && file.length() > 2000L) return
        val sampleRate = 22050
        val durationMs = 35
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 85.0).toFloat()
            val f = 480f - 220f * (i.toFloat() / numSamples)
            val wave = sin(2.0 * PI * f * t).toFloat()
            buffer[i] = (wave * decay * Short.MAX_VALUE * 0.85f).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeWavFile(file, buffer, sampleRate)
    }

    private fun generateZenKalimbaWav(file: File) {
        if (file.exists() && file.length() > 300000L) return

        val sampleRate = 22050
        val durationSeconds = 16
        val totalSamples = sampleRate * durationSeconds
        val buffer = ShortArray(totalSamples)

        val melody = listOf(
            Triple(0.25, 329.63, 0.85), // E4
            Triple(1.05, 493.88, 0.75), // B4
            Triple(1.85, 392.00, 0.80), // G4
            Triple(2.65, 587.33, 0.70), // D5
            Triple(3.40, 493.88, 0.65), // B4

            Triple(4.25, 329.63, 0.80), // E4
            Triple(5.00, 440.00, 0.75), // A4
            Triple(5.75, 493.88, 0.75), // B4
            Triple(6.50, 659.25, 0.70), // E5
            Triple(7.30, 587.33, 0.65), // D5

            Triple(8.20, 196.00, 0.88), // G3
            Triple(8.30, 392.00, 0.75), // G4
            Triple(9.10, 493.88, 0.70), // B4
            Triple(9.90, 587.33, 0.75), // D5
            Triple(10.65, 783.99, 0.60), // G5
            Triple(11.45, 659.25, 0.70), // E5

            Triple(12.25, 220.00, 0.82), // A3
            Triple(13.05, 440.00, 0.75), // A4
            Triple(13.85, 392.00, 0.75), // G4
            Triple(14.60, 329.63, 0.80), // E4
            Triple(15.30, 493.88, 0.60)  // B4
        )

        for (i in 0 until totalSamples) {
            val tTotal = i.toDouble() / sampleRate

            val droneEnv = 0.85 + 0.15 * cos(2.0 * PI * tTotal / 8.0)
            val bowlE3 = sin(2.0 * PI * 164.81 * tTotal) * 0.050
            val bowlB3 = sin(2.0 * PI * 246.94 * tTotal) * 0.035
            val bowlBeat = sin(2.0 * PI * 165.40 * tTotal) * 0.025
            val bowlPad = (bowlE3 + bowlB3 + bowlBeat) * droneEnv

            var kalimbaAcc = 0.0
            for ((startSec, freq, vel) in melody) {
                val tNote = tTotal - startSec
                if (tNote in 0.0..2.8) {
                    val attack = (tNote / 0.004).coerceIn(0.0, 1.0)
                    val envFund = exp(-tNote * 1.35)
                    val envOver1 = exp(-tNote * 3.8)
                    val envOver2 = exp(-tNote * 8.5)

                    val mallet = if (tNote < 0.010) {
                        (1.0 - tNote / 0.010) * sin(2.0 * PI * 140.0 * tNote) * 0.16
                    } else 0.0

                    val fund = sin(2.0 * PI * freq * tNote) * 0.65 * envFund
                    val overtone1 = sin(2.0 * PI * (freq * 2.756) * tNote) * 0.22 * envOver1
                    val overtone2 = sin(2.0 * PI * (freq * 5.404) * tNote) * 0.08 * envOver2

                    kalimbaAcc += (mallet + fund + overtone1 + overtone2) * attack * vel
                }
            }

            var sampleMix = (bowlPad + kalimbaAcc * 0.40)
            val crossfadeSec = 0.35
            if (tTotal < crossfadeSec) {
                sampleMix *= (tTotal / crossfadeSec)
            } else if (tTotal > durationSeconds - crossfadeSec) {
                sampleMix *= ((durationSeconds - tTotal) / crossfadeSec)
            }

            val pcmVal = (sampleMix * Short.MAX_VALUE * 0.90).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = pcmVal.toShort()
        }

        writeWavFile(file, buffer, sampleRate)
    }

    private fun writeWavFile(file: File, buffer: ShortArray, sampleRate: Int) {
        val totalAudioLen = (buffer.size * 2).toLong()
        val totalDataLen = totalAudioLen + 36
        val byteRate = (sampleRate * 2).toLong()

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0 // PCM
        header[22] = 1; header[23] = 0 // mono
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 2; header[33] = 0
        header[34] = 16; header[35] = 0
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        try {
            val fos = FileOutputStream(file)
            fos.write(header)
            val byteBuffer = ByteBuffer.allocate(buffer.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (s in buffer) {
                byteBuffer.putShort(s)
            }
            fos.write(byteBuffer.array())
            fos.close()
        } catch (_: Exception) {}
    }
}
