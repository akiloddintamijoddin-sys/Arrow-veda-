package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.model.ArrowThemePalette
import com.example.model.GameUiState

@Composable
fun ArrowGrid(
    state: GameUiState,
    palette: ArrowThemePalette,
    onArrowTapped: (com.example.model.Arrow) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = state.level.rows
    val cols = state.level.cols

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val availableSize = min(maxWidth, maxHeight)
        val boardSize = min(availableSize, 480.dp)
        val maxGridDim = maxOf(rows, cols)
        val cellSize = boardSize / maxGridDim

        // Board Container
        Box(
            modifier = Modifier
                .size(boardSize)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(palette.boardBackground)
                .border(
                    width = 2.dp,
                    color = palette.boardBorder,
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("puzzle_board")
        ) {
            // Subtle grid dot/line matrix
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stepX = size.width / cols
                val stepY = size.height / rows

                // Draw subtle grid dots at intersections
                for (r in 1 until rows) {
                    for (c in 1 until cols) {
                        drawCircle(
                            color = palette.gridLine,
                            radius = 2.2f,
                            center = Offset(c * stepX, r * stepY)
                        )
                    }
                }
            }

            // Render Active Arrows
            val allActive = state.activeArrows.values
            for (arrow in allActive) {
                val isClear = state.clearArrowIds.contains(arrow.id)
                val isHinted = state.highlightedHintArrowId == arrow.id
                val isShaking = state.shakingArrowId == arrow.id

                ArrowTile(
                    arrow = arrow,
                    cellSize = cellSize,
                    palette = palette,
                    isClear = isClear,
                    isGuidanceActive = state.guidanceActive,
                    isHinted = isHinted,
                    isShaking = isShaking,
                    slideAnimation = null,
                    onTap = { onArrowTapped(arrow) },
                    modifier = Modifier.offset(
                        x = cellSize * arrow.col,
                        y = cellSize * arrow.row
                    )
                )
            }

            // Render Animating Arrows (Sliding Off)
            for ((_, animState) in state.animatingArrows) {
                ArrowTile(
                    arrow = animState.arrow,
                    cellSize = cellSize,
                    palette = palette,
                    isClear = true,
                    isGuidanceActive = false,
                    isHinted = false,
                    isShaking = false,
                    slideAnimation = animState,
                    onTap = {},
                    modifier = Modifier.offset(
                        x = cellSize * animState.arrow.col,
                        y = cellSize * animState.arrow.row
                    )
                )
            }
        }
    }
}
