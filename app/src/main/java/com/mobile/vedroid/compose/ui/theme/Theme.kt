package com.mobile.vedroid.compose.ui.theme

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
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit

private val LightColorScheme = lightColorScheme(

    primary = Purple,
    // Other default colors to override
    onPrimary = Color.White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = PurpleDarker,

    secondary = Grey,
    // Other default colors to override
    onSecondary = Color.White,
    secondaryContainer = GreyLight,
    onSecondaryContainer = GreyDarker,

    tertiary = Lilac,
    // Other default colors to override
    onTertiary = Color.White,
    tertiaryContainer = LilacLight,
    onTertiaryContainer = LilacDarker,

    // Other default colors to override
    surface = Light,
    onSurface = Dark,

    background = Light,
    onBackground = Dark

//    primary = Green,
//    // Other default colors to override
//    onPrimary = Color.White,
//    primaryContainer = GreenLight,
//    onPrimaryContainer = GreenDarker,
//
//    secondary = Blue,
//    // Other default colors to override
//    onSecondary = Color.White,
//    secondaryContainer = BlueLight,
//    onSecondaryContainer = BlueDarker,
//
//    tertiary = BlueDeep,
//    // Other default colors to override
//    onTertiary = Color.White,
//    tertiaryContainer = BlueDeepLight,
//    onTertiaryContainer = BlueDeepDarker,
//
//    // Other default colors to override
//    surface = ComposeLight,
//    onSurface = Dark,
//
//    background = ComposeLight,
//    onBackground = Dark
)


private val DarkColorScheme = darkColorScheme(

    primary = Purple,
    // Other default colors to override
    onPrimary = PurpleDarker,
    primaryContainer = PurpleDark,
    onPrimaryContainer = PurpleLight,

    secondary = Grey,
    // Other default colors to override
    onSecondary = GreyDarker,
    secondaryContainer = GreyDark,
    onSecondaryContainer = GreyLight,

    tertiary = Lilac,
    // Other default colors to override
    onTertiary = LilacDarker,
    tertiaryContainer = LilacDark,
    onTertiaryContainer = LilacLight,

    // Other default colors to override
    surface = Dark,
    onSurface = Light,

    background = Dark,
    onBackground = Light
)

private fun scaledTypography(base: Typography, scale: Float): Typography {
    if (scale == 1f) return base

    fun TextStyle.scaled(): TextStyle {
        val newSize =
            if (fontSize != TextUnit.Unspecified) fontSize * scale else fontSize
        val newLine =
            if (lineHeight != TextUnit.Unspecified) lineHeight * scale else lineHeight
        return copy(fontSize = newSize, lineHeight = newLine)
    }

    return Typography(
        displayLarge   = base.displayLarge.scaled(),
        displayMedium  = base.displayMedium.scaled(),
        displaySmall   = base.displaySmall.scaled(),
        headlineLarge  = base.headlineLarge.scaled(),
        headlineMedium = base.headlineMedium.scaled(),
        headlineSmall  = base.headlineSmall.scaled(),
        titleLarge     = base.titleLarge.scaled(),
        titleMedium    = base.titleMedium.scaled(),
        titleSmall     = base.titleSmall.scaled(),
        bodyLarge      = base.bodyLarge.scaled(),
        bodyMedium     = base.bodyMedium.scaled(),
        bodySmall      = base.bodySmall.scaled(),
        labelLarge     = base.labelLarge.scaled(),
        labelMedium    = base.labelMedium.scaled(),
        labelSmall     = base.labelSmall.scaled()
    )
}



@Composable
fun RentCamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1f,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color is available on Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography(Typography, fontScale),
        content = content
    )
}