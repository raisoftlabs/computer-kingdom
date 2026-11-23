package com.raisoftlabs.computerkingdom.game

enum class Language { ENGLISH, BANGLA }

enum class CharacterId { BYTE, CORE, RAMI, STELLA, PIXEL, VOLT, MOTHERBOARD }

enum class Expression { HAPPY, THINK, SURPRISED }

enum class ChapterId { KINGDOM, CPU, RAM, SSD }

enum class GameId { FIND_MISSING_PART, INSTRUCTION_ORDER, MEMORY_DESK, STORAGE_SORT }

enum class LocationId { POWER, CONNECTION, PROCESSOR, MEMORY, STORAGE, GRAPHICS, HALL }

data class DialogueLine(
    val speaker: String,
    val character: CharacterId,
    val text: String,
    val expression: Expression = Expression.HAPPY,
)

data class ExplainStep(
    val title: String,
    val body: String,
    val character: CharacterId,
)

data class Lesson(
    val chapter: ChapterId,
    val location: LocationId,
    val title: String,
    val subtitle: String,
    val intro: List<DialogueLine>,
    val steps: List<ExplainStep>,
    val game: GameId,
    val challenge: String,
    val takeaway: String,
    val cardName: String,
    val badge: String,
)

data class ComponentInfo(
    val character: CharacterId,
    val name: String,
    val short: String,
    val description: String,
)

data class CollectionCard(
    val id: String,
    val name: String,
    val character: CharacterId,
    val requirement: String,
)

object GameRules {
    fun isUnlocked(completed: Set<ChapterId>, chapter: ChapterId): Boolean = when (chapter) {
        ChapterId.KINGDOM -> true
        ChapterId.CPU -> ChapterId.KINGDOM in completed
        ChapterId.RAM -> ChapterId.CPU in completed
        ChapterId.SSD -> ChapterId.RAM in completed
    }

    fun starsForMistakes(mistakes: Int): Int = when {
        mistakes == 0 -> 3
        mistakes <= 2 -> 2
        else -> 1
    }

    fun chapterForLocation(location: LocationId): ChapterId? = when (location) {
        LocationId.PROCESSOR -> ChapterId.CPU
        LocationId.MEMORY -> ChapterId.RAM
        LocationId.STORAGE -> ChapterId.SSD
        else -> null
    }
}
