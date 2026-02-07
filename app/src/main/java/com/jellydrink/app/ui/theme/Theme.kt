package com.jellydrink.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = JellyBlue,
    onPrimary = DeepOcean,
    primaryContainer = JellyBlueDark,
    onPrimaryContainer = JellyBlueLight,
    secondary = JellyPurple,
    onSecondary = DeepOcean,
    secondaryContainer = JellyPurpleLight,
    tertiary = JellyCyan,
    background = DeepOcean,
    surface = MidOcean,
    onBackground = TextOnDark,
    onSurface = TextOnDark
)

private val LightColorScheme = lightColorScheme(
    primary = JellyBlueDark,
    onPrimary = SurfaceLight,
    primaryContainer = JellyBlueLight,
    onPrimaryContainer = JellyBlueDark,
    secondary = JellyPurple,
    onSecondary = SurfaceLight,
    secondaryContainer = JellyPurpleLight,
    tertiary = JellyCyan,
    background = SurfaceLight,
    surface = SurfaceLight,
    onBackground = TextOnLight,
    onSurface = TextOnLight
)

@Composable
fun JellyDrinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JellyTypography,
        content = content
    )
}
