package ua.leonidius.beatinspector.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    onNavigateToSongDetails: (String) -> Unit,
    onNavigateToLargeText: (Int) -> Unit,
) {
    val navController = rememberNavController() // todo: should there be another navContrlloer for this nested graph o it's ok to use the same one?

    Column {
        NavHost(navController, startDestination = BottomNavScreen.Search.route, Modifier.weight(1f)) {
            composable(BottomNavScreen.Search.route) {
                SearchScreen(onNavigateToSongDetails = onNavigateToSongDetails)
            }
            composable(BottomNavScreen.Settings.route) {
                SettingsScreen()
            }
        }

        BottomNavigationBar(navController = navController)
    }

}


@Composable
fun BottomNavigationBar(
    navController: NavController,
) {
    BottomNavigation() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                label = { Text(stringResource(screen.titleResId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}