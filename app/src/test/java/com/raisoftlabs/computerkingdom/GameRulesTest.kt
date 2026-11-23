package com.raisoftlabs.computerkingdom

import com.raisoftlabs.computerkingdom.game.ChapterId
import com.raisoftlabs.computerkingdom.game.GameRules
import com.raisoftlabs.computerkingdom.game.LocationId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRulesTest {
    @Test
    fun chaptersUnlockInOrder() {
        assertTrue(GameRules.isUnlocked(emptySet(), ChapterId.KINGDOM))
        assertFalse(GameRules.isUnlocked(emptySet(), ChapterId.CPU))
        val kingdomDone = setOf(ChapterId.KINGDOM)
        assertTrue(GameRules.isUnlocked(kingdomDone, ChapterId.CPU))
        assertFalse(GameRules.isUnlocked(kingdomDone, ChapterId.RAM))
        val cpuDone = kingdomDone + ChapterId.CPU
        assertTrue(GameRules.isUnlocked(cpuDone, ChapterId.RAM))
        assertFalse(GameRules.isUnlocked(cpuDone, ChapterId.SSD))
        assertTrue(GameRules.isUnlocked(cpuDone + ChapterId.RAM, ChapterId.SSD))
    }

    @Test
    fun starsRewardUnderstandingNotSpeed() {
        assertEquals(3, GameRules.starsForMistakes(0))
        assertEquals(2, GameRules.starsForMistakes(1))
        assertEquals(2, GameRules.starsForMistakes(2))
        assertEquals(1, GameRules.starsForMistakes(9))
    }

    @Test
    fun mapLocationsOpenTheRightLessons() {
        assertEquals(ChapterId.CPU, GameRules.chapterForLocation(LocationId.PROCESSOR))
        assertEquals(ChapterId.RAM, GameRules.chapterForLocation(LocationId.MEMORY))
        assertEquals(ChapterId.SSD, GameRules.chapterForLocation(LocationId.STORAGE))
        assertEquals(null, GameRules.chapterForLocation(LocationId.GRAPHICS))
        assertEquals(null, GameRules.chapterForLocation(LocationId.POWER))
    }
}
