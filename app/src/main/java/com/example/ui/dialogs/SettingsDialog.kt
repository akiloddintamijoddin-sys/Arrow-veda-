package com.example.ui.dialogs

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ArrowThemePalette
import com.example.model.GameThemeType
import com.example.model.ThemePalettes

@Composable
fun SettingsDialog(
    currentTheme: GameThemeType,
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    hapticsEnabled: Boolean,
    guidanceDefault: Boolean,
    palette: ArrowThemePalette,
    onSoundToggle: (Boolean) -> Unit,
    onMusicToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit,
    onGuidanceToggle: (Boolean) -> Unit,
    onThemeSelect: (GameThemeType) -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit,
    onTestSound: ((String) -> Unit)? = null
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, palette.boardBorder, RoundedCornerShape(28.dp))
                .testTag("settings_dialog"),
            color = palette.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = palette.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle: Background Ambient Zen Music
                SettingsToggleRow(
                    icon = Icons.Default.MusicNote,
                    title = "Ambient Zen Music",
                    subtitle = if (musicEnabled) "Playing soft meditative chords" else "Muted",
                    checked = musicEnabled,
                    palette = palette,
                    onCheckedChange = onMusicToggle
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Sound
                SettingsToggleRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = "Sound Effects",
                    subtitle = if (soundEnabled) "Crisp ASMR tactile audio active" else "Muted",
                    checked = soundEnabled,
                    palette = palette,
                    onCheckedChange = onSoundToggle
                )

                // Sound Effect Test Buttons (Immediate live audio verification)
                if (soundEnabled && onTestSound != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.background.copy(alpha = 0.4f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Test:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textSecondary
                        )
                        SoundTestChip(label = "Slide", onClick = { onTestSound("whoosh") }, palette = palette)
                        SoundTestChip(label = "Knock", onClick = { onTestSound("knock") }, palette = palette)
                        SoundTestChip(label = "Pop", onClick = { onTestSound("pop") }, palette = palette)
                        SoundTestChip(label = "Hint", onClick = { onTestSound("ping") }, palette = palette)
                        SoundTestChip(label = "Win", onClick = { onTestSound("chime") }, palette = palette)
                        SoundTestChip(label = "Vibrate", onClick = { onTestSound("vibrate") }, palette = palette)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Haptics
                SettingsToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Vibrations",
                    subtitle = if (hapticsEnabled) "Active on slides and mistakes" else "Off",
                    checked = hapticsEnabled,
                    palette = palette,
                    onCheckedChange = onHapticsToggle
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Guidance
                SettingsToggleRow(
                    icon = Icons.Default.Visibility,
                    title = "Guidance Assistant",
                    subtitle = if (guidanceDefault) "Aura ON: Clear arrows highlighted" else "Off",
                    checked = guidanceDefault,
                    palette = palette,
                    onCheckedChange = onGuidanceToggle
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Theme Selector Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Visual Themes",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameThemeType.values().forEach { themeType ->
                        val themePal = ThemePalettes.getPalette(themeType)
                        val isSelected = currentTheme == themeType

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(themePal.background)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) palette.accent else themePal.boardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onThemeSelect(themeType) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(themePal.accent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = themeType.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = themePal.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Reset Progress Section
                if (!showResetConfirm) {
                    TextButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = palette.errorColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reset All Game Progress",
                            color = palette.errorColor,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.errorColor.copy(alpha = 0.12f))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Reset levels, stars & coins to Level 1?",
                            fontSize = 12.sp,
                            color = palette.errorColor,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    onResetProgress()
                                    showResetConfirm = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = palette.errorColor)
                            ) {
                                Text("Yes, Reset", fontSize = 12.sp)
                            }
                            TextButton(onClick = { showResetConfirm = false }) {
                                Text("Cancel", color = palette.textSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    palette: ArrowThemePalette,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.background.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = palette.accent,
                uncheckedThumbColor = palette.textSecondary,
                uncheckedTrackColor = palette.surfaceVariant
            )
        )
    }
}

@Composable
private fun SoundTestChip(
    label: String,
    onClick: () -> Unit,
    palette: ArrowThemePalette
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surface)
            .border(1.dp, palette.boardBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = palette.accent
        )
    }
}
