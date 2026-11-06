package com.example.model

data class GridPoint(val r: Int, val c: Int) {
    fun step(dir: Direction): GridPoint = GridPoint(r + dir.dRow, c + dir.dCol)
}

data class PathArrow(
    val id: Int,
    val points: List<GridPoint>,
    val direction: Direction,
    val colorIndex: Int = 0
) {
    val head: GridPoint get() = points.last()
    val tail: GridPoint get() = points.first()

    fun occupies(point: GridPoint): Boolean = points.contains(point)
    fun occupies(r: Int, c: Int): Boolean = points.any { it.r == r && it.c == c }
}

data class PathSlideAnim(
    val arrow: PathArrow,
    val targetOffsetX: Float,
    val targetOffsetY: Float,
    val startTime: Long = System.currentTimeMillis(),
    val durationMs: Long = 520L
)
