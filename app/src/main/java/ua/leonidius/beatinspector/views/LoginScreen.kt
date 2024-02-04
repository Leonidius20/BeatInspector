package ua.leonidius.beatinspector.views

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.AuthStatusViewModel
import ua.leonidius.beatinspector.R

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginButtonPressed: () -> Unit,
    onNavigateToLegalText: (Int) -> Unit,
    viewModel: AuthStatusViewModel = viewModel(factory = AuthStatusViewModel.Factory),
) {
    when(viewModel.uiState) {
        is AuthStatusViewModel.UiState.LoginOffered -> {
            Column(
                modifier
                    .fillMaxHeight()
                    .padding(16.dp),
            ) {
                // todo: add a link to the privacy policy and terms of service
                val linkSpanStyle = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)

                val annotatedString = buildAnnotatedString {
                    append("In order to use the app, you need to log in with your Spotify account. Press \"Log in\" and follow the instructions in the browser window. By continuing, you agree to the app's ")
                    withStyle(style = linkSpanStyle) {
                        pushStringAnnotation("link", "privacy_policy")
                        append("privacy policy")
                        pop()
                    }

                    append(" and ")

                    withStyle(style = linkSpanStyle) {
                        pushStringAnnotation("link", "terms_of_service")
                        append("terms of service")
                        pop()
                    }

                    append(".")
                }

                ClickableText(
                    annotatedString,
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Justify),
                    onClick = { offset ->
                        annotatedString.getStringAnnotations("link", offset, offset)
                            .firstOrNull()?.let { annotation ->
                                onNavigateToLegalText(
                                    when (annotation.item) {
                                        // todo: this logic should be moved to the viewmodel or elsewhere
                                        "privacy_policy" -> R.string.privacy_policy
                                        "terms_of_service" -> R.string.terms_and_conditions
                                        else -> throw IllegalArgumentException("Unknown link annotation")
                                    }
                                )
                            }
                    }
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
