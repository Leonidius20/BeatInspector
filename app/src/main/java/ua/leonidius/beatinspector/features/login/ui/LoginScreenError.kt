package ua.leonidius.beatinspector.features.login.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.features.shared.ui.CenteredScrollableTextScreen

@Composable
fun LoginErrorScreen(
    onLoginButtonPressed: () -> Unit,
) {
    CenteredScrollableTextScreen {
        // todo: add actual error description based on the error type (network, user cancelled, etc)
        // todo: maybe replace with universal error screen, although this case is different, bc here login can be cancelled by user, which is unique
        TextBlock(
            text = stringResource(R.string.log_in_error),
        )
        CenteredButton(
            onClick = onLoginButtonPressed
        ) {
            Text(text = stringResource(R.string.login_screen_try_again_button))
        }
    }
}