package com.example.gestodeestacionamento.ui.theme

import android.app.Activity
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
    primary = JumpParkGreen,
    secondary = JumpParkDarkBlue,
    tertiary = JumpParkGreen,
    background = JumpParkDarkGray,
    surface = JumpParkDarkGray,
    onPrimary = JumpParkWhite,
    onSecondary = JumpParkWhite,
    onBackground = JumpParkWhite,
    onSurface = JumpParkWhite
)

private val LightColorScheme = lightColorScheme(
    primary = JumpParkGreen,
    secondary = JumpParkDarkBlue,
    tertiary = JumpParkGreen,
    background = JumpParkWhite,
    surface = JumpParkWhite,
    onPrimary = JumpParkWhite,
    onSecondary = JumpParkWhite,
    onBackground = JumpParkDarkBlue,
    onSurface = JumpParkDarkBlue
)

@Composable
fun GestãoDeEstacionamentoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}