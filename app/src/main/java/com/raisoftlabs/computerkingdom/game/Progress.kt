package com.raisoftlabs.computerkingdom.game

import android.content.Context
import org.json.JSONObject

data class Progress(
    val completed: Set<ChapterId> = emptySet(),
    val stars: Map<ChapterId, Int> = emptyMap(),
    val cards: Set<String> = emptySet(),
    val badges: Set<String> = emptySet(),
) {
    fun withCompletion(chapter: ChapterId, earnedStars: Int, card: String, badge: String): Progress = copy(
        completed = completed + chapter,
        stars = stars + (chapter to maxOf(stars[chapter] ?: 0, earnedStars)),
        cards = cards + card,
        badges = badges + badge,
    )

    fun totalStars(): Int = stars.values.sum()

    companion object {
        private const val PREFS = "ck_progress"

        fun load(context: Context): Progress {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val completed = prefs.getString("completed", "").orEmpty()
                .split(",").filter { it.isNotBlank() }
                .mapNotNull { runCatching { ChapterId.valueOf(it) }.getOrNull() }.toSet()
            val starJson = JSONObject(prefs.getString("stars", "{}").orEmpty())
            val stars = mutableMapOf<ChapterId, Int>()
            val keys = starJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val chapter = runCatching { ChapterId.valueOf(key) }.getOrNull() ?: continue
                stars[chapter] = starJson.optInt(key, 0)
            }
            fun stringSet(key: String) = prefs.getString(key, "").orEmpty()
                .split(",").filter { it.isNotBlank() }.toSet()
            return Progress(completed, stars, stringSet("cards"), stringSet("badges"))
        }

        fun save(context: Context, progress: Progress) {
            val starJson = JSONObject()
            progress.stars.forEach { (chapter, stars) -> starJson.put(chapter.name, stars) }
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString("completed", progress.completed.joinToString(",") { it.name })
                .putString("stars", starJson.toString())
                .putString("cards", progress.cards.joinToString(","))
                .putString("badges", progress.badges.joinToString(","))
                .apply()
        }
    }
}

data class Settings(
    val language: Language = Language.ENGLISH,
    val textScale: Float = 1f,
    val narration: Boolean = true,
    val sound: Boolean = true,
    val playLimitMinutes: Int = 0,
) {
    companion object {
        private const val PREFS = "ck_settings"

        fun load(context: Context): Settings {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val language = runCatching { Language.valueOf(prefs.getString("language", "ENGLISH").orEmpty()) }
                .getOrDefault(Language.ENGLISH)
            return Settings(
                language = language,
                textScale = prefs.getFloat("textScale", 1f).coerceIn(0.85f, 1.3f),
                narration = prefs.getBoolean("narration", true),
                sound = prefs.getBoolean("sound", true),
                playLimitMinutes = prefs.getInt("playLimit", 0),
            )
        }

        fun save(context: Context, settings: Settings) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString("language", settings.language.name)
                .putFloat("textScale", settings.textScale)
                .putBoolean("narration", settings.narration)
                .putBoolean("sound", settings.sound)
                .putInt("playLimit", settings.playLimitMinutes)
                .apply()
        }
    }
}
