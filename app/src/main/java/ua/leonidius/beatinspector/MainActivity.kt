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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.leonidius.beatinspector.features.login.ui.LoginScreen
import ua.leonidius.beatinspector.features.login.viewmodels.LoginViewModel
import ua.leonidius.beatinspector.data.auth.LoginState
import ua.leonidius.beatinspector.features.settings.ui.SettingsScreen
import ua.leonidius.beatinspector.ui.theme.BeatInspectorTheme
import ua.leonidius.beatinspector.features.tracklist.viewmodels.PlaylistContentViewModel
import ua.leonidius.beatinspector.features.tracklist.viewmodels.TrackListViewModel
import ua.leonidius.beatinspector.features.legal.ui.LongTextScreen
import ua.leonidius.beatinspector.features.details.ui.OpenInSpotifyButton
import ua.leonidius.beatinspector.features.home.ui.HomeScreen
import ua.leonidius.beatinspector.features.search.ui.SearchScreen
import ua.leonidius.beatinspector.features.details.ui.SongDetailsScreen
import ua.leonidius.beatinspector.features.tracklist.ui.TrackListScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()

        val loginViewModel: LoginViewModel by viewModels()

        val loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            loginViewModel.onLoginActivityResult(result.resultCode == Activity.RESULT_OK, result.data)
        }

        val app = application as BeatInspectorApp // todo: remove?

        val openTrackOnSpotifyOrAppStore = { songId: String ->
            val uri = if (app.isSpotifyInstalled)
                "spotify:track:$songId"
            else
                "market://details?id=com.spotify.music"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        val openPlaylistInAppOrAppStore = { playlistUri: String ->
            val uri = if (app.isSpotifyInstalled)
                playlistUri
            else
                "market://details?id=com.spotify.music"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }



        val startDestination =
            if (mainViewModel.authState.value is LoginState.LoggedIn)
                "playlists"
            else "login"


        setContent {
            BeatInspectorTheme {

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    val goTo = remember {
                        { route: String -> navController.navigate(route) }
                    }

                    /*val startDestination = remember {
                        if (viewModel.uiState !is AuthStatusViewModel.UiState.SuccessfulLogin) "login" else "playlists"
                    }*/

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            if (loginViewModel.uiState is LoginViewModel.UiState.SuccessfulLogin) {

                                LaunchedEffect(key1 = null) {
                                    navController.navigate("playlists") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                }

                            } else {
                                LoginScreen(viewModel = loginViewModel, onLoginButtonPressed = {
                                    // for some reason, UI is only recomposed when we use the viewmodel from the argument

                                    loginViewModel.launchLoginSequence { loginActivityLauncher.launch(it) }
                                }, onNavigateToLegalText = { textResId ->
                                    navController.navigate("text/${textResId}")
                                })
                            }
                        }
                        composable( "search/{query}") {
                            SearchScreen(
                                onNavigateToSongDetails = {
                                    goTo("song/${it}")
                                }, onNavigateToSettings = {
                                    goTo("settings")
                                },
                                onOpenSongInSpotify = openTrackOnSpotifyOrAppStore,
                                onOpenSavedTracks = {
                                    goTo("saved_tracks")
                                }
                            )
                        }
                        composable("song/{songId}") {
                            SongDetailsScreen(
                                onOpenInSpotifyButtonClick = openTrackOnSpotifyOrAppStore)
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
                                loginViewModel.logout()

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
                        composable("saved_tracks") {
                            TrackListScreen(
                                viewModel(factory = TrackListViewModel.SavedTracksFactory),
                                onOpenSongInSpotify = openTrackOnSpotifyOrAppStore,
                                onNavigateToSongDetails = {
                                    navController.navigate("song/${it}")
                                }
                            )
                        }
                        composable(Screen.Playlists.routeTemplate) {
                            HomeScreen(
                                onSearch = { goTo(Screen.Search.route(it)) },
                                goToSettings = { goTo(Screen.Settings.route()) },
                                goToSavedTracks = { goTo("saved_tracks") },
                                goToRecentlyPlayed = { goTo("recently_played") },
                                goToPlaylist = { goTo("playlist/${it}") },
                                goToTopTracks = { goTo("top_tracks") },
                                openPlaylistInApp = openPlaylistInAppOrAppStore,
                            )
                        }
                        composable("recently_played") {
                            TrackListScreen(
                                viewModel(factory = TrackListViewModel.RecentlyPlayedFactory),
                                onOpenSongInSpotify = openTrackOnSpotifyOrAppStore,
                                onNavigateToSongDetails = {
                                    navController.navigate("song/${it}")
                                }
                            )
                        }
                        composable("playlist/{playlistId}") {
                            val model: PlaylistContentViewModel = viewModel(factory = PlaylistContentViewModel.Factory)
                            TrackListScreen(
                                model,
                                onOpenSongInSpotify = openTrackOnSpotifyOrAppStore,
                                onNavigateToSongDetails = {
                                    navController.navigate("song/${it}")
                                },
                                headerContent = {
                                    // todo: remove this shit from here

                                    when (val state = model.uiState) {
                                        is PlaylistContentViewModel.UiState.Loading -> {
                                            // nothing
                                        }
                                        is PlaylistContentViewModel.UiState.Loaded -> {
                                            Box(Modifier.fillMaxWidth()) {
                                                OpenInSpotifyButton(
                                                    modifier = Modifier
                                                        .padding(Dimens.paddingNormal)
                                                        .align(Alignment.Center),
                                                    onClick = { openPlaylistInAppOrAppStore(state.uri) },
                                                    isSpotifyInstalled = app.isSpotifyInstalled,
                                                    colors = ButtonDefaults.buttonColors().copy(
                                                        containerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.5f),
                                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                                    ) //todo  temorary until can extract color from playlist img
                                                )
                                            }
                                        }
                                    }

                                }
                            )
                        }
                        composable("top_tracks") {
                            TrackListScreen(
                                viewModel(factory = TrackListViewModel.TopTracksFactory),
                                onOpenSongInSpotify = openTrackOnSpotifyOrAppStore,
                                onNavigateToSongDetails = {
                                    navController.navigate("song/${it}")
                                },
                            )
                        }
                    }

                }
            }
        }

    }



    sealed class Screen(
        val routeTemplate: String,
    ) {
        override fun toString() = routeTemplate

        sealed class NoParametersScreen(routeTemplate: String): Screen(routeTemplate) {
            fun route() = routeTemplate
        }

        sealed class ScreenWithParameters(
            route: String,
            parameterNames: Array<String>,
            parameterValues: Array<Any>
        )

        object Playlists: NoParametersScreen("playlists")

        object Search: ScreenWithParameters("search/{query}", arrayOf(), arrayOf("")) {

            val parameterQuery = "query"

            fun route(query: String) = "search/$query"

        }

        object Settings: NoParametersScreen("settings")

        object SavedTracks: NoParametersScreen("saved_tracks")

        object SongDetails: Screen("song/{songId}") {

            fun route(id: String) = "song/$id"

        }

        object License: Screen("license/{licenseHash}") {

            fun route(licenseHash: String) = "license/$licenseHash"

        }
    }

}


