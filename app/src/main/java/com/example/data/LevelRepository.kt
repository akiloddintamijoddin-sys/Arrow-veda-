package com.example.data

import com.example.model.ArrowLevel
import com.example.model.LevelDifficulty

object LevelRepository {

    private val levelCache = mutableMapOf<Int, ArrowLevel>()

    fun getLevel(id: Int): ArrowLevel {
        val clampedId = id.coerceIn(1, 10000)
        return levelCache.getOrPut(clampedId) { createLevel(clampedId) }
    }

    fun getDailyChallengeLevel(): ArrowLevel {
        val todaySeed = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        return LevelGenerator.generateSolvableLevel(
            levelId = 99999,
            title = "Daily Zen Challenge",
            rows = 6,
            cols = 6,
            targetArrowCount = 28,
            difficulty = LevelDifficulty.HARD,
            chapter = "Daily Special",
            seed = todaySeed * 7919L
        )
    }

    private fun createLevel(id: Int): ArrowLevel {
        val (rows, cols, count, diff, chapter) = when {
            id <= 50 -> {
                val arrowCount = 24 + (id * 6 / 50)
                Tuple5(12, 12, arrowCount, LevelDifficulty.BEGINNER, "Novice")
            }
            id <= 200 -> {
                val arrowCount = 30 + ((id - 50) * 8 / 150)
                Tuple5(14, 14, arrowCount, LevelDifficulty.EASY, "Apprentice")
            }
            id <= 800 -> {
                val arrowCount = 38 + ((id - 200) * 10 / 600)
                Tuple5(15, 15, arrowCount, LevelDifficulty.MEDIUM, "Adept")
            }
            id <= 2500 -> {
                val arrowCount = 48 + ((id - 800) * 10 / 1700)
                Tuple5(16, 16, arrowCount, LevelDifficulty.HARD, "Master")
            }
            id <= 6000 -> {
                val arrowCount = 58 + ((id - 2500) * 10 / 3500)
                Tuple5(18, 18, arrowCount, LevelDifficulty.EXPERT, "Grandmaster")
            }
            else -> {
                val arrowCount = 68 + ((id - 6000) * 12 / 4000)
                Tuple5(20, 20, arrowCount, LevelDifficulty.MASTER, "Ascended")
            }
        }

        val title = getLevelTitle(id, diff)
        return LevelGenerator.generateSolvableLevel(
            levelId = id,
            title = title,
            rows = rows,
            cols = cols,
            targetArrowCount = count,
            difficulty = diff,
            chapter = chapter,
            seed = id * 4391L + 104729L
        )
    }

    private fun getLevelTitle(id: Int, difficulty: LevelDifficulty): String {
        val titles = listOf(
            "Whispering Pines", "Gentle Breeze", "Echo Creek", "Quiet Dawn",
            "Bamboo Path", "Silver Stream", "Moonlit Mist", "Stone Garden",
            "Morning Dew", "Cedar Grove", "Ripple Pool", "Autumn Breeze",
            "Lotus Bloom", "Cloud Break", "Sunken Garden", "Willow Shade",
            "Hidden Spring", "Sunset Ridge", "Silent Lake", "Mountain Peak",
            "Tidal Wave", "Spiral Dance", "Twin Dragons", "Dragonfly",
            "Canyon Echo", "Shadow Box", "Mirror Lake", "Whirlpool",
            "Emerald Maze", "Starfall", "Labyrinth Walk", "Solar Wind",
            "Nebula Gate", "Zenith Arc", "Prism Wave", "Abyssal Gate",
            "Celestial Knot", "Chrono Latch", "Singularity", "Infinite Orbit",
            "Event Horizon", "Cosmic Tangle", "Quantum Knot", "Nova Burst",
            "Apex Zenith", "Astral Core", "Vortex Loom", "Seraph Wing"
        )
        val index = (id - 1) % titles.size
        return titles[index]
    }

    private data class Tuple5<A, B, C, D, E>(
        val a: A, val b: B, val c: C, val d: D, val e: E
    )
}
