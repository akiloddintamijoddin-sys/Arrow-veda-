package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.GameThemeType
import com.example.model.ThemePalettes
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    SPLASH,
    HOME,
    GAME
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val selectedTheme by viewModel.preferences.selectedTheme.collectAsStateWithLifecycle()
            val palette = ThemePalettes.getPalette(selectedTheme)

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = palette.background
                ) {
                    ArrowEscapeApp(
                        viewModel = viewModel,
                        currentTheme = selectedTheme
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.preferences.musicEnabled.value) {
            viewModel.soundManager.startMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.soundManager.stopMusic()
    }
}

@Composable
fun ArrowEscapeApp(
    viewModel: GameViewModel,
    currentTheme: GameThemeType,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unlockedLevel by viewModel.preferences.highestUnlockedLevel.collectAsStateWithLifecycle()
    val activeLevel by viewModel.preferences.currentLevel.collectAsStateWithLifecycle()
    val coins by viewModel.preferences.coins.collectAsStateWithLifecycle()
    val hints by viewModel.preferences.hints.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.preferences.soundEnabled.collectAsStateWithLifecycle()
    val musicEnabled by viewModel.preferences.musicEnabled.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.preferences.hapticsEnabled.collectAsStateWithLifecycle()
    val guidanceActive by viewModel.preferences.guidanceActive.collectAsStateWithLifecycle()

    val palette = ThemePalettes.getPalette(currentTheme)

    Crossfade(
        targetState = currentScreen,
        label = "screen_transition",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = AppScreen.HOME
                    }
                )
            }

            AppScreen.HOME -> {
                HomeScreen(
                    currentLevel = activeLevel,
                    unlockedLevel = unlockedLevel,
                    totalStars = viewModel.preferences.getTotalStars(),
                    coins = coins,
                    hints = hints,
                    palette = palette,
                    currentTheme = currentTheme,
                    soundEnabled = soundEnabled,
                    musicEnabled = musicEnabled,
                    hapticsEnabled = hapticsEnabled,
                    guidanceActive = guidanceActive,
                    onPlayClick = {
                        viewModel.loadLevel(activeLevel)
                        currentScreen = AppScreen.GAME
                    },
                    onDailyChallengeClick = {
                        viewModel.loadDailyChallenge()
                        currentScreen = AppScreen.GAME
                    },
                    onSoundToggle = { viewModel.setSoundEnabled(it) },
                    onMusicToggle = { viewModel.setMusicEnabled(it) },
                    onHapticsToggle = { viewModel.setHapticsEnabled(it) },
                    onGuidanceToggle = { viewModel.setGuidanceEnabled(it) },
                    onThemeSelect = { viewModel.setTheme(it) },
                    onResetProgress = { viewModel.resetAllProgress() },
                    onTestSound = { soundType ->
                        when (soundType) {
                            "whoosh" -> viewModel.soundManager.playSlideWhoosh()
                            "knock" -> viewModel.soundManager.playBlockError()
                            "pop" -> viewModel.soundManager.playWaterDropPop()
                            "ping" -> viewModel.soundManager.playHintPing()
                            "chime" -> viewModel.soundManager.playVictoryChime()
                            "vibrate" -> viewModel.soundManager.vibrateSuccess()
                            else -> viewModel.soundManager.playButtonClick()
                        }
                    }
                )
            }

            AppScreen.GAME -> {
                GameScreen(
                    state = uiState,
                    palette = palette,
                    coins = coins,
                    hints = hints,
                    currentTheme = currentTheme,
                    soundEnabled = soundEnabled,
                    musicEnabled = musicEnabled,
                    hapticsEnabled = hapticsEnabled,
                    onPathArrowTapped = { arrow -> viewModel.onPathArrowTapped(arrow) },
                    onHintClick = { viewModel.requestHint() },
                    onRestartClick = { viewModel.restartLevel() },
                    onNextLevelClick = { viewModel.nextLevel() },
                    onReviveClick = { viewModel.revive() },
                    onBackToHome = { currentScreen = AppScreen.HOME },
                    onSoundToggle = { viewModel.setSoundEnabled(it) },
                    onMusicToggle = { viewModel.setMusicEnabled(it) },
                    onHapticsToggle = { viewModel.setHapticsEnabled(it) },
                    onGuidanceToggle = { viewModel.setGuidanceEnabled(it) },
                    onThemeSelect = { viewModel.setTheme(it) },
                    onResetProgress = {
                        viewModel.resetAllProgress()
                        currentScreen = AppScreen.HOME
                    },
                    onTestSound = { soundType ->
                        when (soundType) {
                            "whoosh" -> viewModel.soundManager.playSlideWhoosh()
                            "knock" -> viewModel.soundManager.playBlockError()
                            "pop" -> viewModel.soundManager.playWaterDropPop()
                            "ping" -> viewModel.soundManager.playHintPing()
                            "chime" -> viewModel.soundManager.playVictoryChime()
                            "vibrate" -> viewModel.soundManager.vibrateSuccess()
                            else -> viewModel.soundManager.playButtonClick()
                        }
                    }
                )
            }
        }
    }
}
