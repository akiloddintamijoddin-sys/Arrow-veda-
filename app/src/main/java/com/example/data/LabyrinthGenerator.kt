package com.example.data

import com.example.model.Direction
import com.example.model.GridPoint
import com.example.model.LevelDifficulty
import com.example.model.PathArrow
import com.example.model.ShapeLibrary
import com.example.model.ShapeMask
import kotlin.random.Random

data class LabyrinthLevel(
    val id: Int,
    val title: String,
    val rows: Int,
    val cols: Int,
    val arrows: List<PathArrow>,
    val shapeMask: ShapeMask,
    val difficulty: LevelDifficulty
)

object LabyrinthGenerator {

    /**
     * Checks if the ray from [head] along [dir] is completely free of any other active arrow cells.
     */
    fun isRayClear(
        head: GridPoint,
        dir: Direction,
        rows: Int,
        cols: Int,
        activeArrows: List<PathArrow>,
        currentArrowId: Int
    ): Boolean {
        var curr = head.step(dir)
        while (curr.r in 0 until rows && curr.c in 0 until cols) {
            for (other in activeArrows) {
                if (other.id != currentArrowId && other.occupies(curr)) {
                    return false
                }
            }
            curr = curr.step(dir)
        }
        return true
    }

    /**
     * Finds the arrow that blocks this [arrow]'s ray, if any.
     */
    fun findBlockingArrow(
        arrow: PathArrow,
        rows: Int,
        cols: Int,
        activeArrows: List<PathArrow>
    ): PathArrow? {
        var curr = arrow.head.step(arrow.direction)
        while (curr.r in 0 until rows && curr.c in 0 until cols) {
            for (other in activeArrows) {
                if (other.id != arrow.id && other.occupies(curr)) {
                    return other
                }
            }
            curr = curr.step(arrow.direction)
        }
        return null
    }

    /**
     * Solvability simulation verifier (Fast zero-allocation version).
     * Simulates play to ensure the puzzle can be 100% untangled from the initial state to 0 remaining arrows.
     */
    fun isSolvable(rows: Int, cols: Int, arrows: List<PathArrow>): Boolean {
        if (arrows.isEmpty()) return false
        val grid = IntArray(rows * cols) { -1 }
        for (i in arrows.indices) {
            val arrow = arrows[i]
            for (pt in arrow.points) {
                if (pt.r in 0 until rows && pt.c in 0 until cols) {
                    grid[pt.r * cols + pt.c] = i
                }
            }
        }

        val active = BooleanArray(arrows.size) { true }
        var remainingCount = arrows.size

        while (remainingCount > 0) {
            var foundClear = false
            for (i in arrows.indices) {
                if (!active[i]) continue
                val arrow = arrows[i]
                var r = arrow.head.r + arrow.direction.dRow
                var c = arrow.head.c + arrow.direction.dCol
                var blocked = false
                while (r in 0 until rows && c in 0 until cols) {
                    val occupant = grid[r * cols + c]
                    if (occupant != -1 && active[occupant]) {
                        blocked = true
                        break
                    }
                    r += arrow.direction.dRow
                    c += arrow.direction.dCol
                }
                if (!blocked) {
                    active[i] = false
                    remainingCount--
                    foundClear = true
                    break
                }
            }
            if (!foundClear) {
                return false // Circular deadlock!
            }
        }
        return true
    }

    /**
     * Finds all currently unblocked arrows in the given state.
     */
    fun getClearArrows(rows: Int, cols: Int, activeArrows: List<PathArrow>): List<PathArrow> {
        return activeArrows.filter { arrow ->
            isRayClear(arrow.head, arrow.direction, rows, cols, activeArrows, arrow.id)
        }
    }

