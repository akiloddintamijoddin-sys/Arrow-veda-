package com.example

import com.example.data.LabyrinthGenerator
import com.example.data.LevelGenerator
import com.example.data.LevelRepository
import com.example.model.Direction
import com.example.model.GridPoint
import com.example.model.LevelDifficulty
import com.example.model.PathArrow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArrowGameLogicTest {

    @Test
    fun testLabyrinthGenerationAcross10000Levels() {
        // Test diverse levels across the entire 10,000 range:
        // Level 1, Level 2, Level 27 (Anchor screenshot), Level 40 (Dog screenshot), Level 100, Level 500, Level 2500, Level 10000
        val testIds = listOf(1, 2, 27, 40, 100, 500, 1000, 2500, 5000, 10000)

        for (id in testIds) {
            val level = LabyrinthGenerator.generateLevel(id)
            assertNotNull(level)
            assertTrue("Level $id must have arrows", level.arrows.isNotEmpty())
            assertTrue("Level $id must be challenging with >= 15 arrows (got ${level.arrows.size})", level.arrows.size >= 15)
            assertTrue("Level $id arrows must have at least 2 points", level.arrows.all { it.points.size >= 2 })
            val isSolvable = LabyrinthGenerator.isSolvable(level.rows, level.cols, level.arrows)
            assertTrue("Labyrinth level $id (${level.title}) must be 100% solvable", isSolvable)
            val clearArrows = LabyrinthGenerator.getClearArrows(level.rows, level.cols, level.arrows)
            println("=== Level $id (${level.title}): arrows=${level.arrows.size}, initiallyClear=${clearArrows.size} ===")
            assertTrue("Level $id must have at least 1 initially clear arrow", clearArrows.isNotEmpty())
            assertTrue("Level $id must have blocked arrows creating a genuine tangle", clearArrows.size < level.arrows.size)
        }
    }

    @Test
    fun testLevelRepositoryAcross10000Levels() {
        val lvl1 = LevelRepository.getLevel(1)
        val lvl100 = LevelRepository.getLevel(100)
        val lvl1000 = LevelRepository.getLevel(1000)
        val lvl10000 = LevelRepository.getLevel(10000)

        assertEquals(LevelDifficulty.BEGINNER, lvl1.difficulty)
        assertEquals(LevelDifficulty.EASY, lvl100.difficulty)
        assertEquals(LevelDifficulty.HARD, lvl1000.difficulty)
        assertEquals(LevelDifficulty.MASTER, lvl10000.difficulty)

        assertTrue(LevelGenerator.isLevelSolvable(lvl1))
        assertTrue(LevelGenerator.isLevelSolvable(lvl100))
        assertTrue(LevelGenerator.isLevelSolvable(lvl10000))
    }

    @Test
    fun testLabyrinthRayClearanceAndCollision() {
        // Arrow 1 at (5, 5) pointing UP
        val arrow1 = PathArrow(
            id = 1,
            points = listOf(GridPoint(6, 5), GridPoint(5, 5)),
            direction = Direction.UP
        )

        // Arrow 2 blocking the ray at (2, 5)
        val blocker = PathArrow(
            id = 2,
            points = listOf(GridPoint(2, 4), GridPoint(2, 5)),
            direction = Direction.RIGHT
        )

        val activeList = listOf(arrow1, blocker)

        // Arrow 1 should be blocked because blocker is in its UP ray (at 2, 5)
        val isClear = LabyrinthGenerator.isRayClear(
            arrow1.head, arrow1.direction, 10, 10, activeList, arrow1.id
        )
        assertFalse("Arrow 1 should be blocked by blocker in ray", isClear)

        // Blocker pointing RIGHT is clear to exit boundary
        val isBlockerClear = LabyrinthGenerator.isRayClear(
            blocker.head, blocker.direction, 10, 10, activeList, blocker.id
        )
        assertTrue("Blocker pointing RIGHT to boundary should be clear", isBlockerClear)
    }

    @Test
    fun testDailyChallengeSolvable() {
        val daily = LevelRepository.getDailyChallengeLevel()
        assertTrue("Daily challenge must be solvable", LevelGenerator.isLevelSolvable(daily))
    }
}
