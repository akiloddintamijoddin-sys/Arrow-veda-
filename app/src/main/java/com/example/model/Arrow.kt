package com.example.model

data class Arrow(
    val id: Int,
    val row: Int,
    val col: Int,
    val direction: Direction,
    val colorIndex: Int = 0
)

data class ArrowMove(
    val arrow: Arrow,
    val timestamp: Long = System.currentTimeMillis()
)
