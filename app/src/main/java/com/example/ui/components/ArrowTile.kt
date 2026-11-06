package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.Arrow
import com.example.model.ArrowThemePalette
import com.example.model.Direction
import com.example.model.SlideAnimationState
import kotlin.math.roundToInt

@Composable
fun ArrowTile(
    arrow: Arrow,
    cellSize: Dp,
    palette: ArrowThemePalette,
    isClear: Boolean,
    isGuidanceActive: Boolean,
    isHinted: Boolean,
    isShaking: Boolean,
    slideAnimation: SlideAnimationState?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isShaking) {
        if (isShaking) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    -12f at 50 with LinearEasing
                    12f at 100 with LinearEasing
                    -8f at 150 with LinearEasing
                    8f at 200 with LinearEasing
                    -4f at 250 with LinearEasing
                    0f at 300 with LinearEasing
                }
            )
        }
    }

    val slideProgress = remember { Animatable(0f) }
    LaunchedEffect(slideAnimation) {
        if (slideAnimation != null) {
            slideProgress.snapTo(0f)
            slideProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 320, easing = LinearEasing)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val hintPulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintPulse"
    )

    val currentOffsetX = if (slideAnimation != null) {
        (slideAnimation.targetX * slideProgress.value).roundToInt()
    } else {
        shakeOffset.value.roundToInt()
    }

    val currentOffsetY = if (slideAnimation != null) {
        (slideAnimation.targetY * slideProgress.value).roundToInt()
    } else {
        0
    }

    val currentAlpha = if (slideAnimation != null) {
        (1f - slideProgress.value * 0.7f).coerceIn(0f, 1f)
    } else 1f

    val arrowBaseColor = palette.arrowColors.getOrElse(arrow.colorIndex) { palette.arrowColors.first() }
    val displayColor = when {
        isShaking -> palette.errorColor
        isHinted -> palette.clearHighlight
        isGuidanceActive && isClear -> palette.clearHighlight
        else -> arrowBaseColor
    }

    Box(
        modifier = modifier
            .size(cellSize)
            .offset { IntOffset(currentOffsetX, currentOffsetY) }
            .scale(if (isHinted) hintPulseScale else 1f)
            .padding(4.dp)
            .testTag("arrow_${arrow.row}_${arrow.col}")
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        palette.arrowBackground,
                        palette.arrowBackground.copy(alpha = 0.88f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isHinted) 2.5.dp else if (isGuidanceActive && isClear) 1.5.dp else 1.dp,
                color = when {
                    isShaking -> palette.errorColor
                    isHinted -> palette.clearHighlight
                    isGuidanceActive && isClear -> palette.clearHighlight.copy(alpha = 0.8f)
                    else -> palette.boardBorder
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = displayColor),
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        // Draw the directional arrow
        ArrowSymbol(
            direction = arrow.direction,
            color = displayColor,
            alpha = currentAlpha,
            modifier = Modifier.fillMaxSize(0.68f)
        )
    }
}

@Composable
fun ArrowSymbol(
    direction: Direction,
    color: Color,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .rotate(direction.angle)
    ) {
        val w = size.width
        val h = size.height

        // Symmetrical rounded arrow shape pointing UP
        val strokeWidth = w * 0.22f
        val headWidth = w * 0.76f
        val headHeight = h * 0.42f
        val topY = h * 0.15f
        val bottomY = h * 0.85f
        val midX = w / 2f

        // Draw shaft
        drawLine(
            color = color.copy(alpha = alpha),
            start = Offset(midX, bottomY),
            end = Offset(midX, topY + strokeWidth * 0.6f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Draw arrow head chevrons
        val headPath = Path().apply {
            moveTo(midX - headWidth / 2f, topY + headHeight)
            lineTo(midX, topY)
            lineTo(midX + headWidth / 2f, topY + headHeight)
        }

        drawPath(
            path = headPath,
            color = color.copy(alpha = alpha),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
