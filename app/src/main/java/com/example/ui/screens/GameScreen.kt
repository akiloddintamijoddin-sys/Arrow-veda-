package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.model.ArrowThemePalette
import com.example.model.GameThemeType
import com.example.model.GameUiState
import com.example.model.PathArrow
import com.example.ui.components.AmazeBottomHud
import com.example.ui.components.AmazeTopHud
import com.example.ui.components.LabyrinthBoard
import com.example.ui.dialogs.DefeatDialog
import com.example.ui.dialogs.HowToPlayDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.VictoryDialog

@Composable
fun GameScreen(
    state: GameUiState,
    palette: ArrowThemePalette,
    coins: Int,
    hints: Int,
    currentTheme: GameThemeType,
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    hapticsEnabled: Boolean,
    onPathArrowTapped: (PathArrow) -> Unit,
    onHintClick: () -> Unit,
    onRestartClick: () -> Unit,
    onNextLevelClick: () -> Unit,
    onReviveClick: () -> Unit,
    onBackToHome: () -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onMusicToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit,
    onGuidanceToggle: (Boolean) -> Unit,
    onThemeSelect: (GameThemeType) -> Unit,
    onResetProgress: () -> Unit,
    onTestSound: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHowToPlay by remember { mutableStateOf(false) }

    // Physical/System back button navigates back to Home screen menu with all options
    BackHandler(onBack = onBackToHome)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = palette.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Amaze GO Top HUD: Back to Home, Level 27 (Golden), Water droplets, FAQ/Help & Settings icons
            AmazeTopHud(
                levelId = state.level.id,
                shapeName = state.labyrinthLevel.title,
                lives = state.lives,
                maxLives = state.maxLives,
                palette = palette,
                onBackClick = onBackToHome,
                onHelpClick = { showHowToPlay = true },
                onSettingsClick = { showSettingsDialog = true }
            )

            // Centered Labyrinth Puzzle Board (Anchor / Dog / Figurative Shape)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LabyrinthBoard(
                    level = state.labyrinthLevel,
                    activeArrows = state.activePathArrows,
                    animatingArrows = state.animatingPathArrows,
                    shakingArrowId = state.shakingArrowId,
                    hintedArrowId = state.highlightedHintArrowId,
                    blockedArrowId = state.blockedPathArrowId,
                    guidanceActive = state.guidanceActive,
                    clearArrowIds = state.clearArrowIds,
                    palette = palette,
                    onArrowTapped = onPathArrowTapped
                )
            }

            // Amaze GO Bottom Controls: Two original options (Guidance & Hint)
            AmazeBottomHud(
                guidanceActive = state.guidanceActive,
                hintsCount = hints,
                onGuidanceClick = { onGuidanceToggle(!state.guidanceActive) },
                onHintClick = onHintClick
            )
        }

        // How to Play & Rules / FAQ Dialog
        if (showHowToPlay) {
            HowToPlayDialog(
                palette = palette,
                onDismiss = { showHowToPlay = false }
            )
        }

        // Victory Dialog
        if (state.isCompleted) {
            VictoryDialog(
                levelId = state.level.id,
                starsEarned = state.starsEarned,
                coinsEarned = state.coinsEarned,
                difficulty = state.labyrinthLevel.difficulty,
                palette = palette,
                onNextLevel = onNextLevelClick,
                onReplay = onRestartClick,
                onHome = onBackToHome
            )
        }

        // Defeat Dialog (Out of Droplet Lives)
        if (state.isGameOver) {
            DefeatDialog(
                coins = coins,
                palette = palette,
                onRevive = onReviveClick,
                onRestart = onRestartClick,
                onHome = onBackToHome
            )
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                currentTheme = currentTheme,
                soundEnabled = soundEnabled,
                musicEnabled = musicEnabled,
                hapticsEnabled = hapticsEnabled,
                guidanceDefault = state.guidanceActive,
                palette = palette,
                onSoundToggle = onSoundToggle,
                onMusicToggle = onMusicToggle,
                onHapticsToggle = onHapticsToggle,
                onGuidanceToggle = onGuidanceToggle,
                onThemeSelect = onThemeSelect,
                onResetProgress = {
                    onResetProgress()
                    showSettingsDialog = false
                },
                onDismiss = { showSettingsDialog = false },
                onTestSound = onTestSound
            )
        }
    }
}
