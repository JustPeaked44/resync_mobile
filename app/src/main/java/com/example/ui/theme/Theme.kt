package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Custom Light Color Scheme matching Resync Brand Guidelines
private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = SurfaceWhite,
    secondary = HoverIndigo,
    onSecondary = SurfaceWhite,
    tertiary = SuccessEmerald,
    onTertiary = SurfaceWhite,
    background = BackgroundSlate,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundSlate,
    onSurfaceVariant = TextSecondary,
    outline = BorderSlate,
    error = ErrorRose,
    onError = SurfaceWhite
)

// Custom Dark Color Scheme matching Resync Brand Guidelines
private val DarkColorScheme = darkColorScheme(
    primary = HoverIndigo, // A slightly brighter variant for dark mode readability
    onPrimary = BackgroundSlateDark,
    secondary = PrimaryIndigo,
    onSecondary = BackgroundSlateDark,
    tertiary = SuccessEmerald,
    onTertiary = BackgroundSlateDark,
    background = BackgroundSlateDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceWhiteDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceWhiteDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderSlateDark,
    error = ErrorRose,
    onError = SurfaceWhite
)

@Composable
fun ResyncTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// Retro-compatibility wrapper
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ResyncTheme(
        isDarkTheme = darkTheme,
        content = content
    )
}
