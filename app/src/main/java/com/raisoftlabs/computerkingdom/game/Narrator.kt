package com.raisoftlabs.computerkingdom.game

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

object Narrator {
    private var engine: TextToSpeech? = null
    private var language: Language = Language.ENGLISH

    fun init(context: Context, language: Language) {
        this.language = language
        if (engine != null) return
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) setLanguage(this.language)
        }
    }

    fun setLanguage(language: Language) {
        this.language = language
        val locale = if (language == Language.BANGLA) Locale("bn", "BD") else Locale.ENGLISH
        when (engine?.setLanguage(locale)) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> engine?.setLanguage(Locale.ENGLISH)
            else -> Unit
        }
    }

    fun speak(text: String) {
        engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "computer-kingdom")
    }

    fun stop() {
        engine?.stop()
    }
}

object Sfx {
    private val handler = Handler(Looper.getMainLooper())

    fun tap() = tone(660, 70, 0.12f)

    fun success() {
        tone(720, 90, 0.16f)
        handler.postDelayed({ tone(980, 150, 0.16f) }, 100L)
    }

    fun wrong() = tone(210, 180, 0.12f)

    private fun tone(frequency: Int, durationMs: Int, amplitude: Float) {
        val sampleRate = 44100
        val count = sampleRate * durationMs / 1000
        val data = ShortArray(count) { index ->
            (sin(2.0 * PI * frequency * index / sampleRate) * 32767 * amplitude).toInt().toShort()
        }
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(data.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(data, 0, data.size)
        track.play()
        handler.postDelayed({ track.release() }, durationMs + 120L)
    }
}
