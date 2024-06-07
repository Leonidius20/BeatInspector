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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikepenz.aboutlibraries.entity.License
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import ua.leonidius.beatinspector.features.details.ui.SongDetailsScreen
import ua.leonidius.beatinspector.features.home.ui.HomeScreen
import ua.leonidius.beatinspector.features.legal.ui.LongTextScreen
import ua.leonidius.beatinspector.features.login.ui.LoginScreen
import ua.leonidius.beatinspector.features.login.viewmodels.LoginViewModel
import ua.leonidius.beatinspector.features.search.ui.SearchScreen
import ua.leonidius.beatinspector.features.settings.ui.SettingsScreen
import ua.leonidius.beatinspector.features.tracklist.liked.viewmodels.LikedTracksViewModel
import ua.leonidius.beatinspector.features.tracklist.playlist.ui.PlaylistContentActions
import ua.leonidius.beatinspector.features.tracklist.playlist.ui.PlaylistContentScreen
import ua.leonidius.beatinspector.features.tracklist.recent.viewmodels.RecentlyPlayedViewModel
import ua.leonidius.beatinspector.features.tracklist.shared.ui.TrackListActionsImpl
import ua.leonidius.beatinspector.features.tracklist.shared.ui.TrackListScreen
import ua.leonidius.beatinspector.features.tracklist.top.viewmodels.TopTracksViewModel
import ua.leonidius.beatinspector.ui.theme.BeatInspectorTheme
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @Named("spotify_installed_flow")
    lateinit var isSpotifyInstalledFlow: StateFlow<Boolean>

    @Inject
    lateinit var licenses: Set<License> // todo: remove and place somewhere else, probably some viewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()

        val loginViewModel: LoginViewModel by viewModels()

        // todo: do it somewhere else using the @ActivityContext hilt injection
        val loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            loginViewModel.onLoginActivityResult(result.resultCode == Activity.RESULT_OK, result.data)
        }


        val openTrackOnSpotifyOrAppStore = { songId: String ->
            val uri = if (isSpotifyInstalledFlow.value)
                "spotify:track:$songId"
            else
                "market://details?id=com.spotify.music"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        val openPlaylistInAppOrAppStore = { playlistUri: String ->
            val uri = if (isSpotifyInstalledFlow.value)
                playlistUri
            else
                "market://details?id=com.spotify.music"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }



        val startDestination =
            if (mainViewModel.isLoggedIn())
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
                            LongTextScreen(text = licenses.find { it.hash == licenseHash }!!.licenseContent!!)
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
                                // (application as BeatInspectorApp).accountDataCache.clear()
                            }, onLinkClicked = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, it.toUri())
                                startActivity(browserIntent)
                            }, onLicenseClicked = { licenseHash ->
                                navController.navigate("license/${licenseHash}")
                            })
                        }

                        val trackListActions = TrackListActionsImpl(
                            openSongInSpotify = openTrackOnSpotifyOrAppStore,
                            goToSongDetails = { navController.navigate("song/${it}") }
                        )

                        composable("saved_tracks") {
                            TrackListScreen(
                                hiltViewModel<LikedTracksViewModel>(),
                                trackListActions,
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
                                hiltViewModel<RecentlyPlayedViewModel>(),
                                trackListActions,
                            )
                        }

                        composable("playlist/{playlistId}") {
                            val playlistContentActions = PlaylistContentActions(
                                openSongInSpotify = trackListActions.openSongInSpotify,
                                goToSongDetails = trackListActions.goToSongDetails,
                                openPlaylistInSpotify = openPlaylistInAppOrAppStore
                            )

                            PlaylistContentScreen(
                                playlistContentActions,
                                isSpotifyInstalledFlow,
                            )
                        }
                        composable("top_tracks") {
                            TrackListScreen(
                                hiltViewModel<TopTracksViewModel>(),
                                trackListActions,
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


