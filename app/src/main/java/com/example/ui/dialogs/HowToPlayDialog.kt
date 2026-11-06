package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ArrowThemePalette

data class FaqItemData(
    val id: String,
    val category: String,
    val question: String,
    val answer: String,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
fun HowToPlayDialog(
    palette: ArrowThemePalette,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // Default to FAQ tab (0)

    val faqList = remember {
        listOf(
            FaqItemData(
                id = "faq_direction",
                category = "Arrow Direction",
                question = "How do I know which way an arrow will fly?",
                answer = "Every arrow features a sharp triangular arrowhead with a bright white directional chevron (^, v, <, >) marking its forward trajectory. The opposite end (the tail) has a distinct hollow donut ring, making it quick and intuitive to identify the head and its flight direction.",
                icon = Icons.Default.Navigation,
                iconTint = Color(0xFF00A3FF)
            ),
            FaqItemData(
                id = "faq_blocked",
                category = "Advanced Levels",
                question = "Why does an arrow shake red and refuse to move when tapped?",
                answer = "An arrow can only slide out if its straight-line path to the board boundary is completely clear of any other arrows. If its nose points into another arrow, it is blocked: tapping it causes a red error shake and deducts 1 water droplet life. Find the arrows whose exit corridor is totally unobstructed first!",
                icon = Icons.Default.Close,
                iconTint = Color(0xFFEF4444)
            ),
            FaqItemData(
                id = "faq_starters",
                category = "Game Strategy",
                question = "How many arrows are unblocked at the beginning of a level?",
                answer = "In our advanced labyrinth tangles, only 1 or 2 starter arrows are initially clear at the outer edge! All remaining arrows are interlocked behind each other in a causal dependency chain. Untangling the first root arrow unlocks the next arrow, triggering a hypnotic domino cascade.",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                iconTint = Color(0xFF10B981)
            ),
            FaqItemData(
                id = "faq_hints",
                category = "Hints & Assistance",
                question = "What should I do if I get stuck on a difficult board?",
                answer = "Tap the Lightbulb (Hint) icon in the top or bottom action bar. It instantly scans the current board and highlights a guaranteed-unblocked arrow with an electric cyan glow that is ready to slide out without collision.",
                icon = Icons.Default.Lightbulb,
                iconTint = Color(0xFFF59E0B)
            ),
            FaqItemData(
                id = "faq_audio",
                category = "Sound & ASMR",
                question = "How do I adjust Zen Music, slide audio, and vibration?",
                answer = "Tap the Settings (Sliders/Tune) icon on the Home screen or top bar. From there, you can toggle the procedural bamboo flute Zen music, tactile ASMR whoosh slide sounds, and haptic vibration feedback, as well as test each sound in real-time.",
                icon = Icons.Default.MusicNote,
                iconTint = Color(0xFF8B5CF6)
            ),
            FaqItemData(
                id = "faq_lives",
                category = "Lives & Revives",
                question = "How do water droplets work and what happens if they run out?",
                answer = "You have a pool of water droplets (lives) per puzzle. Tapping a blocked arrow uses 1 droplet. If all droplets are depleted, you can revive with coins or continue from your exact current board state without losing your progress.",
                icon = Icons.Default.Favorite,
                iconTint = Color(0xFFEC4899)
            ),
            FaqItemData(
                id = "faq_offline",
                category = "Offline & Levels",
                question = "Does ArrowVeda work 100% offline without Wi-Fi or data?",
                answer = "Yes! ArrowVeda is completely 100% offline. All 10,000 unique silhouette mazes (Anchor, Puppy, Dragon, Lotus, etc.) are computed on-device with zero internet needed, and your level progress and stars automatically save to your device storage.",
                icon = Icons.Default.WifiOff,
                iconTint = Color(0xFF0284C7)
            ),
            FaqItemData(
                id = "faq_daily",
                category = "Daily Challenge",
                question = "What is the Daily Zen Challenge?",
                answer = "Each calendar day features a unique mindful tangle challenge. Clearing the Daily Challenge awards you bonus stars, coins, and hint items to aid your journey across the 10,000 levels.",
                icon = Icons.Default.CalendarMonth,
                iconTint = Color(0xFF14B8A6)
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = palette.accent.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, palette.boardBorder, RoundedCornerShape(28.dp))
                .testTag("how_to_play_dialog"),
            color = palette.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(palette.accent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Default.QuestionAnswer else Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (selectedTab == 0) "ArrowVeda FAQs" else "How to Play",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = if (selectedTab == 0) "Frequently Asked Questions" else "Rules & Beginner Guide",
                                fontSize = 11.sp,
                                color = palette.textSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(palette.background.copy(alpha = 0.6f))
                            .testTag("faq_dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher: FAQs vs Rules
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = palette.background.copy(alpha = 0.6f),
                    contentColor = palette.accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, palette.boardBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = palette.accent,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("tab_faqs"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.QuestionAnswer,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 0) palette.accent else palette.textSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FAQs",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 0) palette.accent else palette.textSecondary
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("tab_rules"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 1) palette.accent else palette.textSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Rules & Guide",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 1) palette.accent else palette.textSecondary
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(390.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedTab == 0) {
                        // FAQ Tab Content
                        faqList.forEachIndexed { index, item ->
                            FaqExpandableCard(
                                item = item,
                                defaultExpanded = index <= 1, // First 2 expanded by default
                                palette = palette
                            )
                        }
                    } else {
                        // Rules Tab Content
                        GuideRuleCard(
                            step = "1",
                            title = "Trace the Snake Paths",
                            subtitle = "Identify Head & Tail",
                            description = "Every arrow winds through the shape from its starting donut ring (tail) to its sharp pointed tip with directional chevron (head).",
                            icon = Icons.Default.Sensors,
                            palette = palette
                        )

                        GuideRuleCard(
                            step = "2",
                            title = "Look for Clear Corridors",
                            subtitle = "Unobstructed Exit",
                            description = "Arrows can only slide off the board if the straight line in front of their head to the board's outer boundary has no obstacles.",
                            icon = Icons.Default.Navigation,
                            palette = palette
                        )

                        GuideRuleCard(
                            step = "3",
                            title = "Trigger Domino Cascades",
                            subtitle = "Solve Layer by Layer",
                            description = "Only 1 or 2 starter arrows are unblocked initially. Releasing a starter arrow unlocks the arrows trapped behind it.",
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            palette = palette
                        )

                        GuideRuleCard(
                            step = "4",
                            title = "Mindful Zen & ASMR",
                            subtitle = "Relax and De-Stress",
                            description = "Enjoy soothing bamboo flute harmonies, tactile sliding sounds, and calm haptic feedback as you untangle your mind.",
                            icon = Icons.Default.Spa,
                            palette = palette
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("faq_dialog_got_it_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Got it! Let's Untangle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FaqExpandableCard(
    item: FaqItemData,
    defaultExpanded: Boolean,
    palette: ArrowThemePalette
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.background.copy(alpha = 0.55f))
            .border(
                width = 1.dp,
                color = if (expanded) palette.accent.copy(alpha = 0.4f) else palette.boardBorder.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { expanded = !expanded }
            .padding(14.dp)
            .testTag(item.id)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(item.iconTint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(palette.surface)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.iconTint,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.question,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    lineHeight = 18.sp
                )
            }

            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = palette.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 42.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.boardBorder.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = item.answer,
                        fontSize = 12.sp,
                        color = palette.textPrimary.copy(alpha = 0.9f),
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideRuleCard(
    step: String,
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    palette: ArrowThemePalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.background.copy(alpha = 0.55f))
            .border(1.dp, palette.boardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(palette.accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "STEP $step • ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.accent
                )
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
            }
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = palette.textSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = palette.textSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
