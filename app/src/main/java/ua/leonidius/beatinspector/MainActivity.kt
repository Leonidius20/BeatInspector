package ua.leonidius.beatinspector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AuthStatusViewModel by viewModels(factoryProducer = { AuthStatusViewModel.Factory })

        val loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onLoginActivityResult(result.resultCode == Activity.RESULT_OK, result.data)
        }

        val app = application as BeatInspectorApp // todo: remove?

        setContent {
            BeatInspectorTheme {

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    val startDestination = remember {
                        if (viewModel.uiState !is AuthStatusViewModel.UiState.SuccessfulLogin) "login" else "search"
                    }

                    NavHost(navController = navController, startDestination = startDestination) {
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
                                LoginScreen(viewModel = viewModel, onLoginButtonPressed = {
                                    // for some reason, UI is only recomposed when we use the viewmodel from the argument

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
                            val textId = it.arguments?.getString("textId")!!
                            LongTextScreen(textId = textId.toInt())
                        }
                        composable("license/{licenseHash}") {
                            val licenseHash = it.arguments?.getString("licenseHash")!!
                            LongTextScreen(text = app.licenses.find { it.hash == licenseHash }!!.licenseContent!!)
                        }
                        composable("settings") {
                            SettingsScreen(onLegalDocClicked = {
                                navController.navigate("text/${it}")
                            }, onLogOutClicked = {
                                viewModel.logout()

                                // open the Spotify Manage Apps page
                                // todo: is it possible to only redirect to login if the tab is closed?
                                CustomTabsIntent.Builder()
                                    .build().launchUrl(this@MainActivity, Uri.parse("https://www.spotify.com/account/apps/"))

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
                            }, onLicenseClicked = { licenseHash ->
                                navController.navigate("license/${licenseHash}")
                            })
                        }
                    }

                }
            }
        }

    }

}
