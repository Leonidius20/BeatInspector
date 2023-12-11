package ua.leonidius.beatinspector

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.leonidius.beatinspector.ui.theme.BeatInspectorTheme
import ua.leonidius.beatinspector.views.SearchScreen
import ua.leonidius.beatinspector.views.SongDetailsScreen

class MainActivity : ComponentActivity() {

    private val viewModel: AuthStatusViewModel by viewModels(factoryProducer = { AuthStatusViewModel.Factory })

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo: if authed = false, do the auth stuff and show placeholder
        // val viewModel: AuthStatusViewModel by viewModels()

        if (!viewModel.isLoggedIn) {
            viewModel.initiateLogin(this)
        }

        setContent {
            BeatInspectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    if (viewModel.isLoggedIn) {
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


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) { // todo: get constant outta here (RC_AUTH)
            viewModel.authenticator.onResponse(this, data) { isSuccessful ->
                Toast.makeText(this, "success: $isSuccessful", Toast.LENGTH_LONG).show()
                viewModel.setLoggedIn(isSuccessful)
            } // todo: move to viewmodel
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
