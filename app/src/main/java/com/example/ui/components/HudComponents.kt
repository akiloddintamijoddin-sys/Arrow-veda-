package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowThemePalette
import com.example.model.LevelDifficulty

@Composable
fun TopGameHud(
    levelId: Int,
    levelTitle: String,
    chapter: String,
    difficulty: LevelDifficulty,
    gridSize: String,
    lives: Int,
    maxLives: Int,
    coins: Int,
    palette: ArrowThemePalette,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // First Row: Back Button, Hearts, Coins, Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(palette.surface)
                        .testTag("hud_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to home",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 3 Hearts Life System
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.surface)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("hearts_container")
                ) {
                    for (i in 1..maxLives) {
                        val isAlive = i <= lives
                        val heartScale by animateFloatAsState(
                            targetValue = if (isAlive) 1f else 0.85f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "heartScale"
                        )
                        val heartColor by animateColorAsState(
                            targetValue = if (isAlive) Color(0xFFEF4444) else palette.textSecondary.copy(alpha = 0.35f),
                            label = "heartColor"
                        )

                        Icon(
                            imageVector = if (isAlive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isAlive) "Active life" else "Lost life",
                            tint = heartColor,
                            modifier = Modifier
                                .size(22.dp)
                                .scale(heartScale)
                        )
                    }
                }

                // Coin Counter Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.surface)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("coin_indicator")
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        color = palette.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Right Buttons: Restart & Settings
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onRestartClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(palette.surface)
                            .testTag("hud_restart_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart level",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(palette.surface)
                            .testTag("hud_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Level Title Banner with Difficulty Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Level $levelId",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            letterSpacing = 0.5.sp
                        )

                        // Difficulty & Grid badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(difficulty.color.copy(alpha = 0.15f))
                                .border(1.dp, difficulty.color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${difficulty.label.uppercase()} • $gridSize",
                                color = difficulty.color,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$levelTitle • $chapter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = palette.textSecondary
                    )
                }
            }
        }
    }
}