    /**
     * Generates a dense, intricate, guaranteed-solvable labyrinth puzzle for ANY level from 1 to 10000+.
     * Ensures an advanced difficulty where 95%+ of arrows are blocked, leaving only 1 or 2 starter arrows.
     */
    fun generateLevel(levelId: Int): LabyrinthLevel {
        val shape = ShapeLibrary.getShapeForLevel(levelId)
        val seed = levelId.toLong() * 2654435761L + 104729L
        val random = Random(seed)

        val difficulty = when {
            levelId <= 25 -> LevelDifficulty.BEGINNER
            levelId <= 100 -> LevelDifficulty.EASY
            levelId <= 500 -> LevelDifficulty.MEDIUM
            levelId <= 2000 -> LevelDifficulty.HARD
            levelId <= 5000 -> LevelDifficulty.EXPERT
            else -> LevelDifficulty.MASTER
        }

        val placedArrows = mutableListOf<PathArrow>()
        val occupiedCells = mutableSetOf<GridPoint>()
        var arrowIdCounter = 1

        val starterLimit = if (difficulty == LevelDifficulty.BEGINNER && levelId <= 5) 2 else 1

        // Phase 1: Place 1 or 2 starter roots at the outer boundary pointing freely outward
        val boundaryCandidates = shape.activeCells.filter { cell ->
            Direction.entries.any { dir ->
                val neck = cell.step(dir.opposite())
                shape.activeCells.contains(neck) && isRayClearToBoundary(cell, dir, shape.rows, shape.cols, occupiedCells)
            }
        }.shuffled(random)

        for (head in boundaryCandidates) {
            if (placedArrows.size >= starterLimit) break
            if (occupiedCells.contains(head)) continue

            val validDirs = Direction.entries.filter { dir ->
                val neck = head.step(dir.opposite())
                shape.activeCells.contains(neck) &&
                    !occupiedCells.contains(neck) &&
                    isRayClearToBoundary(head, dir, shape.rows, shape.cols, occupiedCells)
            }
            if (validDirs.isEmpty()) continue

            val chosenDir = validDirs.random(random)
            val path = mutableListOf(head.step(chosenDir.opposite()), head)

            val arrow = PathArrow(
                id = arrowIdCounter++,
                points = path,
                direction = chosenDir,
                colorIndex = (arrowIdCounter - 1) % 4
            )
            placedArrows.add(arrow)
            occupiedCells.addAll(path)
        }

        // Phase 2: Grow dense causal DAG
        // Every subsequent arrow MUST point into an already-placed arrow so it starts blocked!
        val passLengths = listOf(
            3..5,
            2..4,
            2..3,
            2..2
        )

        for (lengthRange in passLengths) {
            var progress = true
            var passAttempts = 0
            while (progress && passAttempts < 500) {
                passAttempts++
                progress = false

                val freeCells = shape.activeCells.filterNot { occupiedCells.contains(it) }.shuffled(random)
                for (headCandidate in freeCells) {
                    if (occupiedCells.contains(headCandidate)) continue

                    // Find directions that point directly into an already-placed arrow
                    val dirsPointingAtExisting = Direction.entries.filter { dir ->
                        val neck = headCandidate.step(dir.opposite())
                        shape.activeCells.contains(neck) &&
                            !occupiedCells.contains(neck) &&
                            rayHitsOccupiedCell(headCandidate, dir, shape.rows, shape.cols, occupiedCells)
                    }.shuffled(random)

                    for (chosenDir in dirsPointingAtExisting) {
                        val neckCell = headCandidate.step(chosenDir.opposite())
                        val targetLen = random.nextInt(lengthRange.first, lengthRange.last + 1)
                        val path = mutableListOf(neckCell, headCandidate)

                        var currentTail = neckCell
                        var lastBackwardDir = chosenDir.opposite()

                        for (step in 2 until targetLen) {
                            val nextDirs = Direction.entries.filter { it != lastBackwardDir.opposite() }.shuffled(random)
                            var extended: GridPoint? = null
                            for (bDir in nextDirs) {
                                val cand = currentTail.step(bDir)
                                if (shape.activeCells.contains(cand) &&
                                    !occupiedCells.contains(cand) &&
                                    !path.contains(cand) &&
                                    !isInExitRay(headCandidate, chosenDir, cand, shape.rows, shape.cols)
                                ) {
                                    extended = cand
                                    lastBackwardDir = bDir
                                    break
                                }
                            }
                            if (extended != null) {
                                path.add(0, extended)
                                currentTail = extended
                            } else {
                                break
                            }
                        }

                        if (path.size >= 2) {
                            val candidateArrow = PathArrow(
                                id = arrowIdCounter,
                                points = path,
                                direction = chosenDir,
                                colorIndex = arrowIdCounter % 4
                            )

                            // Validate solvability in fast simulation
                            val testList = ArrayList<PathArrow>(placedArrows.size + 1)
                            testList.addAll(placedArrows)
                            testList.add(candidateArrow)

                            if (isSolvable(shape.rows, shape.cols, testList)) {
                                placedArrows.add(candidateArrow)
                                occupiedCells.addAll(path)
                                arrowIdCounter++
                                progress = true
                                break
                            }
                        }
                    }
                }
            }
        }

        // Phase 3: Final sweep for remaining isolated pairs to maximize visual density
        val remaining = shape.activeCells.filterNot { occupiedCells.contains(it) }.shuffled(random)
        for (cell in remaining) {
            if (occupiedCells.contains(cell)) continue
            for (dir in Direction.entries.shuffled(random)) {
                // Try cell as head
                val tail = cell.step(dir.opposite())
                if (shape.activeCells.contains(tail) && !occupiedCells.contains(tail)) {
                    val path = listOf(tail, cell)
                    val candidate = PathArrow(arrowIdCounter, path, dir, arrowIdCounter % 4)
                    val testList = ArrayList<PathArrow>(placedArrows.size + 1)
                    testList.addAll(placedArrows)
                    testList.add(candidate)

                    if (isSolvable(shape.rows, shape.cols, testList)) {
                        val clearCount = getClearArrows(shape.rows, shape.cols, testList).size
                        if (clearCount <= starterLimit + 1) {
                            placedArrows.add(candidate)
                            occupiedCells.addAll(path)
                            arrowIdCounter++
                            break
                        }
                    }
                }

                // Try cell as tail
                val head = cell.step(dir)
                if (shape.activeCells.contains(head) && !occupiedCells.contains(head)) {
                    val path = listOf(cell, head)
                    val candidate = PathArrow(arrowIdCounter, path, dir, arrowIdCounter % 4)
                    val testList = ArrayList<PathArrow>(placedArrows.size + 1)
                    testList.addAll(placedArrows)
                    testList.add(candidate)

                    if (isSolvable(shape.rows, shape.cols, testList)) {
                        val clearCount = getClearArrows(shape.rows, shape.cols, testList).size
                        if (clearCount <= starterLimit + 1) {
                            placedArrows.add(candidate)
                            occupiedCells.addAll(path)
                            arrowIdCounter++
                            break
                        }
                    }
                }
            }
        }

        // Guarantee final solvability check
        val finalArrows = if (isSolvable(shape.rows, shape.cols, placedArrows)) {
            placedArrows
        } else {
            breakCyclesToEnsureSolvability(shape.rows, shape.cols, placedArrows)
        }

        return LabyrinthLevel(
            id = levelId,
            title = shape.type.displayName,
            rows = shape.rows,
            cols = shape.cols,
            arrows = finalArrows,
            shapeMask = shape,
            difficulty = difficulty
        )
    }

