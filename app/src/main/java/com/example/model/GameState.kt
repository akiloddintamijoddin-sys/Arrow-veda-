package com.example.model

import com.example.data.LabyrinthGenerator
import com.example.data.LabyrinthLevel

data class SlideAnimationState(
    val arrow: Arrow,
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    val startTime: Long = System.currentTimeMillis()
)

data class GameUiState(
    val level: ArrowLevel,
    val labyrinthLevel: LabyrinthLevel = LabyrinthGenerator.generateLevel(level.id),
    val activePathArrows: List<PathArrow> = labyrinthLevel.arrows,
    val animatingPathArrows: List<PathSlideAnim> = emptyList(),
    val activeArrows: Map<Pair<Int, Int>, Arrow> = emptyMap(),
    val animatingArrows: Map<Int, SlideAnimationState> = emptyMap(),
    val shakingArrowId: Int? = null,
    val blockedPathArrowId: Int? = null,
    val highlightedHintArrowId: Int? = null,
    val guidanceActive: Boolean = false,
    val clearArrowIds: Set<Int> = emptySet(),
    val lives: Int = 4,
    val maxLives: Int = 4,
    val mistakes: Int = 0,
    val movesCount: Int = 0,
    val isCompleted: Boolean = false,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val starsEarned: Int = 0,
    val coinsEarned: Int = 0,
    val canUndo: Boolean = false
)
