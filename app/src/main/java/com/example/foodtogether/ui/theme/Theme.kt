package com.example.foodtogether.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OrangeChoosse80,
    secondary = Pink80,
    tertiary = Pink40,
    surface =Pink80,

    primaryContainer = OrangeChoosse80, //выделенный
    onPrimary = black80,//цвет кнопки
    onTertiary = black80,
    onBackground = black80,
    onSurface = black80,


    )


private val LightColorScheme = lightColorScheme(
    primary = OrangeChoosse80, //кнопки

    onPrimary = black80,//цвет кнопки
    primaryContainer = OrangeChoosse80, //выделенный

    secondary = Pink80,
    tertiary = Pink40,

    background = Pink80,
    surface = Pink80,
    onSecondary = black80,
    onTertiary = black80,
    onBackground = black80,
    onSurface = black80 //цвет активной панели навигации

)

@Composable
fun FoodTogetherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}