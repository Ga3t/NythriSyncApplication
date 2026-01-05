package com.ga3t.nytrisync.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Light green color scheme
private val LightGreenPrimary = Color(0xFF66BB6A)
private val LightGreenOnPrimary = Color(0xFFFFFFFF)
private val LightGreenPrimaryContainer = Color(0xFFC8E6C9)
private val LightGreenOnPrimaryContainer = Color(0xFF1B5E20)

private val LightColors = lightColorScheme(
    primary = LightGreenPrimary,
    onPrimary = LightGreenOnPrimary,
    primaryContainer = LightGreenPrimaryContainer,
    onPrimaryContainer = LightGreenOnPrimaryContainer
)

private val DarkColors = darkColorScheme(
    primary = LightGreenPrimary,
    onPrimary = LightGreenOnPrimary,
    primaryContainer = LightGreenPrimaryContainer,
    onPrimaryContainer = LightGreenOnPrimaryContainer
)

private val RoundedShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

private val AppTypography = Typography().run {
    Typography(
        displayLarge = displayLarge.copy(fontFamily = InterFamily),
        displayMedium = displayMedium.copy(fontFamily = InterFamily),
        displaySmall = displaySmall.copy(fontFamily = InterFamily),
        headlineLarge = headlineLarge.copy(fontFamily = InterFamily, fontWeight = FontWeight.SemiBold),
        headlineMedium = headlineMedium.copy(fontFamily = InterFamily),
        headlineSmall = headlineSmall.copy(fontFamily = InterFamily),
        titleLarge = titleLarge.copy(fontFamily = InterFamily),
        titleMedium = titleMedium.copy(fontFamily = InterFamily),
        titleSmall = titleSmall.copy(fontFamily = InterFamily),
        bodyLarge = bodyLarge.copy(fontFamily = InterFamily),
        bodyMedium = bodyMedium.copy(fontFamily = InterFamily),
        bodySmall = bodySmall.copy(fontFamily = InterFamily),
        labelLarge = labelLarge.copy(fontFamily = InterFamily, fontWeight = FontWeight.Medium),
        labelMedium = labelMedium.copy(fontFamily = InterFamily),
        labelSmall = labelSmall.copy(fontFamily = InterFamily)
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (darkTheme) DarkColors else LightColors
        }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = RoundedShapes,
        typography = AppTypography,
        content = content
    )
}