package com.example.model

import androidx.compose.ui.graphics.Color

enum class GameThemeType(val displayName: String) {
    SAND("Zen Sand"),
    MIDNIGHT("Midnight"),
    EMERALD("Emerald"),
    SAKURA("Sakura")
}

data class ArrowThemePalette(
    val type: GameThemeType,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val boardBackground: Color,
    val boardBorder: Color,
    val gridLine: Color,
    val arrowColors: List<Color>,
    val arrowBackground: Color,
    val clearHighlight: Color,
    val errorColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color
)

object ThemePalettes {
    val Sand = ArrowThemePalette(
        type = GameThemeType.SAND,
        background = Color(0xFFF6F2EC),
        surface = Color(0xFFEFE8DE),
        surfaceVariant = Color(0xFFE5DDD0),
        boardBackground = Color(0xFFEBE3D7),
        boardBorder = Color(0xFFD8CEBF),
        gridLine = Color(0x30A89C8C),
        arrowColors = listOf(
            Color(0xFF2C3E50), // Navy slate
            Color(0xFF34495E), // Steel
            Color(0xFF2B5B84), // Soft ocean
            Color(0xFF4A6572)  // Slate
        ),
        arrowBackground = Color(0xFFFAF7F2),
        clearHighlight = Color(0xFF10B981), // Emerald glow
        errorColor = Color(0xFFEF4444),
        textPrimary = Color(0xFF1E293B),
        textSecondary = Color(0xFF64748B),
        accent = Color(0xFFE07A5F) // Terracotta coral
    )

    val Midnight = ArrowThemePalette(
        type = GameThemeType.MIDNIGHT,
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B),
        surfaceVariant = Color(0xFF334155),
        boardBackground = Color(0xFF162238),
        boardBorder = Color(0xFF2E3E5C),
        gridLine = Color(0x3064748B),
        arrowColors = listOf(
            Color(0xFF38BDF8), // Cyan ice
            Color(0xFF818CF8), // Indigo
            Color(0xFF2DD4BF), // Teal
            Color(0xFFA78BFA)  // Lavender
        ),
        arrowBackground = Color(0xFF1E293B),
        clearHighlight = Color(0xFF34D399),
        errorColor = Color(0xFFF87171),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        accent = Color(0xFF38BDF8)
    )

    val Emerald = ArrowThemePalette(
        type = GameThemeType.EMERALD,
        background = Color(0xFF0F231D),
        surface = Color(0xFF1B3830),
        surfaceVariant = Color(0xFF264C41),
        boardBackground = Color(0xFF142B24),
        boardBorder = Color(0xFF2A574A),
        gridLine = Color(0x303E7A69),
        arrowColors = listOf(
            Color(0xFF6EE7B7), // Mint
            Color(0xFFA7F3D0), // Light jade
            Color(0xFFFDE68A), // Soft amber
            Color(0xFF86EFAC)  // Spring green
        ),
        arrowBackground = Color(0xFF1B3830),
        clearHighlight = Color(0xFFFBBF24),
        errorColor = Color(0xFFF87171),
        textPrimary = Color(0xFFECFDF5),
        textSecondary = Color(0xFF99F6E4),
        accent = Color(0xFF34D399)
    )

    val Sakura = ArrowThemePalette(
        type = GameThemeType.SAKURA,
        background = Color(0xFF231625),
        surface = Color(0xFF332035),
        surfaceVariant = Color(0xFF452B48),
        boardBackground = Color(0xFF2B1A2D),
        boardBorder = Color(0xFF533557),
        gridLine = Color(0x3084538A),
        arrowColors = listOf(
            Color(0xFFF472B6), // Pink
            Color(0xFFC084FC), // Lilac
            Color(0xFFFB7185), // Rose
            Color(0xFFE879F9)  // Fuchsia
        ),
        arrowBackground = Color(0xFF332035),
        clearHighlight = Color(0xFFF43F5E),
        errorColor = Color(0xFFFB923C),
        textPrimary = Color(0xFFFDF2F8),
        textSecondary = Color(0xFFFBCFE8),
        accent = Color(0xFFF472B6)
    )

    fun getPalette(type: GameThemeType): ArrowThemePalette = when (type) {
        GameThemeType.SAND -> Sand
        GameThemeType.MIDNIGHT -> Midnight
        GameThemeType.EMERALD -> Emerald
        GameThemeType.SAKURA -> Sakura
    }
}
