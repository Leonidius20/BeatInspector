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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.leonidius.beatinspector.ui.theme.BeatInspectorTheme
import ua.leonidius.beatinspector.views.SearchScreen
import ua.leonidius.beatinspector.views.SongDetailsScreen

class MainActivity : ComponentActivity() {

    private val viewModel: AuthStatusViewModel by viewModels(factoryProducer = { AuthStatusViewModel.Factory })

    private lateinit var loginActivityLauncher: ActivityResultLauncher<Intent>

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onLoginActivityResult(result.resultCode == Activity.RESULT_OK, result.data)
        }

        viewModel.checkAuthStatus { loginActivityLauncher.launch(it) }

        setContent {
            BeatInspectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    if (viewModel.uiState.isLoggedIn) {
                        val navController = rememberNavController()

                        NavHost(navController = navController, startDestination = "search") {
                            composable("search") {
                                SearchScreen(onNavigateToSongDetails = { // todo: only send ID of the track to the other screen
                                    navController.navigate("song/${it}")
                                })
                            }
                            composable("song/{songId}") { backStackEntry ->
                                val songId = backStackEntry.arguments?.getString("songId")
                                SongDetailsScreen(songId = songId)
                            }
                        }
                    } else {
                        Text(text = "Auth required. Follow instructions in browser window")
                    }
                }
            }
        }

    }

}
