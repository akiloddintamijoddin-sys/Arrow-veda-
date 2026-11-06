package com.example.ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ArrowThemePalette
import com.example.model.LevelDifficulty
import kotlinx.coroutines.delay

@Composable
fun VictoryDialog(
    levelId: Int,
    starsEarned: Int,
    coinsEarned: Int,
    difficulty: LevelDifficulty = LevelDifficulty.BEGINNER,
    palette: ArrowThemePalette,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(100)
        star1Scale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        delay(80)
        if (starsEarned >= 2) {
            star2Scale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        }
        delay(80)
        if (starsEarned >= 3) {
            star3Scale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, palette.boardBorder, RoundedCornerShape(28.dp))
                .testTag("victory_dialog"),
            color = palette.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(difficulty.color.copy(alpha = 0.15f))
                        .border(1.dp, difficulty.color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LEVEL $levelId • ${difficulty.label.uppercase()}",
                        color = difficulty.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (starsEarned) {
                        3 -> "Flawless Untangle!"
                        2 -> "Great Flow!"
                        else -> "Level Complete!"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Stars Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star 1
                    Icon(
                        imageVector = if (starsEarned >= 1) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star 1",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier
                            .size(46.dp)
                            .scale(if (starsEarned >= 1) star1Scale.value else 0.85f)
                    )
                    // Star 2 (Middle, slightly larger)
                    Icon(
                        imageVector = if (starsEarned >= 2) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star 2",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier
                            .size(56.dp)
                            .scale(if (starsEarned >= 2) star2Scale.value else 0.85f)
                    )
                    // Star 3
                    Icon(
                        imageVector = if (starsEarned >= 3) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star 3",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier
                            .size(46.dp)
                            .scale(if (starsEarned >= 3) star3Scale.value else 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Coins Reward Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+$coinsEarned Coins",
                        color = palette.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Next Level Button
                Button(
                    onClick = onNextLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("victory_next_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Next Level",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("victory_replay_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = palette.textPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Replay", fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("victory_home_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = palette.textPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Home", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
