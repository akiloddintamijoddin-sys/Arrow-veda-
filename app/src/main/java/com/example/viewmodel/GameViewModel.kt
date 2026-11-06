package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.GamePreferences
import com.example.data.LabyrinthGenerator
import com.example.data.LabyrinthLevel
import com.example.data.LevelRepository
import com.example.model.GameThemeType
import com.example.model.GameUiState
import com.example.model.PathArrow
import com.example.model.PathSlideAnim
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val preferences = GamePreferences(application)
    val soundManager = SoundManager(application)

    private val _uiState = MutableStateFlow(
        createInitialState(preferences.currentLevel.value)
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        soundManager.isSoundEnabled = preferences.soundEnabled.value
        soundManager.isMusicEnabled = preferences.musicEnabled.value
        soundManager.isHapticsEnabled = preferences.hapticsEnabled.value
        if (preferences.musicEnabled.value) {
            soundManager.startMusic()
        }
        loadLevel(preferences.currentLevel.value)
    }

    private fun createInitialState(levelId: Int): GameUiState {
        val clampedId = levelId.coerceIn(1, 10000)
        val labLevel = LabyrinthGenerator.generateLevel(clampedId)
        val dummyLevel = LevelRepository.getLevel((clampedId - 1) % 100 + 1)
        val clearArrows = LabyrinthGenerator.getClearArrows(labLevel.rows, labLevel.cols, labLevel.arrows)
            .map { it.id }.toSet()

        val maxL = if (clampedId >= 21) 5 else 4

        return GameUiState(
            level = dummyLevel.copy(id = clampedId, title = labLevel.title),
            labyrinthLevel = labLevel,
            activePathArrows = labLevel.arrows,
            animatingPathArrows = emptyList(),
            clearArrowIds = clearArrows,
            guidanceActive = preferences.guidanceActive.value,
            lives = maxL,
            maxLives = maxL,
            mistakes = 0,
            movesCount = 0,
            isCompleted = false,
            isGameOver = false,
            isPaused = false,
            canUndo = false
        )
    }

    fun loadLevel(levelId: Int) {
        val clampedId = levelId.coerceIn(1, 10000)
        preferences.setCurrentLevel(clampedId)

        val labLevel = LabyrinthGenerator.generateLevel(clampedId)
        val dummyLevel = LevelRepository.getLevel((clampedId - 1) % 100 + 1)
        val clearArrows = LabyrinthGenerator.getClearArrows(labLevel.rows, labLevel.cols, labLevel.arrows)
            .map { it.id }.toSet()

        val maxL = if (clampedId >= 21) 5 else 4

        _uiState.value = GameUiState(
            level = dummyLevel.copy(id = clampedId, title = labLevel.title),
            labyrinthLevel = labLevel,
            activePathArrows = labLevel.arrows,
            animatingPathArrows = emptyList(),
            shakingArrowId = null,
            blockedPathArrowId = null,
            highlightedHintArrowId = null,
            guidanceActive = preferences.guidanceActive.value,
            clearArrowIds = clearArrows,
            lives = maxL,
            maxLives = maxL,
            mistakes = 0,
            movesCount = 0,
            isCompleted = false,
            isGameOver = false,
            isPaused = false,
            starsEarned = 0,
            coinsEarned = 0,
            canUndo = false
        )
    }

    fun loadDailyChallenge() {
        val todaySeed = (System.currentTimeMillis() / (1000 * 60 * 60 * 24) % 10000 + 1).toInt()
        loadLevel(todaySeed)
    }

    fun toggleGuidance() {
        val newActive = !_uiState.value.guidanceActive
        preferences.setGuidanceActive(newActive)
        soundManager.playButtonClick()
        _uiState.update { it.copy(guidanceActive = newActive) }
    }

    fun onPathArrowTapped(arrow: PathArrow) {
        val state = _uiState.value
        if (state.isCompleted || state.isGameOver || state.isPaused) return
        if (state.animatingPathArrows.any { it.arrow.id == arrow.id }) return

        val isClear = LabyrinthGenerator.isRayClear(
            head = arrow.head,
            dir = arrow.direction,
            rows = state.labyrinthLevel.rows,
            cols = state.labyrinthLevel.cols,
            activeArrows = state.activePathArrows,
            currentArrowId = arrow.id
        )

        if (isClear) {
            handlePathArrowEscape(arrow)
        } else {
            handlePathArrowBlocked(arrow)
        }
    }

    private fun handlePathArrowEscape(arrow: PathArrow) {
        soundManager.playSlideWhoosh()

        val currentState = _uiState.value
        val newActive = currentState.activePathArrows.filter { it.id != arrow.id }

        val distance = 1400f
        val targetX = arrow.direction.dCol * distance
        val targetY = arrow.direction.dRow * distance

        val anim = PathSlideAnim(
            arrow = arrow,
            targetOffsetX = targetX,
            targetOffsetY = targetY
        )

        val newAnimating = currentState.animatingPathArrows + anim

        val newClearArrows = LabyrinthGenerator.getClearArrows(
            currentState.labyrinthLevel.rows,
            currentState.labyrinthLevel.cols,
            newActive
        ).map { it.id }.toSet()

        val isNowCompleted = newActive.isEmpty()
        var stars = 0
        var coins = 0

        if (isNowCompleted) {
            stars = when (currentState.mistakes) {
                0 -> 3
                1 -> 2
                else -> 1
            }
            coins = 50 + (stars * 20)
            preferences.saveLevelComplete(currentState.level.id, stars, coins)
            viewModelScope.launch {
                delay(540)
                soundManager.playVictoryChime()
            }
        }

        _uiState.update { prev ->
            prev.copy(
                activePathArrows = newActive,
                animatingPathArrows = newAnimating,
                clearArrowIds = newClearArrows,
                blockedPathArrowId = null,
                highlightedHintArrowId = if (prev.highlightedHintArrowId == arrow.id) null else prev.highlightedHintArrowId,
                movesCount = prev.movesCount + 1,
                isCompleted = isNowCompleted,
                starsEarned = stars,
                coinsEarned = coins
            )
        }

        viewModelScope.launch {
            delay(520)
            _uiState.update { prev ->
                prev.copy(animatingPathArrows = prev.animatingPathArrows.filter { it.arrow.id != arrow.id })
            }
        }
    }

    private fun handlePathArrowBlocked(arrow: PathArrow) {
        soundManager.playBlockError()

        val currentMistakes = _uiState.value.mistakes + 1
        val newLives = (_uiState.value.lives - 1).coerceAtLeast(0)
        val gameOver = newLives <= 0

        if (newLives < _uiState.value.lives) {
            viewModelScope.launch {
                delay(120)
                soundManager.playWaterDropPop()
            }
        }

        _uiState.update { prev ->
            prev.copy(
                blockedPathArrowId = arrow.id,
                shakingArrowId = arrow.id,
                lives = newLives,
                mistakes = currentMistakes,
                isGameOver = gameOver
            )
        }

        viewModelScope.launch {
            delay(500)
            _uiState.update { prev ->
                if (prev.shakingArrowId == arrow.id) {
                    prev.copy(shakingArrowId = null)
                } else prev
            }
        }
    }

    fun requestHint() {
        val state = _uiState.value
        if (state.isCompleted || state.isGameOver) return

        val clearArrows = LabyrinthGenerator.getClearArrows(
            state.labyrinthLevel.rows,
            state.labyrinthLevel.cols,
            state.activePathArrows
        )

        if (clearArrows.isEmpty()) return

        val hasHint = preferences.consumeHint()
        if (!hasHint) {
            val spentCoins = preferences.spendCoins(20)
            if (!spentCoins) {
                soundManager.playBlockError()
                return
            }
        }

        val hintArrow = clearArrows.first()
        soundManager.playHintPing()

        _uiState.update { prev ->
            prev.copy(
                highlightedHintArrowId = hintArrow.id,
                blockedPathArrowId = null
            )
        }

        viewModelScope.launch {
            delay(3500)
            _uiState.update { prev ->
                if (prev.highlightedHintArrowId == hintArrow.id) {
                    prev.copy(highlightedHintArrowId = null)
                } else prev
            }
        }
    }

    fun restartLevel() {
        soundManager.playButtonClick()
        loadLevel(_uiState.value.level.id)
    }

    fun nextLevel() {
        soundManager.playButtonClick()
        val nextId = (_uiState.value.level.id + 1).coerceIn(1, 10000)
        loadLevel(nextId)
    }

    fun revive() {
        preferences.spendCoins(50)
        soundManager.playHintPing()
        val maxL = _uiState.value.maxLives
        _uiState.update { prev ->
            prev.copy(
                lives = maxL,
                isGameOver = false
            )
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferences.setSoundEnabled(enabled)
        soundManager.isSoundEnabled = enabled
        if (enabled) {
            soundManager.playHintPing()
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        preferences.setMusicEnabled(enabled)
        soundManager.isMusicEnabled = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        preferences.setHapticsEnabled(enabled)
        soundManager.isHapticsEnabled = enabled
        if (enabled) {
            soundManager.vibrateTick()
        }
    }

    fun setGuidanceEnabled(enabled: Boolean) {
        preferences.setGuidanceActive(enabled)
        soundManager.playButtonClick()
        _uiState.update { it.copy(guidanceActive = enabled) }
    }

    fun setTheme(theme: GameThemeType) {
        preferences.setSelectedTheme(theme)
        soundManager.playButtonClick()
    }

    fun resetAllProgress() {
        preferences.resetProgress()
        soundManager.playVictoryChime()
        loadLevel(1)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
