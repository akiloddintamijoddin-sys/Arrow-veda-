package com.example.data

import com.example.model.Arrow
import com.example.model.ArrowLevel
import com.example.model.Direction
import com.example.model.LevelDifficulty
import kotlin.random.Random

object LevelGenerator {

    /**
     * Checks if a ray in [direction] from ([row], [col]) to the border has any other arrows.
     */
    fun isPathClear(
        row: Int,
        col: Int,
        direction: Direction,
        rows: Int,
        cols: Int,
        activeArrows: Map<Pair<Int, Int>, Arrow>
    ): Boolean {
        var r = row + direction.dRow
        var c = col + direction.dCol

        while (r in 0 until rows && c in 0 until cols) {
            if (activeArrows.containsKey(Pair(r, c))) {
                return false
            }
            r += direction.dRow
            c += direction.dCol
        }
        return true
    }

    /**
     * Verifies that the level is 100% solvable by simulating forward topological clearance.
     */
    fun isLevelSolvable(level: ArrowLevel): Boolean {
        val remaining = level.arrows.associateBy { Pair(it.row, it.col) }.toMutableMap()

        while (remaining.isNotEmpty()) {
            val clearArrow = remaining.values.firstOrNull { arrow ->
                isPathClear(arrow.row, arrow.col, arrow.direction, level.rows, level.cols, remaining)
            } ?: return false // Deadlock! Not solvable

            remaining.remove(Pair(clearArrow.row, clearArrow.col))
        }

        return true
    }

    /**
     * Finds all arrows that currently have a clear path to exit.
     */
    fun findClearArrows(
        rows: Int,
        cols: Int,
        arrows: Collection<Arrow>
    ): List<Arrow> {
        val gridMap = arrows.associateBy { Pair(it.row, it.col) }
        return arrows.filter { arrow ->
            isPathClear(arrow.row, arrow.col, arrow.direction, rows, cols, gridMap)
        }
    }

    /**
     * Reverse-generation algorithm that constructs guaranteed-solvable levels.
     * In reverse time, placing an arrow facing D is legal if the ray in direction D
     * does not collide with arrows that will exit AFTER it (i.e. already placed in reverse time).
     */
    fun generateSolvableLevel(
        levelId: Int,
        title: String,
        rows: Int,
        cols: Int,
        targetArrowCount: Int,
        difficulty: LevelDifficulty,
        chapter: String,
        seed: Long = levelId.toLong() * 31337L
    ): ArrowLevel {
        val random = Random(seed)
        val placedArrows = mutableListOf<Arrow>()
        val occupiedCells = mutableSetOf<Pair<Int, Int>>()

        var attempts = 0
        val maxAttempts = 1200
        var arrowIdCounter = 1

        while (placedArrows.size < targetArrowCount && attempts < maxAttempts) {
            attempts++
            val r = random.nextInt(rows)
            val c = random.nextInt(cols)
            val pos = Pair(r, c)

            if (occupiedCells.contains(pos)) continue

            // Pick a direction that has a clear path to the edge among currently placed arrows
            val possibleDirections = Direction.values().filter { dir ->
                var currR = r + dir.dRow
                var currC = c + dir.dCol
                var blocked = false
                while (currR in 0 until rows && currC in 0 until cols) {
                    if (occupiedCells.contains(Pair(currR, currC))) {
                        blocked = true
                        break
                    }
                    currR += dir.dRow
                    currC += dir.dCol
                }
                !blocked
            }

            if (possibleDirections.isNotEmpty()) {
                val chosenDir = possibleDirections[random.nextInt(possibleDirections.size)]
                val arrow = Arrow(
                    id = arrowIdCounter++,
                    row = r,
                    col = c,
                    direction = chosenDir,
                    colorIndex = random.nextInt(4)
                )
                placedArrows.add(arrow)
                occupiedCells.add(pos)
            }
        }

        val level = ArrowLevel(
            id = levelId,
            title = title,
            rows = rows,
            cols = cols,
            arrows = placedArrows,
            difficulty = difficulty,
            chapter = chapter
        )

        // Sanity check: must be verified solvable
        return if (isLevelSolvable(level)) {
            level
        } else {
            // Fallback to simple guaranteed cross pattern if random hit a rare snag
            createFallbackLevel(levelId, title, rows, cols, difficulty, chapter)
        }
    }

    private fun createFallbackLevel(
        levelId: Int,
        title: String,
        rows: Int,
        cols: Int,
        difficulty: LevelDifficulty,
        chapter: String
    ): ArrowLevel {
        val arrows = mutableListOf<Arrow>()
        var id = 1
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if ((r + c) % 2 == 0) {
                    val dir = when {
                        r == 0 -> Direction.UP
                        r == rows - 1 -> Direction.DOWN
                        c == 0 -> Direction.LEFT
                        c == cols - 1 -> Direction.RIGHT
                        else -> if (c < cols / 2) Direction.LEFT else Direction.RIGHT
                    }
                    arrows.add(Arrow(id = id++, row = r, col = c, direction = dir, colorIndex = (r + c) % 4))
                }
            }
        }
        return ArrowLevel(levelId, title, rows, cols, arrows, difficulty, chapter)
    }
}
