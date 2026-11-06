package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowThemePalette

@Composable
fun BottomActionBar(
    guidanceActive: Boolean,
    hintsCount: Int,
    canUndo: Boolean,
    palette: ArrowThemePalette,
    onGuidanceClick: () -> Unit,
    onHintClick: () -> Unit,
    onUndoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Guidance Toggle Button
            ActionToolButton(
                icon = if (guidanceActive) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                label = "Guidance",
                isActive = guidanceActive,
                palette = palette,
                onClick = onGuidanceClick,
                badgeText = if (guidanceActive) "ON" else null,
                testTag = "guidance_button"
            )

            // Hint Button
            ActionToolButton(
                icon = Icons.Default.Lightbulb,
                label = "Hint",
                isActive = false,
                palette = palette,
                onClick = onHintClick,
                badgeText = if (hintsCount > 0) "$hintsCount" else "20 🪙",
                testTag = "hint_button"
            )

            // Undo Button
            ActionToolButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "Undo",
                isActive = false,
                isEnabled = canUndo,
                palette = palette,
                onClick = onUndoClick,
                testTag = "undo_button"
            )
        }
    }
}

@Composable
fun ActionToolButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    palette: ArrowThemePalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    badgeText: String? = null,
    testTag: String = "action_button"
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !isEnabled -> palette.surface.copy(alpha = 0.5f)
            isActive -> palette.clearHighlight.copy(alpha = 0.2f)
            else -> palette.surface
        },
        label = "bgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !isEnabled -> palette.textSecondary.copy(alpha = 0.4f)
            isActive -> palette.clearHighlight
            else -> palette.textPrimary
        },
        label = "contentColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .border(
                    width = if (isActive) 1.5.dp else 1.dp,
                    color = if (isActive) palette.clearHighlight else palette.boardBorder,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(enabled = isEnabled, onClick = onClick)
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )

            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.accent)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isEnabled) palette.textSecondary else palette.textSecondary.copy(alpha = 0.4f)
        )
    }
}
