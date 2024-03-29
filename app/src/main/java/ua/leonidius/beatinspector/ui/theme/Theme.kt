package ua.leonidius.beatinspector.ui.theme

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.WindowInsetsController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.em
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.luminance
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40

        /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BeatInspectorTheme(
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
       // isLandscape = //todo pass with compositionLocal
        //    view.context.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    )
}

@Composable
fun ChangeStatusBarColor(colorArgb: Int) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorArgb
            window.navigationBarColor = colorArgb



            // if color is dark, make status bar icons light, otherwise make them dark
            val isColorDark = //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
               if (colorArgb == Color.TRANSPARENT) false else ColorUtils.calculateLuminance(colorArgb) < 0.6f
            //} else {
            //    false
            //}

            Log.d("Theme", "isColorDark: $isColorDark, color: $colorArgb")

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !isColorDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !isColorDark

        }
    }
}