    private fun isRayClearToBoundary(
        head: GridPoint,
        dir: Direction,
        rows: Int,
        cols: Int,
        occupiedCells: Set<GridPoint>
    ): Boolean {
        var r = head.r + dir.dRow
        var c = head.c + dir.dCol
        while (r in 0 until rows && c in 0 until cols) {
            if (occupiedCells.contains(GridPoint(r, c))) {
                return false
            }
            r += dir.dRow
            c += dir.dCol
        }
        return true
    }

    private fun rayHitsOccupiedCell(
        head: GridPoint,
        dir: Direction,
        rows: Int,
        cols: Int,
        occupiedCells: Set<GridPoint>
    ): Boolean {
        var r = head.r + dir.dRow
        var c = head.c + dir.dCol
        while (r in 0 until rows && c in 0 until cols) {
            if (occupiedCells.contains(GridPoint(r, c))) {
                return true
            }
            r += dir.dRow
            c += dir.dCol
        }
        return false
    }

    /**
     * Finds the exact arrows stuck in a circular deadlock and removes only one arrow per cycle.
     */
    private fun breakCyclesToEnsureSolvability(
        rows: Int,
        cols: Int,
        arrows: List<PathArrow>
    ): List<PathArrow> {
        val list = arrows.toMutableList()
        var safetyCounter = 0
        while (!isSolvable(rows, cols, list) && list.size > 15 && safetyCounter < 50) {
            safetyCounter++
            val simRemaining = list.toMutableList()
            while (simRemaining.isNotEmpty()) {
                val clear = simRemaining.firstOrNull { arrow ->
                    isRayClear(arrow.head, arrow.direction, rows, cols, simRemaining, arrow.id)
                } ?: break
                simRemaining.remove(clear)
            }
            if (simRemaining.isNotEmpty()) {
                list.remove(simRemaining.first())
            } else {
                break
            }
        }
        return list
    }

    private fun isInExitRay(
        head: GridPoint,
        dir: Direction,
        point: GridPoint,
        rows: Int,
        cols: Int
    ): Boolean {
        var r = head.r + dir.dRow
        var c = head.c + dir.dCol
        while (r in 0 until rows && c in 0 until cols) {
            if (r == point.r && c == point.c) return true
            r += dir.dRow
            c += dir.dCol
        }
        return false
    }
}
