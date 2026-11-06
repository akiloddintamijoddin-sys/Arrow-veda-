package com.example.model

enum class Direction(val dRow: Int, val dCol: Int, val angle: Float) {
    UP(-1, 0, 0f),
    RIGHT(0, 1, 90f),
    DOWN(1, 0, 180f),
    LEFT(0, -1, 270f);

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}
