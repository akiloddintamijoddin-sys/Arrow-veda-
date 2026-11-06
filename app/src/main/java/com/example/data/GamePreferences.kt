package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GameThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("arrow_zen_prefs", Context.MODE_PRIVATE)

    private val _highestUnlockedLevel = MutableStateFlow(prefs.getInt(KEY_UNLOCKED_LEVEL, 1))
    val highestUnlockedLevel: StateFlow<Int> = _highestUnlockedLevel.asStateFlow()

    private val _currentLevel = MutableStateFlow(prefs.getInt(KEY_CURRENT_LEVEL, 1))
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _coins = MutableStateFlow(prefs.getInt(KEY_COINS, 100))
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _hints = MutableStateFlow(prefs.getInt(KEY_HINTS, 5))
    val hints: StateFlow<Int> = _hints.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _musicEnabled = MutableStateFlow(prefs.getBoolean(KEY_MUSIC, true))
    val musicEnabled: StateFlow<Boolean> = _musicEnabled.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTICS, true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _guidanceActive = MutableStateFlow(prefs.getBoolean(KEY_GUIDANCE, false))
    val guidanceActive: StateFlow<Boolean> = _guidanceActive.asStateFlow()

    private val _selectedTheme = MutableStateFlow(
        runCatching {
            GameThemeType.valueOf(prefs.getString(KEY_THEME, GameThemeType.SAND.name) ?: GameThemeType.SAND.name)
        }.getOrDefault(GameThemeType.SAND)
    )
    val selectedTheme: StateFlow<GameThemeType> = _selectedTheme.asStateFlow()

    fun getStarsForLevel(levelId: Int): Int {
        return prefs.getInt("stars_$levelId", 0)
    }

    fun getTotalStars(): Int {
        var total = 0
        val maxLevel = _highestUnlockedLevel.value.coerceAtLeast(100)
        for (i in 1..maxLevel) {
            total += prefs.getInt("stars_$i", 0)
        }
        return total
    }

    fun saveLevelComplete(levelId: Int, starsEarned: Int, coinsAwarded: Int) {
        val currentStars = getStarsForLevel(levelId)
        val bestStars = maxOf(currentStars, starsEarned)
        val nextLevel = levelId + 1

        val editor = prefs.edit()
        editor.putInt("stars_$levelId", bestStars)

        val newUnlocked = maxOf(_highestUnlockedLevel.value, nextLevel)
        editor.putInt(KEY_UNLOCKED_LEVEL, newUnlocked)
        editor.putInt(KEY_CURRENT_LEVEL, nextLevel)

        val newCoins = _coins.value + coinsAwarded
        editor.putInt(KEY_COINS, newCoins)
        editor.apply()

        _highestUnlockedLevel.value = newUnlocked
        _currentLevel.value = nextLevel
        _coins.value = newCoins
    }

    fun setCurrentLevel(levelId: Int) {
        prefs.edit().putInt(KEY_CURRENT_LEVEL, levelId).apply()
        _currentLevel.value = levelId
    }

    fun addCoins(amount: Int) {
        val newCoins = _coins.value + amount
        prefs.edit().putInt(KEY_COINS, newCoins).apply()
        _coins.value = newCoins
    }

    fun spendCoins(amount: Int): Boolean {
        if (_coins.value >= amount) {
            val newCoins = _coins.value - amount
            prefs.edit().putInt(KEY_COINS, newCoins).apply()
            _coins.value = newCoins
            return true
        }
        return false
    }

    fun addHints(amount: Int) {
        val newHints = _hints.value + amount
        prefs.edit().putInt(KEY_HINTS, newHints).apply()
        _hints.value = newHints
    }

    fun consumeHint(): Boolean {
        if (_hints.value > 0) {
            val newHints = _hints.value - 1
            prefs.edit().putInt(KEY_HINTS, newHints).apply()
            _hints.value = newHints
            return true
        }
        return false
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MUSIC, enabled).apply()
        _musicEnabled.value = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
        _hapticsEnabled.value = enabled
    }

    fun setGuidanceActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_GUIDANCE, active).apply()
        _guidanceActive.value = active
    }

    fun setSelectedTheme(theme: GameThemeType) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        _selectedTheme.value = theme
    }

    fun resetProgress() {
        prefs.edit().clear().apply()
        _highestUnlockedLevel.value = 1
        _currentLevel.value = 1
        _coins.value = 100
        _hints.value = 5
        _soundEnabled.value = true
        _musicEnabled.value = true
        _hapticsEnabled.value = true
        _guidanceActive.value = false
        _selectedTheme.value = GameThemeType.SAND
    }

    companion object {
        private const val KEY_UNLOCKED_LEVEL = "key_unlocked_level"
        private const val KEY_CURRENT_LEVEL = "key_current_level"
        private const val KEY_COINS = "key_coins"
        private const val KEY_HINTS = "key_hints"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_MUSIC = "key_music"
        private const val KEY_HAPTICS = "key_haptics"
        private const val KEY_GUIDANCE = "key_guidance"
        private const val KEY_THEME = "key_theme"
    }
}
