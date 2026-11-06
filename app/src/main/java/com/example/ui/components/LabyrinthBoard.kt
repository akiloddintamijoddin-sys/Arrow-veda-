package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.data.LabyrinthLevel
import com.example.model.ArrowThemePalette
import com.example.model.Direction
import com.example.model.PathArrow
import com.example.model.PathSlideAnim
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun LabyrinthBoard(
    level: LabyrinthLevel,
    activeArrows: List<PathArrow>,
    animatingArrows: List<PathSlideAnim>,
    shakingArrowId: Int?,
    hintedArrowId: Int?,
    blockedArrowId: Int?,
    guidanceActive: Boolean,
    clearArrowIds: Set<Int>,
    palette: ArrowThemePalette,
    onArrowTapped: (PathArrow) -> Unit,
    modifier: Modifier = Modifier
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(shakingArrowId) {
        if (shakingArrowId != null) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    -14f at 50 with LinearEasing
                    14f at 100 with LinearEasing
                    -10f at 150 with LinearEasing
                    10f at 200 with LinearEasing
                    -5f at 250 with LinearEasing
                    0f at 300 with LinearEasing
                }
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        val availableSize = min(maxWidth, maxHeight)
        val boardSize = min(availableSize, 560.dp)

        val rows = level.rows
        val cols = level.cols

        Box(
            modifier = Modifier
                .size(boardSize)
                .testTag("labyrinth_board")
                .pointerInput(activeArrows) {
                    detectTapGestures { tapOffset ->
                        val tapped = findTappedArrow(
                            tapOffset = tapOffset,
                            arrows = activeArrows,
                            cellW = size.width.toFloat() / cols,
                            cellH = size.height.toFloat() / rows
                        )
                        if (tapped != null) {
                            onArrowTapped(tapped)
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cw = size.width / cols
                val ch = size.height / rows
                val strokeW = (cw * 0.17f).coerceIn(2.8.dp.toPx(), 4.8.dp.toPx())

                // 1. Draw Active Winding Snake Arrows
                for (arrow in activeArrows) {
                    val isBlocked = blockedArrowId == arrow.id || shakingArrowId == arrow.id
                    val isHinted = hintedArrowId == arrow.id
                    val isClearInGuidance = guidanceActive && clearArrowIds.contains(arrow.id)

                    val color = when {
                        isBlocked -> Color(0xFFEF4444) // Vivid Red as seen in screenshot!
                        isHinted -> Color(0xFF00A3FF)  // Electric Cyan Blue as seen in screenshot!
                        isClearInGuidance -> Color(0xFF00B0FF).copy(alpha = 0.9f)
                        else -> Color(0xFF433428)      // Espresso dark brown from screenshot!
                    }

                    val dx = if (shakingArrowId == arrow.id) shakeOffset.value else 0f

                    drawStaticPathArrow(
                        arrow = arrow,
                        cellW = cw,
                        cellH = ch,
                        strokeWidth = strokeW,
                        color = color,
                        offsetX = dx,
                        offsetY = 0f,
                        alpha = 1f
                    )
                }

                // 2. Draw Snake Slithering Out Animation ("Sap ke jaise chalte hue bahar nikalte")
                val now = System.currentTimeMillis()
                for (anim in animatingArrows) {
                    drawSlitheringSnake(
                        anim = anim,
                        cellW = cw,
                        cellH = ch,
                        strokeWidth = strokeW,
                        now = now
                    )
                }
            }
        }
    }
}

/**
 * Draws a static winding arrow with an unmistakably clear, prominent arrowhead ("nok")
 * and distinct tail terminal donut so direction is immediately obvious.
 */
private fun DrawScope.drawStaticPathArrow(
    arrow: PathArrow,
    cellW: Float,
    cellH: Float,
    strokeWidth: Float,
    color: Color,
    offsetX: Float,
    offsetY: Float,
    alpha: Float
) {
    if (arrow.points.isEmpty()) return

    val displayColor = color.copy(alpha = alpha)
    val minCell = minOf(cellW, cellH)

    val pts = arrow.points.map { pt ->
        Offset(pt.c * cellW + cellW / 2f + offsetX, pt.r * cellH + cellH / 2f + offsetY)
    }

    val tailPt = pts.first()
    val headPt = pts.last()

    // Arrowhead dimensions: generous, bold, and distinct
    val arrowLen = (minCell * 0.42f).coerceIn(12.dp.toPx(), 24.dp.toPx())
    val arrowHalfWidth = (minCell * 0.28f).coerceIn(8.dp.toPx(), 16.dp.toPx())
    val notchDepth = arrowLen * 0.24f

    // Tip extends forward from head cell center towards boundary
    val tipX = headPt.x + arrow.direction.dCol * (minCell * 0.44f)
    val tipY = headPt.y + arrow.direction.dRow * (minCell * 0.44f)

    val baseCenterX = tipX - arrow.direction.dCol * arrowLen
    val baseCenterY = tipY - arrow.direction.dRow * arrowLen
    val notchX = baseCenterX + arrow.direction.dCol * notchDepth
    val notchY = baseCenterY + arrow.direction.dRow * notchDepth

    // Draw the continuous body polyline straight into the arrowhead notch
    val bodyPath = Path()
    bodyPath.moveTo(tailPt.x, tailPt.y)
    for (i in 1 until pts.size) {
        bodyPath.lineTo(pts[i].x, pts[i].y)
    }
    bodyPath.lineTo(notchX, notchY)

    drawPath(
        path = bodyPath,
        color = displayColor,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Tail start indicator: distinct donut ring (identifies the tail as start, not tip)
    drawCircle(
        color = displayColor,
        radius = strokeWidth * 1.1f,
        center = tailPt
    )
    drawCircle(
        color = Color(0xFFF9EED9), // Board background cutout
        radius = strokeWidth * 0.45f,
        center = tailPt
    )

    // Draw prominent, razor-sharp arrowhead ("Nok") pointing in arrow.direction
    drawArrowHead(
        tipX = tipX,
        tipY = tipY,
        baseCenterX = baseCenterX,
        baseCenterY = baseCenterY,
        notchX = notchX,
        notchY = notchY,
        direction = arrow.direction,
        arrowHalfWidth = arrowHalfWidth,
        strokeWidth = strokeWidth,
        color = displayColor,
        alpha = alpha
    )
}

/**
 * Draws the arrow slithering like a real snake along its own bends and exit ray out of the board.
 */
private fun DrawScope.drawSlitheringSnake(
    anim: PathSlideAnim,
    cellW: Float,
    cellH: Float,
    strokeWidth: Float,
    now: Long
) {
    val arrow = anim.arrow
    if (arrow.points.isEmpty()) return

    val elapsed = (now - anim.startTime).coerceAtLeast(0)
    val totalDuration = anim.durationMs.toFloat()
    val rawProgress = (elapsed / totalDuration).coerceIn(0f, 1f)

    if (rawProgress >= 1f) return // Completed and off the board

    // Smooth accelerating ease curve (slow start slither, fast exit)
    val progress = rawProgress * rawProgress * (3f - 2f * rawProgress)

    // Construct the full trajectory: points from tail to head, then exit ray extending far beyond board
    val pts = arrow.points.map { pt ->
        Offset(pt.c * cellW + cellW / 2f, pt.r * cellH + cellH / 2f)
    }

    val headPt = pts.last()
    val exitDistance = maxOf(size.width, size.height) * 1.6f
    val exitPt = Offset(
        headPt.x + arrow.direction.dCol * exitDistance,
        headPt.y + arrow.direction.dRow * exitDistance
    )

    val trajectory = pts + exitPt

    // Calculate cumulative arc-length distances along trajectory
    val cumDist = FloatArray(trajectory.size)
    cumDist[0] = 0f
    for (i in 1 until trajectory.size) {
        val dx = trajectory[i].x - trajectory[i - 1].x
        val dy = trajectory[i].y - trajectory[i - 1].y
        cumDist[i] = cumDist[i - 1] + sqrt(dx * dx + dy * dy)
    }

    val bodyLength = cumDist[pts.size - 1]
    val totalDist = cumDist.last()

    // Slide interval:
    // progress = 0: tail at 0, head at bodyLength
    // progress = 1: tail at totalDist, head at totalDist + bodyLength (completely off screen)
    val tailDist = progress * totalDist
    val headDist = (tailDist + bodyLength).coerceAtMost(totalDist)

    if (tailDist >= totalDist) return

    // Sample points along trajectory between tailDist and headDist
    fun pointAtDistance(dist: Float): Pair<Offset, Direction> {
        val clamped = dist.coerceIn(0f, totalDist)
        for (i in 0 until trajectory.size - 1) {
            if (clamped <= cumDist[i + 1]) {
                val segLen = cumDist[i + 1] - cumDist[i]
                val frac = if (segLen > 0f) (clamped - cumDist[i]) / segLen else 0f
                val p1 = trajectory[i]
                val p2 = trajectory[i + 1]
                val pt = Offset(p1.x + (p2.x - p1.x) * frac, p1.y + (p2.y - p1.y) * frac)

                val dir = when {
                    p2.x - p1.x > 1f -> Direction.RIGHT
                    p2.x - p1.x < -1f -> Direction.LEFT
                    p2.y - p1.y > 1f -> Direction.DOWN
                    else -> Direction.UP
                }
                return Pair(pt, dir)
            }
        }
        return Pair(trajectory.last(), arrow.direction)
    }

    val snakePoints = mutableListOf<Offset>()
    val (tailOffset, _) = pointAtDistance(tailDist)
    snakePoints.add(tailOffset)

    for (i in 0 until trajectory.size) {
        if (cumDist[i] > tailDist && cumDist[i] < headDist) {
            snakePoints.add(trajectory[i])
        }
    }

    val (headOffset, headDir) = pointAtDistance(headDist)
    snakePoints.add(headOffset)

    if (snakePoints.size < 2) return

    val snakeColor = Color(0xFF00A3FF)     // Electric Cyan Blue
    val snakeGlow = Color(0xFF38BDF8)      // Bright Sky Blue Glow

    val snakePath = Path()
    snakePath.moveTo(snakePoints.first().x, snakePoints.first().y)
    for (i in 1 until snakePoints.size) {
        snakePath.lineTo(snakePoints[i].x, snakePoints[i].y)
    }

    // Outer glow for glowing snake trail
    drawPath(
        path = snakePath,
        color = snakeGlow.copy(alpha = 0.40f),
        style = Stroke(
            width = strokeWidth * 2.0f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Solid snake body
    drawPath(
        path = snakePath,
        color = snakeColor,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Tail start indicator donut
    drawCircle(
        color = snakeColor,
        radius = strokeWidth * 1.1f,
        center = snakePoints.first()
    )
    drawCircle(
        color = Color(0xFFF9EED9),
        radius = strokeWidth * 0.45f,
        center = snakePoints.first()
    )

    // Draw snake arrowhead (nok) at the leading head point
    val minCell = minOf(cellW, cellH)
    val arrowLen = (minCell * 0.42f).coerceIn(12.dp.toPx(), 24.dp.toPx())
    val arrowHalfWidth = (minCell * 0.28f).coerceIn(8.dp.toPx(), 16.dp.toPx())
    val notchDepth = arrowLen * 0.24f

    val tipX = headOffset.x
    val tipY = headOffset.y
    val baseCenterX = tipX - headDir.dCol * arrowLen
    val baseCenterY = tipY - headDir.dRow * arrowLen
    val notchX = baseCenterX + headDir.dCol * notchDepth
    val notchY = baseCenterY + headDir.dRow * notchDepth

    drawArrowHead(
        tipX = tipX,
        tipY = tipY,
        baseCenterX = baseCenterX,
        baseCenterY = baseCenterY,
        notchX = notchX,
        notchY = notchY,
        direction = headDir,
        arrowHalfWidth = arrowHalfWidth,
        strokeWidth = strokeWidth,
        color = Color(0xFF00E5FF),
        alpha = 1f
    )
}

/**
 * Draws a sharp, crisp, unmistakably directional arrowhead ("nok").
 */
private fun DrawScope.drawArrowHead(
    tipX: Float,
    tipY: Float,
    baseCenterX: Float,
    baseCenterY: Float,
    notchX: Float,
    notchY: Float,
    direction: Direction,
    arrowHalfWidth: Float,
    strokeWidth: Float,
    color: Color,
    alpha: Float
) {
    val perpCol = -direction.dRow
    val perpRow = direction.dCol

    val wing1X = baseCenterX + perpCol * arrowHalfWidth
    val wing1Y = baseCenterY + perpRow * arrowHalfWidth

    val wing2X = baseCenterX - perpCol * arrowHalfWidth
    val wing2Y = baseCenterY - perpRow * arrowHalfWidth

    val arrowHeadPath = Path().apply {
        moveTo(tipX, tipY)
        lineTo(wing1X, wing1Y)
        lineTo(notchX, notchY)
        lineTo(wing2X, wing2Y)
        close()
    }

    val displayColor = color.copy(alpha = alpha)

    // 1. Fill solid arrowhead body
    drawPath(path = arrowHeadPath, color = displayColor, style = Fill)

    // 2. Stroke with razor-sharp miter join for distinct, crisp point
    drawPath(
        path = arrowHeadPath,
        color = displayColor,
        style = Stroke(
            width = (strokeWidth * 0.45f).coerceAtLeast(1.8.dp.toPx()),
            cap = StrokeCap.Round,
            join = StrokeJoin.Miter,
            miter = 4f
        )
    )

    // 3. Bright high-contrast chevron barb along wing1 -> tip -> wing2
    // This unmistakably highlights where the arrow is pointing ("nok ka muh")
    val leadingEdgePath = Path().apply {
        moveTo(wing1X, wing1Y)
        lineTo(tipX, tipY)
        lineTo(wing2X, wing2Y)
    }
    drawPath(
        path = leadingEdgePath,
        color = Color.White.copy(alpha = 0.85f * alpha),
        style = Stroke(
            width = 2.2.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Miter,
            miter = 4f
        )
    )

    // 4. Center forward spine indicator (notch to tip)
    drawLine(
        color = Color.White.copy(alpha = 0.55f * alpha),
        start = Offset(notchX, notchY),
        end = Offset(tipX, tipY),
        strokeWidth = 1.4.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Finds the arrow closest to the player's tap coordinate.
 */
private fun findTappedArrow(
    tapOffset: Offset,
    arrows: List<PathArrow>,
    cellW: Float,
    cellH: Float
): PathArrow? {
    val hitThreshold = maxOf(cellW, cellH) * 0.95f

    var closestArrow: PathArrow? = null
    var closestDist = Float.MAX_VALUE

    for (arrow in arrows) {
        for (i in 0 until arrow.points.size - 1) {
            val p1 = arrow.points[i]
            val p2 = arrow.points[i + 1]
            val x1 = p1.c * cellW + cellW / 2f
            val y1 = p1.r * cellH + cellH / 2f
            val x2 = p2.c * cellW + cellW / 2f
            val y2 = p2.r * cellH + cellH / 2f

            val dist = distanceToSegment(tapOffset.x, tapOffset.y, x1, y1, x2, y2)
            if (dist < closestDist && dist <= hitThreshold) {
                closestDist = dist
                closestArrow = arrow
            }
        }

        val head = arrow.head
        val hx = head.c * cellW + cellW / 2f
        val hy = head.r * cellH + cellH / 2f
        val distHead = distance(tapOffset.x, tapOffset.y, hx, hy)
        if (distHead < closestDist && distHead <= hitThreshold) {
            closestDist = distHead
            closestArrow = arrow
        }
    }

    return closestArrow
}

private fun distanceToSegment(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val dx = x2 - x1
    val dy = y2 - y1
    val l2 = dx * dx + dy * dy
    if (l2 == 0f) return distance(px, py, x1, y1)

    val t = ((px - x1) * dx + (py - y1) * dy) / l2
    val clampedT = t.coerceIn(0f, 1f)
    val projX = x1 + clampedT * dx
    val projY = y1 + clampedT * dy
    return distance(px, py, projX, projY)
}

private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val dx = x1 - x2
    val dy = y1 - y2
    return sqrt(dx * dx + dy * dy)
}
