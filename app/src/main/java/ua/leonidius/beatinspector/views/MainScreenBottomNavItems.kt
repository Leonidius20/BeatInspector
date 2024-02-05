package ua.leonidius.beatinspector.views

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ua.leonidius.beatinspector.R

sealed class BottomNavScreen(val route: String, @StringRes val titleResId: Int, val icon: ImageVector) {
    object Search: BottomNavScreen("search", R.string.search_title, Icons.Default.Search) // todo: also icon?
    object Settings: BottomNavScreen("settings", R.string.settings_title, Icons.Default.Settings)
}

val bottomNavItems = listOf(
    BottomNavScreen.Search,
    BottomNavScreen.Settings,
)