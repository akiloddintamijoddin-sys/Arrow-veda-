package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.model.ArrowThemePalette
import com.example.model.Direction
import com.example.model.GameThemeType
import com.example.ui.components.ArrowSymbol
import com.example.ui.dialogs.HowToPlayDialog
import com.example.ui.dialogs.SettingsDialog

@Composable
fun HomeScreen(
    currentLevel: Int,
    unlockedLevel: Int,
    totalStars: Int,
    coins: Int,
    hints: Int,
    palette: ArrowThemePalette,
    currentTheme: GameThemeType,
    soundEnabled: Boolean,
    musicEnabled: Boolean = true,
    hapticsEnabled: Boolean,
    guidanceActive: Boolean,
    onPlayClick: () -> Unit,
    onDailyChallengeClick: () -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onMusicToggle: (Boolean) -> Unit = {},
    onHapticsToggle: (Boolean) -> Unit,
    onGuidanceToggle: (Boolean) -> Unit,
    onThemeSelect: (GameThemeType) -> Unit,
    onResetProgress: () -> Unit,
    onTestSound: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showSettings by remember { mutableStateOf(false) }
    var showHowToPlay by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingY"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = palette.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Calm background geometric accents
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                drawCircle(
                    color = palette.accent.copy(alpha = 0.04f),
                    radius = w * 0.45f,
                    center = Offset(w * 0.15f, h * 0.2f)
                )
                drawCircle(
                    color = palette.clearHighlight.copy(alpha = 0.035f),
                    radius = w * 0.5f,
                    center = Offset(w * 0.85f, h * 0.75f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .widthIn(max = 540.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Utilities Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Coin pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(palette.surface)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$coins",
                            color = palette.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Settings & Help Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { showHowToPlay = true },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(palette.surface)
                                .testTag("home_help_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "How to play",
                                tint = palette.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { showSettings = true },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(palette.surface)
                                .testTag("home_settings_button")
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

                Spacer(modifier = Modifier.height(20.dp))

                // App Hero Icon & Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = floatOffset.dp)
                ) {
                    // Official ArrowVeda Lotus Logo Squircle
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = Color(0x33C08A3E))
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFFFFF9EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrowveda_icon_fg),
                            contentDescription = "ArrowVeda Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Brand Title: ArrowVeda
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Arrow",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2B3441),
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Veda",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF986B49),
                            letterSpacing = (-0.5).sp
                        )
                    }

                    Text(
                        text = "Untangle your mind",
                        fontSize = 14.sp,
                        color = Color(0xFF5D4A39),
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Stats Summary Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.boardBorder, RoundedCornerShape(22.dp))
                        .padding(vertical = 14.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatPillItem(
                        icon = Icons.Default.PlayArrow,
                        value = "Lvl $currentLevel",
                        label = "of 10,000",
                        palette = palette
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(palette.boardBorder)
                    )
                    StatPillItem(
                        icon = Icons.Default.Star,
                        value = "$totalStars",
                        label = "Stars",
                        palette = palette,
                        iconTint = Color(0xFFFBBF24)
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(palette.boardBorder)
                    )
                    StatPillItem(
                        icon = Icons.Default.Lightbulb,
                        value = "$hints",
                        label = "Hints",
                        palette = palette,
                        iconTint = palette.clearHighlight
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Main Menu Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Play Primary Button
                    Button(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp))
                            .testTag("home_play_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.accent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        val levelInfo = remember(currentLevel) { com.example.data.LevelRepository.getLevel(currentLevel) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (currentLevel == 1) "Play Level 1 • ${levelInfo.difficulty.label}" else "Continue Level $currentLevel • ${levelInfo.difficulty.label}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Daily Zen Challenge
                    OutlinedButton(
                        onClick = onDailyChallengeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("home_daily_challenge_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = palette.textPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, palette.boardBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = palette.clearHighlight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Daily Zen Challenge",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Subtle footer
                Text(
                    text = "10,000 Levels • 100% Offline • Calming ASMR",
                    fontSize = 11.sp,
                    color = palette.textSecondary.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (showSettings) {
            SettingsDialog(
                currentTheme = currentTheme,
                soundEnabled = soundEnabled,
                musicEnabled = musicEnabled,
                hapticsEnabled = hapticsEnabled,
                guidanceDefault = guidanceActive,
                palette = palette,
                onSoundToggle = onSoundToggle,
                onMusicToggle = onMusicToggle,
                onHapticsToggle = onHapticsToggle,
                onGuidanceToggle = onGuidanceToggle,
                onThemeSelect = onThemeSelect,
                onResetProgress = {
                    onResetProgress()
                    showSettings = false
                },
                onDismiss = { showSettings = false },
                onTestSound = onTestSound
            )
        }

        if (showHowToPlay) {
            HowToPlayDialog(
                palette = palette,
                onDismiss = { showHowToPlay = false }
            )
        }
    }
}

@Composable
private fun StatPillItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    palette: ArrowThemePalette,
    iconTint: Color = palette.accent
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = palette.textSecondary
        )
    }
}
