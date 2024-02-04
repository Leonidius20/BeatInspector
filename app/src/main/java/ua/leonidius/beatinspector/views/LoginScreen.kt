package ua.leonidius.beatinspector.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.AuthStatusViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginButtonPressed: () -> Unit,
    viewModel: AuthStatusViewModel = viewModel(factory = AuthStatusViewModel.Factory),
) {
    when(viewModel.uiState) {
        is AuthStatusViewModel.UiState.LoginOffered -> {
            Column(
                modifier
                    .fillMaxHeight()
                    .padding(16.dp),
            ) {
                Text(
                    text = "In order to use the app, you need to log in with your Spotify account. Press \"Log in\" and follow the instructions in the browser window. By continuing, you agree to the app's privacy policy and terms of service.",
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify,
                )
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onLoginButtonPressed
                ) {
                    Text(text = "Log in")
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null)
                }


            }
        }
        is AuthStatusViewModel.UiState.LoginInProgress -> {
            Column(
                modifier
                    .fillMaxHeight()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Please follow the instructions in the browser window...",
                    textAlign = TextAlign.Justify,
                )
                Spacer(modifier = Modifier.padding(16.dp))
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            }

        }
        is AuthStatusViewModel.UiState.LoginError -> {
            Column(
                modifier
                    .fillMaxHeight()
                    .padding(16.dp),
            ) {
                // todo: add actual error description based on the error type (network, user cancelled, etc)
                Text(
                    text = "There was an error during the login process. Please try again.",
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify,
                )
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onLoginButtonPressed
                ) {
                    Text(text = "Try again")
                }
            }

        }
        is AuthStatusViewModel.UiState.SuccessfulLogin -> {
            // this screen will not be shown
        }
    }



}
