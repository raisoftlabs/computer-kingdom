package com.raisoftlabs.computerkingdom.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Map : Screen
    data object Explore : Screen
    data object Collection : Screen
    data object ParentGate : Screen
    data object Parent : Screen
    data object Settings : Screen
    data class Story(val lines: List<DialogueLine>, val chapter: ChapterId?, val next: Screen) : Screen
    data class Lesson(val chapter: ChapterId) : Screen
    data class Game(val game: GameId, val chapter: ChapterId) : Screen
    data class Reward(val chapter: ChapterId, val stars: Int) : Screen
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    var screen by mutableStateOf<Screen>(Screen.Splash)
        private set

    var progress by mutableStateOf(Progress.load(application))
        private set

    var settings by mutableStateOf(Settings.load(application))
        private set

    var showBreak by mutableStateOf(false)
        private set

    var toast by mutableStateOf<String?>(null)
        private set

    var gateAnswer by mutableStateOf(9)
        private set

    private var sessionStart = System.currentTimeMillis()
    private var breakShown = false

    val text: UiText get() = uiText(settings.language)
    val lessonList: List<Lesson> get() = lessons(settings.language)
    val gameText: GamesText get() = gamesText(settings.language)
    val componentText: ComponentInfoText get() = components(settings.language)
    val cardList: List<CollectionCard> get() = collectionCards(settings.language)

    fun lesson(chapter: ChapterId): Lesson = lessonList.first { it.chapter == chapter }

    fun navigate(next: Screen) {
        screen = next
        toast = null
        Narrator.stop()
        checkPlayTime()
    }

    fun showMessage(message: String) {
        toast = message
    }

    fun clearMessage() {
        toast = null
    }

    fun startChapter(chapter: ChapterId) {
        val lesson = lesson(chapter)
        navigate(Screen.Story(lesson.intro, chapter, Screen.Lesson(chapter)))
    }

    fun setLanguage(language: Language) {
        settings = settings.copy(language = language)
        Settings.save(getApplication(), settings)
        Narrator.setLanguage(language)
    }

    fun setTextScale(scale: Float) {
        settings = settings.copy(textScale = scale)
        Settings.save(getApplication(), settings)
    }

    fun setNarration(enabled: Boolean) {
        settings = settings.copy(narration = enabled)
        Settings.save(getApplication(), settings)
        if (!enabled) Narrator.stop()
    }

    fun setSound(enabled: Boolean) {
        settings = settings.copy(sound = enabled)
        Settings.save(getApplication(), settings)
    }

    fun setPlayLimit(minutes: Int) {
        settings = settings.copy(playLimitMinutes = minutes)
        Settings.save(getApplication(), settings)
        sessionStart = System.currentTimeMillis()
        breakShown = false
    }

    fun checkPlayTime() {
        val limit = settings.playLimitMinutes
        if (limit <= 0 || breakShown) return
        val elapsed = System.currentTimeMillis() - sessionStart
        if (elapsed >= limit * 60_000L) {
            breakShown = true
            showBreak = true
            Narrator.stop()
        }
    }

    fun dismissBreak() {
        showBreak = false
        sessionStart = System.currentTimeMillis()
        breakShown = false
    }

    fun newGateQuestion() {
        gateAnswer = Random.nextInt(5, 12)
    }

    fun completeChapter(chapter: ChapterId, earnedStars: Int) {
        val lesson = lesson(chapter)
        progress = progress.withCompletion(chapter, earnedStars, cardId(lesson.cardName), "badge_${chapter.name.lowercase()}")
        Progress.save(getApplication(), progress)
        playCorrect()
        navigate(Screen.Reward(chapter, earnedStars))
    }

    fun resetProgress() {
        progress = Progress()
        Progress.save(getApplication(), progress)
        playTap()
    }

    fun playTap() {
        if (settings.sound) Sfx.tap()
    }

    fun playCorrect() {
        if (settings.sound) Sfx.success()
    }

    fun playWrong() {
        if (settings.sound) Sfx.wrong()
    }

    private fun cardId(cardName: String): String = when (cardName) {
        lessonList[0].cardName -> "byte"
        lessonList[1].cardName -> "core"
        lessonList[2].cardName -> "rami"
        else -> "stella"
    }

    init {
        newGateQuestion()
    }
}
