package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowThemePalette

@Composable
fun AmazeTopHud(
    levelId: Int,
    shapeName: String,
    lives: Int,
    maxLives: Int,
    palette: ArrowThemePalette,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
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
                .padding(top = 4.dp)
        ) {
            // Main Top Bar: Back, Level Number (golden), Help FAQ, Theme, Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button (returns to Home Menu with all catalog & options)
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("amaze_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = Color(0xFF5D4A39),
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Centered Golden Level Title (matches screenshots: "Level 27" / "Level 40")
                Text(
                    text = "Level $levelId",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC08A3E), // Golden mustard from screenshot
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.testTag("level_title_text")
                )

                // Action Icons (Help FAQ & Settings Hexagon - Palette removed per user request)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onHelpClick,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("amaze_faq_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "FAQ & Rules",
                            tint = Color(0xFF5D4A39),
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("amaze_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hexagon,
                            contentDescription = "Settings",
                            tint = Color(0xFF5D4A39),
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            // Thin Horizontal Divider across screen width (from screenshot)
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFFE5DAC8)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Water Droplet Lives System in top-left (from screenshot: 💧 💧 💧 💧)
            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .testTag("water_droplets_container"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..maxLives) {
                    val isAlive = i <= lives
                    val scale by animateFloatAsState(
                        targetValue = if (isAlive) 1f else 0.88f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "dropScale"
                    )

                    WaterDropIcon(
                        isAlive = isAlive,
                        modifier = Modifier
                            .size(26.dp)
                            .scale(scale)
                    )
                }
            }
        }
    }
}

@Composable
fun WaterDropIcon(isAlive: Boolean, modifier: Modifier = Modifier) {
    val dropColor by animateColorAsState(
        targetValue = if (isAlive) Color(0xFF29B6F6) else Color(0xFFE2D6C5),
        label = "dropColor"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Symmetrical smooth water droplet
        val path = Path().apply {
            moveTo(w / 2f, h * 0.05f) // Sharp top tip
            cubicTo(
                w * 0.95f, h * 0.5f,
                w, h * 0.88f,
                w / 2f, h * 0.98f
            )
            cubicTo(
                0f, h * 0.88f,
                w * 0.05f, h * 0.5f,
                w / 2f, h * 0.05f
            )
            close()
        }

        drawPath(path = path, color = dropColor, style = Fill)

        // Soft specular reflection highlight on alive water droplet
        if (isAlive) {
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = w * 0.12f,
                center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.55f)
            )
        }
    }
}

/**
 * Restored original 2 action tool options: Guidance and Hint.
 * (10000+ banner and 9-dot grid button removed per user request)
 */
@Composable
fun AmazeBottomHud(
    guidanceActive: Boolean,
    hintsCount: Int,
    onGuidanceClick: () -> Unit,
    onHintClick: () -> Unit,
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
                .padding(horizontal = 32.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option 1: Guidance Tool (Eye icon toggles clear ray highlights)
            AmazeToolButton(
                icon = if (guidanceActive) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                label = "Guidance",
                isActive = guidanceActive,
                badgeText = if (guidanceActive) "ON" else null,
                badgeColor = Color(0xFF00A3FF),
                onClick = onGuidanceClick,
                testTag = "amaze_guidance_button"
            )

            // Option 2: Hint Tool (Lightbulb with remaining hints badge)
            AmazeToolButton(
                icon = Icons.Default.Lightbulb,
                label = "Hint",
                isActive = false,
                badgeText = if (hintsCount > 0) "$hintsCount" else "🪙",
                badgeColor = Color(0xFFEF4444),
                onClick = onHintClick,
                testTag = "amaze_hint_button"
            )
        }
    }
}

@Composable
fun AmazeToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    badgeText: String? = null,
    badgeColor: Color = Color(0xFF00A3FF),
    onClick: () -> Unit,
    testTag: String = "tool_button"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(
                    elevation = if (isActive) 10.dp else 6.dp,
                    shape = CircleShape,
                    ambientColor = if (isActive) Color(0xFF00A3FF).copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.08f),
                    spotColor = if (isActive) Color(0xFF00A3FF).copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.15f)
                )
                .clip(CircleShape)
                .background(if (isActive) Color(0xFFE0F2FE) else Color.White)
                .border(
                    width = if (isActive) 2.dp else 1.2.dp,
                    color = if (isActive) Color(0xFF00A3FF) else Color(0xFFE5DAC8),
                    shape = CircleShape
                )
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color(0xFF0284C7) else Color(0xFF5D4A39),
                modifier = Modifier.size(32.dp)
            )

            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) Color(0xFF0284C7) else Color(0xFF6B553F)
        )
    }
}
