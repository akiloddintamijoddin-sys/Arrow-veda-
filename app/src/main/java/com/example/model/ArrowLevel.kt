package com.example.model

import androidx.compose.ui.graphics.Color

enum class LevelDifficulty(
    val label: String,
    val color: Color
) {
    BEGINNER("Beginner", Color(0xFF10B981)), // Emerald Green
    EASY("Easy", Color(0xFF06B6D4)),         // Cyan
    MEDIUM("Medium", Color(0xFF3B82F6)),     // Blue
    HARD("Hard", Color(0xFFF59E0B)),         // Amber
    EXPERT("Expert", Color(0xFFEF4444)),     // Coral Red
    MASTER("Master", Color(0xFF8B5CF6))      // Deep Purple
}

data class ArrowLevel(
    val id: Int,
    val title: String,
    val rows: Int,
    val cols: Int,
    val arrows: List<Arrow>,
    val difficulty: LevelDifficulty = LevelDifficulty.BEGINNER,
    val chapter: String = "Beginner"
)
