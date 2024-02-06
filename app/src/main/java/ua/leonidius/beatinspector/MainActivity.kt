package ua.leonidius.beatinspector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.leonidius.beatinspector.ui.theme.BeatInspectorTheme
import ua.leonidius.beatinspector.views.LoginScreen
import ua.leonidius.beatinspector.views.LongTextScreen
import ua.leonidius.beatinspector.views.SearchScreen
import ua.leonidius.beatinspector.views.SettingsScreen
import ua.leonidius.beatinspector.views.SongDetailsScreen

class MainActivity : ComponentActivity() {

    private val viewModel: AuthStatusViewModel by viewModels(factoryProducer = { AuthStatusViewModel.Factory })

    private lateinit var loginActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onLoginActivityResult(result.resultCode == Activity.RESULT_OK, result.data)
        }

        // viewModel.checkAuthStatus { loginActivityLauncher.launch(it) }

        setContent {
            BeatInspectorTheme {

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    val loggedIn = viewModel.uiState is AuthStatusViewModel.UiState.SuccessfulLogin

                    NavHost(navController = navController, startDestination = if (!loggedIn) "login" else "search") {
                        composable("login") {
                            if (viewModel.uiState is AuthStatusViewModel.UiState.SuccessfulLogin) {
                                // todo: performance optimization?
                                // LaunchedEffect(key1 = null, {})
                                navController.navigate("search") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                }
                            } else {
                                LoginScreen(onLoginButtonPressed = {
                                    viewModel.launchLoginSequence { loginActivityLauncher.launch(it) }
                                }, onNavigateToLegalText = { textResId ->
                                    navController.navigate("text/${textResId}")
                                })
                            }
                        }
                        composable("search") {
                            SearchScreen(
                                onNavigateToSongDetails = {
                                    navController.navigate("song/${it}")
                                }, onNavigateToSettings = {
                                    navController.navigate("settings")
                                })
                        }
                        composable("song/{songId}") {
                            SongDetailsScreen()
                        }
                        composable("text/{textId}") {
                            LongTextScreen()
                        }
                        composable("settings") {
                            SettingsScreen(onLegalDocClicked = {
                                navController.navigate("text/${it}")
                            }, onLogOutClicked = {
                                viewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("settings") {
                                        inclusive = true
                                    }
                                }
                                // todo: make Auth status observable in Authernicator
                                // and have the cache observe that?
                                (application as BeatInspectorApp).accountDataCache.clear()
                            }, onLinkClicked = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, it.toUri())
                                startActivity(browserIntent)
                            })
                        }
                    }

                }
            }
        }

    }

}
